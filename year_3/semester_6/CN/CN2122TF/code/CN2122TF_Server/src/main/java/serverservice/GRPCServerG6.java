package serverservice;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import com.google.protobuf.ByteString;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import rpcstubs.*;
import serverservice.utils.StorageOperations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class GRPCServerG6 extends ServerServiceGrpc.ServerServiceImplBase {
    // TODO: Requests IDs are not unique, should add auto generated part to ID string
    // TODO: input arguments need to be checked for bad input
    // private static final String PROCESSED_FOLDER_BLOB_PATH = "processed-images";
    private static String PROJECT_ID = "cn2122-t2-g06";
    private static final Logger LOGGER = Logger.getLogger(GRPCServerG6.class.getName());

    private static final String FIRESTORE_REQUESTS_COLLECTION = "Pub-Sub-Requests";
    private static final String FIRESTORE_OBJECTS_COLLECTION = "DetectedObjects";
    private static final String STORAGE_BUCKET_NAME = "cn-2022-g06";
    private static final int BUFFER_READ_SIZE = 4 * 1024;

    private static int servicePort = 8000;
    private static GoogleCredentials gcpCredentials;
    private static Firestore firestoreDb;

    public static void main(String[] args) throws InterruptedException {
        try {
            FileHandler fileHandler = new FileHandler("logs.log");
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to initialize log file");
        }

        try {
            if (args.length > 0) {
                PROJECT_ID = args[0];
                if(args.length > 1) {
                    servicePort = Integer.parseInt(args[1]);
                }
            } else {
                LOGGER.info("Usage: [projectID] [port]");
            }
            // get credentials and init modules
            gcpCredentials = GoogleCredentials.getApplicationDefault();
            firestoreDb = FirestoreOptions.newBuilder().setCredentials(gcpCredentials).build().getService();

            io.grpc.Server svc = ServerBuilder.forPort(servicePort).addService(new GRPCServerG6()).build();
            svc.start();
            System.out.println("Project ID: " + PROJECT_ID);
            System.out.println("Server started, listening on port: "+ servicePort);
            LOGGER.info("Server started, listening on port: "+ servicePort);
            svc.awaitTermination();
            LOGGER.info("Server terminated");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    public StreamObserver<ImageRequestReply> submitImageFile(StreamObserver<SubmitRequestReply> responseObserver) {
        // receive stream from client - v
        // build file - v
        // send as a blob to cloud storage - v
        // return ID bucket + blob name - v
        // send ID to topic pub/sub detectionworkers - v
        LOGGER.info("Submit image was called");
        ServerClientImageStreamObserver reqs = new ServerClientImageStreamObserver(responseObserver, PROJECT_ID, LOGGER);
        return reqs;
    }

    @Override
    public void getResultObjects(SubmitRequestReply request, StreamObserver<ResultObjects> responseObserver) {
        // get info from processed request firestore (bucket, blob name)
        // get image from storage
        // TODO: Create firestore operations class
        String requestID = request.getOperID();
        Query query = firestoreDb.collection(FIRESTORE_OBJECTS_COLLECTION).whereEqualTo("request_id",requestID);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            List<QueryDocumentSnapshot> docs = querySnapshot.get().getDocuments();
            for(DocumentSnapshot doc : docs) {
                if (doc != null) {
                    ResultObjects res = ResultObjects.newBuilder()
                            .setObjName(Objects.requireNonNull(doc.get("obj_name", String.class)))
                            .setCertaintyGrade(Objects.requireNonNull(doc.get("object_certainty_grade", Double.class)))
                            .build();
                    responseObserver.onNext(res);
                }
            }
            responseObserver.onCompleted();
        } catch (InterruptedException | ExecutionException e) {
            responseObserver.onError(e);
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    public void getResultImage(SubmitRequestReply request, StreamObserver<ImageRequestReply> responseObserver) {
        String requestID = request.getOperID();
        String blob_name = null;
        Query query = firestoreDb.collection(FIRESTORE_REQUESTS_COLLECTION).whereEqualTo("request_id",requestID);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            List<QueryDocumentSnapshot> docs = querySnapshot.get().getDocuments();
            for(DocumentSnapshot doc : docs) {
               blob_name = doc.get("processed_blob_name", String.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        if (blob_name != null && !blob_name.equals("")) {
            StorageOperations so = new StorageOperations();
            Blob b = so.getBlobObjFromBucket(STORAGE_BUCKET_NAME, blob_name);
            String name = blob_name.split("/")[1];
            if (b.getSize() < 1_000_000) {
                // Blob is small read all its content in one request
                byte[] content = b.getContent();
                responseObserver.onNext(ImageRequestReply.newBuilder()
                        .setByteBlock(ByteString.copyFrom(content))
                        .setName(name)
                        .build()
                );
            } else {
                // When Blob size is big or unknown use the blob's channel reader.
                try (ReadChannel reader = b.reader()) {
                    ByteBuffer bytes = ByteBuffer.allocate(BUFFER_READ_SIZE);
                    int bRead = 0;
                    while ((bRead = reader.read(bytes)) > 0) {
                        bytes.flip();
                        if (bRead < BUFFER_READ_SIZE) {
                            ByteBuffer lastBytes = ByteBuffer.allocate(bRead);
                            for (int i = 0; i < bRead; i++) {
                                lastBytes.put(bytes.get(i));
                            }
                            bytes = lastBytes;
                        }
                        responseObserver.onNext(ImageRequestReply.newBuilder()
                                .setByteBlock(ByteString.copyFrom(bytes))
                                .setName(name)
                                .build()
                        );
                        bytes.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.severe(e.getMessage());
                }
            }
            responseObserver.onCompleted();
        } else {
            // error
            LOGGER.info("Submitted image from request: "+requestID+" : its not processed");
            responseObserver.onCompleted();
        }
    }

    @Override
    public void searchFilesBetween(SearchRequest request, StreamObserver<SearchResult> responseObserver) {
        List<String> processIds = new ArrayList<>();
        List<String> imageNames = new ArrayList<>();
        Query query = firestoreDb.collection(FIRESTORE_OBJECTS_COLLECTION)
                .whereGreaterThanOrEqualTo("object_certainty_grade",request.getCertaintyGrade())
                .whereEqualTo("obj_name",request.getObjName());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            List<QueryDocumentSnapshot> docs = querySnapshot.get().getDocuments();
            if (docs.size() == 0) {
                responseObserver.onNext(SearchResult.newBuilder().addAllFileName(imageNames).build());
                responseObserver.onCompleted();
                return;
            }
            for (DocumentSnapshot doc : docs) {
                if (doc != null) {
                    processIds.add(doc.get("request_id",String.class));
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            responseObserver.onError(e);
            e.printStackTrace();
        }
        Timestamp startTs = null, endTs = null;
        try {
            startTs = parseTimestamp(request.getStartTs());
            endTs = parseTimestamp(request.getEndTs());

            if (startTs == null || endTs == null) throw new Exception("Start and/or End dates cant be null.");
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            responseObserver.onError(e);
            return;
        }

        Query query2 = firestoreDb.collection(FIRESTORE_REQUESTS_COLLECTION)
                .whereIn("request_id",processIds);
                //.whereGreaterThanOrEqualTo("process_date",request.getStartTs());
                //.whereLessThanOrEqualTo("process_date", request.getEndTs().getSeconds());
        ApiFuture<QuerySnapshot> querySnapshot2 = query2.get();
        try {
            List<QueryDocumentSnapshot> docs = querySnapshot2.get().getDocuments();
            for (DocumentSnapshot doc : docs) {
                if (doc != null) {
                    Timestamp processDate = (Timestamp)doc.get("process_date");
                    if (processDate != null) {
                        if (processDate.getSeconds() > startTs.getSeconds()
                                && processDate.getSeconds() <= endTs.getSeconds()) {
                            String name = doc.get("processed_blob_name",String.class);
                            if (name != null) {
                                System.out.println(name);
                                String[] split = name.split("/");
                                imageNames.add(split[split.length-1]);
                            }
                        }
                    }
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.severe(e.getMessage());
            responseObserver.onError(e);
            e.printStackTrace();
            return;
        }

        responseObserver.onNext(SearchResult.newBuilder().addAllFileName(imageNames).build());
        responseObserver.onCompleted();
    }

    private Timestamp parseTimestamp(String dateStr) {
        var form = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = form.parse(dateStr);
            return Timestamp.of(date);
        } catch (ParseException e) {
            LOGGER.severe("Error while parsing timestamp");
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
