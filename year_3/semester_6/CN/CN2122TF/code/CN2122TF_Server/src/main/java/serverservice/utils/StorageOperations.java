package serverservice.utils;

import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 *   GOOGLE_APPLICATION_CREDENTIALS environment variable needs to be set with the private key .json
 *   for the correct usage of this class
 * */
public class StorageOperations {

    Storage storage = null;

    public StorageOperations() {
        StorageOptions storageOptions = null;
        storageOptions = StorageOptions.getDefaultInstance();
        storage = storageOptions.getService();
        String projID = storageOptions.getProjectId();
        if (projID != null) System.out.println("Storage Operations Project ID:" + projID);
        else {
            System.out.println("The environment variable GOOGLE_APPLICATION_CREDENTIA0LS isn't well defined!!");
            throw new IllegalArgumentException("The environment variable GOOGLE_APPLICATION_CREDENTIA0LS isn't well defined!!");
        }
    }

    /**
     *  Uploads a file to cloud storage and sets its permissions to public reader access
     * @param bucketName
     * @param blobName
     * @param filePath
     */
    public void shareFileToStorage(String bucketName, String blobName, String filePath) {
        try {
            uploadFileToBucket(bucketName, blobName, filePath);
            publishBlobToPublicAccess(bucketName, blobName);
        } catch (Exception e) {
            System.out.println("Error: File upload failed or its permissions were not set correctly. \n");
            e.printStackTrace();
        }
    }

    public void uploadFileToBucket(String bucketName, String blobName, String filePath) throws Exception {
        Path uploadFrom = Paths.get(filePath);
        String contentType = Files.probeContentType(uploadFrom);
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        if (Files.size(uploadFrom) > 1_000_000) {
            // When content is not available or large (1MB or more) it is recommended
            // to write it in chunks via the blob's channel writer.
            try (WriteChannel writer = storage.writer(blobInfo)) {
                byte[] buffer = new byte[1024];
                try (InputStream input = Files.newInputStream(uploadFrom)) {
                    int limit;
                    while ((limit = input.read(buffer)) >= 0) {
                        try {
                            writer.write(ByteBuffer.wrap(buffer, 0, limit));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } else {
            byte[] bytes = Files.readAllBytes(uploadFrom);
            // create the blob in one request.
            storage.create(blobInfo, bytes);
        }
        System.out.println("Blob " + blobName + " created in bucket " + bucketName);
    }

    public void downloadBlobFromBucket(String bucketName, String blobName, String filePath) throws IOException {
        Path downloadTo = Paths.get(filePath);
        Blob blob = getBlobObjFromBucket(bucketName, blobName);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            return;
        }
        PrintStream writeTo = new PrintStream(Files.newOutputStream(downloadTo));
        if (blob.getSize() < 1_000_000) {
            // Blob is small read all its content in one request
            byte[] content = blob.getContent();
            writeTo.write(content);
        } else {
            // When Blob size is big or unknown use the blob's channel reader.
            try (ReadChannel reader = blob.reader()) {
                WritableByteChannel channel = Channels.newChannel(writeTo);
                ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
                while (reader.read(bytes) > 0) {
                    bytes.flip();
                    channel.write(bytes);
                    bytes.clear();
                }
            }
        }
        writeTo.close();
        System.out.println("Blob " + blobName + " downloaded to " + downloadTo);
    }

    private void publishBlobToPublicAccess(String bucketName, String blobName) {
        Blob blob = getBlobObjFromBucket(bucketName, blobName);
        if (blob == null) {
            System.out.println("No such Blob exists !");
            return;
        }
        Acl.Entity aclEntity = Acl.User.ofAllUsers();
        Acl.Role role = Acl.Role.READER;

        Acl acl = Acl.newBuilder(aclEntity, role).build();
        blob.createAcl(acl);
    }

    public Blob getBlobObjFromBucket(String bucketName, String blobName) {
        BlobId blobId = BlobId.of(bucketName, blobName);
        return storage.get(blobId);
    }

}

