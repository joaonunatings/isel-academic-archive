package serverservice.utils;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

public class PubSubOperations {

    static String PROJECT_ID;
    static final String[] SCHEMA_PROPERTIES = {"request_id", "bucket_name", "blob_name"};

    public PubSubOperations(String projectId) {
        PROJECT_ID = projectId;
    }

    public void publishMessage(String pubTopicName, PubSubWorkMessage msg) throws Exception {
        TopicName topic = TopicName.ofProjectTopicName(PROJECT_ID, pubTopicName);
        Publisher publisher = Publisher.newBuilder(topic).build();

        PubsubMessage pubsubMessage = encodeMessage(msg);
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        String msgID = future.get();
        System.out.println("Message Published with ID=" + msgID);
        publisher.shutdown();
    }
    //
    private PubsubMessage encodeMessage(PubSubWorkMessage message) {
        JsonObject jsonObj = new JsonObject();
        // TODO: Make this not hardcoded
        jsonObj.addProperty(SCHEMA_PROPERTIES[0], message.REQUEST_ID);
        jsonObj.addProperty(SCHEMA_PROPERTIES[1], message.BUCKET_NAME);
        jsonObj.addProperty(SCHEMA_PROPERTIES[2], message.BLOB_NAME);
        String jsonString = jsonObj.toString();

        PubsubMessage.Builder pubSubMessage = PubsubMessage.newBuilder();
        pubSubMessage.setData(ByteString.copyFromUtf8(jsonString));
        return pubSubMessage.build();
    }
}
