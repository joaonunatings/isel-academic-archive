package serverservice.utils;

public class PubSubWorkMessage {

    final String REQUEST_ID;
    final String BUCKET_NAME;
    final String BLOB_NAME;

    public PubSubWorkMessage(String requestId,
                             String bucketName,
                             String blobName) {
        this.REQUEST_ID = requestId;
        this.BUCKET_NAME = bucketName;
        this.BLOB_NAME = blobName;
    }
}

/*
{
    "request_id":"ID",
    "bucket_name":"name1",
    "blob_name":"name2"
}
*/