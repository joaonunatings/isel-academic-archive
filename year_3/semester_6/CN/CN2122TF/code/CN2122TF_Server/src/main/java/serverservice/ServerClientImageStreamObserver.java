package serverservice;

import io.grpc.stub.StreamObserver;
import rpcstubs.ImageRequestReply;
import rpcstubs.SubmitRequestReply;
import serverservice.utils.PubSubOperations;
import serverservice.utils.PubSubWorkMessage;
import serverservice.utils.StorageOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ServerClientImageStreamObserver implements StreamObserver<ImageRequestReply> {
    private final String PATH = "/var/cn2122tf_server/files/submitedImages/";
    private final String TOPIC_ID = "detectionworkers";
    private final String PROJECT_ID;
    private final String BUCKET_NAME = "cn-2022-g06";
    private final String BLOB_FOLDER_NAME = "images/";
    private String imageFileName = null;
    private Logger logger;

    StreamObserver<SubmitRequestReply> streamFinalReply;

    private List<byte[]> arr = new ArrayList<>();

    public ServerClientImageStreamObserver(StreamObserver<SubmitRequestReply> streamFinalReply, String projectId, Logger logger) {
        this.streamFinalReply = streamFinalReply;
        this.PROJECT_ID = projectId;
        this.logger = logger;
    }

    // should write each block directly to file
    @Override
    public void onNext(ImageRequestReply imageRequestReply) {
        if (imageFileName == null) imageFileName = imageRequestReply.getName();
        arr.add(imageRequestReply.getByteBlock().toByteArray());
    }

    @Override
    public void onError(Throwable throwable) {
        logger.severe("Error: " + throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        StorageOperations so = new StorageOperations();
        PubSubOperations pbo = new PubSubOperations(PROJECT_ID);
        try {
            File submitedImage = new File(PATH+imageFileName);
            try (FileOutputStream outputStream = new FileOutputStream(submitedImage)) {
               for(var barray : arr) {
                   outputStream.write(barray);
               }
            }

            String blobName = BLOB_FOLDER_NAME + imageFileName;
            String requestId = BUCKET_NAME +"-"+ imageFileName;
            so.uploadFileToBucket(BUCKET_NAME, blobName,PATH + imageFileName);
            pbo.publishMessage(TOPIC_ID,new PubSubWorkMessage(requestId,BUCKET_NAME,blobName));

            SubmitRequestReply reply = SubmitRequestReply.newBuilder().setOperID(requestId).build();
            streamFinalReply.onNext(reply);
            streamFinalReply.onCompleted();
            if(!submitedImage.delete()) logger.severe("Could not delete server saved file:"+PATH+imageFileName+" .");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            streamFinalReply.onError(e);
            e.printStackTrace();
        }
    }
}
