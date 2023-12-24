import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.pubsub.v1.ProjectSubscriptionName;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Worker {

    static final Logger LOGGER = Logger.getLogger(Worker.class.getName());
    static Firestore db;
    static Storage storage;
    private static String PROJECT_ID = "cn2122-t2-g06";

    private static String SUBSCRIPTION_NAME = "WorkerSubscription";
    static final String DETECTED_OBJECTS_COLLECTION = "DetectedObjects";
    static final String REQUESTS_COLLECTION = "Pub-Sub-Requests";

    // cn2122-t2-g06 WorkerSubscription
    public static void main(String[] args) {
        try {
            FileHandler fileHandler = new FileHandler("logs.log");
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println("Failed on log");
            e.printStackTrace();
            LOGGER.severe("Failed to initialize log file");
        }
        if (args.length > 0) {
            PROJECT_ID = args[0];
            if (args.length > 1) {
                SUBSCRIPTION_NAME = args[1];
            }
        } else {
            LOGGER.info("Usage: Worker [project-id] [subscription-name]");
        }
        try {
            firestoreInit(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            StorageOptions storageOptions = StorageOptions.getDefaultInstance();
            storage = storageOptions.getService();
        } catch (IOException e) {
            System.out.println("Failed to initialize operations module");
            LOGGER.severe("ERROR: Failed to initialize operations modules.\n");
        }
        // Get arguments
        String projectID = PROJECT_ID, subscriptionName = SUBSCRIPTION_NAME;
        if (args.length > 0) {
            projectID = args[0];
            if (args.length > 1) {
                subscriptionName = args[1];
            }
        }
        // Subscribe to topic
        try {
            ProjectSubscriptionName projSubscriptionName = ProjectSubscriptionName.of(projectID, subscriptionName);
            Subscriber subscriber =
                    Subscriber.newBuilder(projSubscriptionName, new MessageReceiveHandler(LOGGER))
                            .build();
            subscriber.startAsync().awaitRunning();
        } catch(Exception e) {
            LOGGER.severe("ERROR: Failed to subscribe to topic.\n");
            LOGGER.severe(e.getMessage());
        }

        LOGGER.info("Worker successfully started.");
        while(true) {}
    }

    private static void firestoreInit(String pathFileKeyJson) throws IOException {
        try {
            GoogleCredentials credentials = null;
            if (pathFileKeyJson != null) {
                InputStream serviceAccount = new FileInputStream(pathFileKeyJson);
                credentials = GoogleCredentials.fromStream(serviceAccount);
            } else {
                // use GOOGLE_APPLICATION_CREDENTIALS environment variable
                credentials = GoogleCredentials.getApplicationDefault();
            }
            FirestoreOptions options = FirestoreOptions
                    .newBuilder().setCredentials(credentials).build();
            db = options.getService();
            LOGGER.info("Firestore initialized");
        } catch (Exception e) {
            System.out.println("Error in firestoreInit");
        }

    }

}
