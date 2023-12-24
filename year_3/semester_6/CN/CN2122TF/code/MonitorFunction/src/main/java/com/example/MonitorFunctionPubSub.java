package com.example;

import com.google.api.core.ApiFuture;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.compute.v1.InstanceGroupManagersClient;
import com.google.cloud.compute.v1.ListManagedInstancesInstanceGroupManagersRequest;
import com.google.cloud.compute.v1.ManagedInstance;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.firestore.*;
import com.google.cloud.functions.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import java.util.logging.Logger;

public class MonitorFunctionPubSub implements BackgroundFunction<MonitorFunctionPubSub.PubSubMessage> {
    private static final Logger logger = Logger.getLogger(MonitorFunctionPubSub.class.getName());
    private static final String PUB_SUB_REQUESTS_DOCUMENT = "Pub-Sub-Requests";
    private static final Gson gson = new Gson();
    private static final String projectID = "cn2122-t2-g06";
    private static final String instanceZone = "europe-southwest1-a";
    private static final String instanceGroupName = "worker-instance-group";
    private static Firestore firestore = initFirestore();

    @Override
    public void accept(PubSubMessage pubSubMessage, Context context) throws Exception {
        if (firestore == null) {
            logger.info("Error connecting to Firestore. Exiting function.");
            //https://cloud.google.com/functions/docs/bestpractices/retries#why_event-driven_functions_fail_to_complete
            //https://cloud.google.com/functions/docs/bestpractices/retries#set_an_end_condition_to_avoid_infinite_retry_loops
            throw new RuntimeException("Error connecting to Firestore");
        }

        Iterator<DocumentReference> iterator = firestore.collection(PUB_SUB_REQUESTS_DOCUMENT).listDocuments().iterator();
        var documentCount = 0;
        var requestsInLast60s = 0;

        Timestamp currentTimeStamp = Timestamp.parseTimestamp(context.timestamp());
        logger.info("Current time stamp="+currentTimeStamp);
        logger.info("Current time stamp in seconds="+currentTimeStamp.getSeconds());
        var fixedTimeStamp = currentTimeStamp.getSeconds()+3600l; //+1hour pq pelos vistos é obtido um tempo de UTC+0, embora nós tenhamos definido que a regiao era europe-west1 (bélgica)
        logger.info("Fixed current timestamp="+fixedTimeStamp);

        while(iterator.hasNext()){
            long currentDocTimeStampInSeconds = Long.valueOf((String) iterator.next().get().get().get("timestamp"));
            logger.info("Message timestamp in seconds="+currentDocTimeStampInSeconds);
            var diff = fixedTimeStamp-currentDocTimeStampInSeconds;
            logger.info("Diff of seconds="+diff);
            if(diff<=60) requestsInLast60s++;
            documentCount++;
        }
        logger.info("Document count in "+PUB_SUB_REQUESTS_DOCUMENT+"= "+documentCount);

        //2. Disponibilidade e elasticidade. Ponto 2:

        var ref = 0.05f; // 3pedidos/60s
        float obtainedRatio = requestsInLast60s/60f;
        int currentInstances = countManagedInstanceGroupVMs();
        logger.info("Number of running instances: " + currentInstances);
        logger.info("Requests in the last 60s: "+requestsInLast60s);
        logger.info("Obtained ratio: "+obtainedRatio);
        if(obtainedRatio>(ref+0.02f) && currentInstances<4){ //só incrementa se o numero de instancias é inferior a 4
            resizeManagedInstanceGroup(++currentInstances);
            logger.info("Number of instance groups will increase by 1");
        } else if(obtainedRatio<(ref-0.02f) && currentInstances>1){ //só decrementa se o numero de instancias é superior a 1
            resizeManagedInstanceGroup(--currentInstances);
            logger.info("Number of instance groups will decrease by 1");
        }

        //logger.info("Received data " + pubSubMessage.data); // aparece tipo: ewogICAgInJlcXVlc3RfaWQiOiJtc2...
        try {
            String data = new String(Base64.getDecoder().decode(pubSubMessage.data));
            if(data.isEmpty()) {logger.info("No data"); return; }
            else logger.info("Data received: "+data);
            logger.info("Timestamp: "+context.timestamp());

            PubSubWorkMessage pubSubWorkMessage = gson.fromJson(data, PubSubWorkMessage.class);
            logger.info("Message to store: " + pubSubWorkMessage.toString());
            putInFireStore(pubSubWorkMessage, context, String.valueOf(fixedTimeStamp));
        } catch (NullPointerException e){
            logger.info("Bad message"+e.getMessage());
        }
    }

