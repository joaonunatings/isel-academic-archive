import com.google.api.core.ApiFuture;
import com.google.cloud.ReadChannel;
import com.google.cloud.Timestamp;
import com.google.cloud.WriteChannel;
import com.google.cloud.firestore.*;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.pubsub.v1.PubsubMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

//TODO: Should add capability to receive both JSON and Binary encoding

public class MessageReceiveHandler implements MessageReceiver {

    private static Logger logger;

    public MessageReceiveHandler(Logger logger) {
        MessageReceiveHandler.logger = logger;
    }

    private static class Request {
        final String request_id;
        final String bucket_name;
        final String blob_name;

        public Request(String request_id, String bucket_name, String blob_name) {
            this.request_id = request_id;
            this.bucket_name = bucket_name;
            this.blob_name = blob_name;
        }
    }

    @Override
    public void receiveMessage(PubsubMessage pubsubMessage, AckReplyConsumer ackReplyConsumer) {
        // Get the schema encoding type.
        String encoding = pubsubMessage.getAttributesMap().get("googclient_schemaencoding");
        JsonElement msg = null;
        try {
            if (encoding.equals("JSON")) {
                msg = JsonParser.parseString(pubsubMessage.getData().toStringUtf8());
                logger.info("Received a JSON-formatted message:" + msg);
            } else {
                throw new Exception("Failed to decode pubsub message");
            }
        } catch (Exception e) {
            ackReplyConsumer.nack();
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
        if (msg == null) {
            ackReplyConsumer.nack();
            logger.severe("Error: Could not receive message correctly.");
            return;
        }
        JsonObject jsonObj = msg.getAsJsonObject();
        Request req = new Request(jsonObj.get("request_id").toString().split("\"")[1],
                                jsonObj.get("bucket_name").toString().split("\"")[1],
                                jsonObj.get("blob_name").toString().split("\"")[1]
        );

        // send it to vision API
        // get objects
        // send objects to FireStore
        // add annotated image blob name, and process date, to the request

        try {
            detectLocalizedObjectsGcs(Worker.storage,req.request_id,req.bucket_name,req.blob_name);
        } catch (IOException e) {
            logger.severe(e.getMessage());
            ackReplyConsumer.nack();
            e.printStackTrace();
            return;
        }
        ackReplyConsumer.ack();
    }

    public static void detectLocalizedObjectsGcs(Storage storage, String requestId, String bucketName, String blobName) throws IOException {
        // get image object  from cloud storage
        String gcsPath = "gs://" + bucketName + "/" + blobName;
        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(gcsPath).build();
        Image img = Image.newBuilder().setSource(imgSource).build();

        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder().setType(Feature.Type.OBJECT_LOCALIZATION))
                        .setImage(img)
                        .build();

        BatchAnnotateImagesRequest singleBatchRequest = BatchAnnotateImagesRequest.newBuilder()
                .addRequests(request)
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            // Perform the request
            BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(singleBatchRequest);
            List<AnnotateImageResponse> listResponses = batchResponse.getResponsesList();

            if (listResponses.isEmpty()) {
                logger.info("Empty response, no object detected.");
                return;
            }
            // get the only response
            AnnotateImageResponse response = listResponses.get(0);
            // Create document for each object found
            for (LocalizedObjectAnnotation annotation : response.getLocalizedObjectAnnotationsList()) {
                // System.out.format("Object name: %s%n", annotation.getName());
                // System.out.format("Confidence: %s%n", annotation.getScore());
                createObjectDocument(requestId, annotation.getName(), annotation.getScore());
            }
            // annotate in memory Blob image
            BufferedImage bufferImg = getBlobBufferedImage(storage, BlobId.of(bucketName, blobName));
            annotateWithObjects(bufferImg, response.getLocalizedObjectAnnotationsList());
            // save the image to a new blob in the same bucket. The name of new blob has the annotated prefix
            writeAnnotatedImage(storage, bufferImg, bucketName, "annotated-" + blobName);
            // set blob name in firestore, and processed timestamp
            updateRequest("annotated-"+blobName,Worker.REQUESTS_COLLECTION,requestId);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private static void updateRequest(String blobName, String collectionName, String request_id) {
        Query query = Worker.db.collection(collectionName).whereEqualTo("request_id",request_id);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        String id = null;
        try {
            List<QueryDocumentSnapshot> docs = querySnapshot.get().getDocuments();
            for(DocumentSnapshot doc : docs) {
                if (id == null) {
                    id = doc.getId();
                    break;
                }
            }
            if (id != null) {
                DocumentReference docRef = Worker.db.collection(collectionName).document(id);
                HashMap<String, Object> map = new HashMap<>();
                map.put("processed_blob_name",blobName);
                map.put("process_date", Timestamp.now().toDate());
                ApiFuture<WriteResult> result = docRef.update(map);
            } else {
                throw new Exception("Could not update request: "+request_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
    }

    private static void createObjectDocument(String request_id,String objName, float objCertainty) {
        CollectionReference colRef = Worker.db.collection(Worker.DETECTED_OBJECTS_COLLECTION);
        DocumentReference docRef = colRef.document();
        HashMap<String, Object> map = new HashMap<>();
        map.put("request_id",request_id);
        map.put("obj_name",objName);
        map.put("object_certainty_grade",objCertainty);
        ApiFuture<WriteResult> result = docRef.create(map);
        try {
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.severe(e.getMessage());
        }
    }

    private static void writeAnnotatedImage(Storage storage, BufferedImage bufferImg, String bucketName, String destinationBlobName) throws IOException {
        BlobInfo blobInfo = BlobInfo
                .newBuilder(BlobId.of(bucketName, destinationBlobName))
                .setContentType("image/jpeg")
                .build();
        Blob destBlob = storage.create(blobInfo);
        WriteChannel writeChannel = storage.writer(destBlob);
        OutputStream out = Channels.newOutputStream(writeChannel);
        ImageIO.write(bufferImg, "jpg", out);
        out.close();
    }

    private static BufferedImage getBlobBufferedImage(Storage storage, BlobId blobId) throws IOException {
        Blob blob = storage.get(blobId);
        if (blob == null) {
            logger.info("No such Blob exists !");
            throw new IOException("Blob <" + blobId.getName() + "> not found in bucket <" + blobId.getBucket() + ">");
        }
        ReadChannel reader = blob.reader();
        InputStream in = Channels.newInputStream(reader);
        return ImageIO.read(in);
    }

    public static void annotateWithObjects(BufferedImage img, List<LocalizedObjectAnnotation> objects) {
        for (LocalizedObjectAnnotation obj : objects) {
            annotateWithObject(img, obj);
        }
    }

    private static void annotateWithObject(BufferedImage img, LocalizedObjectAnnotation obj) {
        Graphics2D gfx = img.createGraphics();
        gfx.setFont(new Font("Arial", Font.PLAIN, 18));
        gfx.setStroke(new BasicStroke(3));
        gfx.setColor(new Color(0x00ff00));
        Polygon poly = new Polygon();
        BoundingPoly imgPoly = obj.getBoundingPoly();
        // draw object name
        gfx.drawString(obj.getName(),
                imgPoly.getNormalizedVertices(0).getX() * img.getWidth(),
                imgPoly.getNormalizedVertices(0).getY() * img.getHeight() - 3);
        // draw bounding box of object
        for (NormalizedVertex vertex : obj.getBoundingPoly().getNormalizedVerticesList()) {
            poly.addPoint((int) (img.getWidth() * vertex.getX()), (int) (img.getHeight() * vertex.getY()));
        }
        gfx.draw(poly);
    }
}