    private static void putInFireStore(PubSubWorkMessage pubSubWorkMessage, Context context, String timestamp) {
        CollectionReference colRef = firestore.collection(PUB_SUB_REQUESTS_DOCUMENT);
        DocumentReference docRef = colRef.document(""+context.eventId()); // O message ID vem no eventID
        HashMap<String, Object> map = new HashMap<>();
        map.put("request_id", pubSubWorkMessage.request_id);
        map.put("bucket_name", pubSubWorkMessage.bucket_name);
        map.put("blob_name", pubSubWorkMessage.blob_name);
        map.put("timestamp", timestamp); //in seconds

        ApiFuture<WriteResult> result = docRef.set(map);
        try { result.get(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException e) { e.printStackTrace(); }
        logger.info("Message " + map.toString() + " was written to Firestore");
    }

    static void resizeManagedInstanceGroup(int newSize) { //Slides/09-ComputeEngine.pdf ultimo slide
        logger.info("=== Resizing instance group ===");
        InstanceGroupManagersClient managersClient = null;

        try { managersClient = InstanceGroupManagersClient.create();
        } catch (IOException e) { e.printStackTrace(); }

        OperationFuture<Operation, Operation> result = managersClient.resizeAsync(projectID, instanceZone, instanceGroupName, newSize);
        Operation oper = null;

        try { oper = result.get(); }
        catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) { e.printStackTrace(); }
        logger.info("Resizing with status " + oper.getStatus().toString());
    }

    private static Firestore initFirestore() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirestoreOptions options = FirestoreOptions.newBuilder().setCredentials(credentials).build();
            Firestore db = options.getService();
            return db;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int countManagedInstanceGroupVMs() {
        InstanceGroupManagersClient managersClient = null;
        try { managersClient = InstanceGroupManagersClient.create();
        } catch (IOException e) { e.printStackTrace(); }
        ListManagedInstancesInstanceGroupManagersRequest request = ListManagedInstancesInstanceGroupManagersRequest.newBuilder()
                        .setInstanceGroupManager(instanceGroupName)
                        .setProject(projectID)
                        .setReturnPartialSuccess(true)
                        .setZone(instanceZone)
                        .build();
        logger.info("Instances of instance group: " + instanceGroupName);
        var count = 0;
        for (ManagedInstance instance : managersClient.listManagedInstances(request).iterateAll()) {
            logger.info(instance.getInstance()+". STATUS =" + instance.getInstanceStatus());
            count++;
        }
        logger.info("Number of instances="+count);
        return count;
    }

    class PubSubMessage {
        String data ;
        Map<String, String> attributes;

        @Override
        public String toString() { return "PubSubWorkMessage { data='" + data +"' }"; }
    }

    class PubSubWorkMessage { // /schemas/image-detection-work. Schema type = "Protocol buffer"
        final String request_id;
        final String bucket_name;
        final String blob_name;

        public PubSubWorkMessage(String requestId, String bucketName, String blobName) {
            this.request_id = requestId;
            this.bucket_name = bucketName;
            this.blob_name = blobName;
        }

        @Override
        public String toString() {
            return "PubSubWorkMessage{" +
                    "request_id='" + request_id + '\'' +
                    ", bucket_name='" + bucket_name + '\'' +
                    ", blog_name='" + blob_name + '\'' +
                    '}';
        }
    }
}

