import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rpcstubs.*;
import utils.ImageObject;
import utils.SubmitReply;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Client {

    private static final int READ_BLOCK_SIZE = 4096;
    private static final String PROCESSED_IMAGES_PATH = "received_images/";
    private static final String LOOKUP_URL = "https://europe-west1-cn2122-t2-g06.cloudfunctions.net/look-up-function-public?instanceGroupName=";

    private static String svcIP = "localhost";
    private static int svcPort = 8000;

    private static String instanceGroupName = "server-instance-group";

    private static ManagedChannel channel;
    private static ServerServiceGrpc.ServerServiceBlockingStub blockingStub;
    private static ServerServiceGrpc.ServerServiceStub noBlockStub;
    private static ServerServiceGrpc.ServerServiceFutureStub futStub;

    private static List<SubmitReply> requests = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage:\njava -jar <jar> [serviceIP] [servicePORT]");
        }
        readArguments(args);
        channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                .usePlaintext()
                .build();
        while(true) {
            printMenu();
            clearConsole();
        }
    }

    private static void readArguments(String[] args) {
        if (args.length == 2) {
            svcIP = args[0];
            svcPort = Integer.parseInt(args[1]);
        } else {
            getRandomServerIp(instanceGroupName);
        }
        System.out.println("Connecting to " + svcIP + ":" + svcPort + " in " + instanceGroupName);
    }

    private static void getRandomServerIp(String instanceGroupName) {
        final String url = LOOKUP_URL + instanceGroupName;
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<String> ips = new ArrayList<>(Arrays.asList(response.body().split(" ")));
                Random rand = new Random();
                int randIndex = rand.nextInt(ips.size());
                svcIP = ips.get(randIndex);
            } else {
                //TODO: Error
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO: Save the submitted requests ids so the client can consult them

    // Function to clear console
    public static void clearConsole() {
        for(int i = 0; i < 100; i++)
            System.out.println();
    }

    // Method that prints a console menu and let user choose options using Scanner
    private static void printMenu() {
        int opt = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n\n\n\n");
        System.out.println("1. Submit Image");
        System.out.println("2. Get objects list found in submitted request");
        System.out.println("3. Get processed image from submitted request");
        System.out.println("4. Search for images in system");
        System.out.println("5. Exit");
        System.out.println("\n\n");
        System.out.print("Choose an option: ");
        String input = sc.nextLine();
        if(!input.isEmpty()) opt = Integer.parseInt(input);

        switch (opt) {
            case 1: submitImage(); break;
            case 2: getObjectsFromRequest(); break;
            case 3: getProcessedImage(); break;
            case 4: searchForImagesInTimeLine(); break;
            case 5: System.out.println("Client exiting."); System.exit(0); break;
            default: System.out.println("Invalid option");
        }
        sc.nextLine();
    }

    private static void submitImage() {
        String path = readFromConsole("Image file path?");
        String imageName = readFromConsole("Image file name?");
        final SubmitReply[] reply = new SubmitReply[1];
        try {
            noBlockStub = ServerServiceGrpc.newStub(channel);
            StreamObserver<SubmitRequestReply> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(SubmitRequestReply submitRequestReply) {
                    reply[0] = new SubmitReply(submitRequestReply.getOperID());
                    requests.add(reply[0]);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("Error: occurred while trying to submit image file - " + imageName);
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Request with ID: " + reply[0].REQUEST_ID + " as been submitted.\n");
                }
            };
            StreamObserver<ImageRequestReply> n_Reqs = noBlockStub.submitImageFile(responseObserver);

            byte[] myBuffer = new byte[READ_BLOCK_SIZE];
            int bytesRead;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
            while ((bytesRead = in.read(myBuffer,0,READ_BLOCK_SIZE)) != -1) {
                if (bytesRead < READ_BLOCK_SIZE) {
                    byte[] arr = Arrays.copyOf(myBuffer, bytesRead);
                    n_Reqs.onNext(ImageRequestReply.newBuilder()
                            .setByteBlock(ByteString.copyFrom(arr))
                            .setName(imageName)
                            .build()
                    );
                    break;
                }
                n_Reqs.onNext(ImageRequestReply.newBuilder()
                        .setByteBlock(ByteString.copyFrom(myBuffer))
                        .setName(imageName)
                        .build()
                );
            }
            n_Reqs.onCompleted();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private static void getObjectsFromRequest() {
        String requestID = readFromConsole("Request ID to be searched?");
        List<ImageObject> objsList = new ArrayList<>();
        noBlockStub = ServerServiceGrpc.newStub(channel);
        StreamObserver<ResultObjects> streamObjs = new StreamObserver<>() {
            @Override
            public void onNext(ResultObjects resultObjects) {
                objsList.add(new ImageObject(resultObjects.getObjName(), resultObjects.getCertaintyGrade()));
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: While searching for objects of submitted request: " + requestID);
                System.out.println("Found the following objects: \n" + objsList);
            }

            @Override
            public void onCompleted() {
                System.out.println("Found the following objects: \n" + objsList);
            }
        };

        noBlockStub.getResultObjects(SubmitRequestReply.newBuilder().setOperID(requestID).build(),streamObjs);
    }

    private static void getProcessedImage() {
        String requestID = readFromConsole("Request ID to be searched?");
        final String[] processedImageName = {null};
        final FileOutputStream[] outputStream = {null};
        noBlockStub = ServerServiceGrpc.newStub(channel);
        StreamObserver<ImageRequestReply> imageStream = new StreamObserver<>() {
            @Override
            public void onNext(ImageRequestReply imageRequestReply) {
                if (processedImageName[0] == null) processedImageName[0] = imageRequestReply.getName();
                if (outputStream[0] == null) {
                    try {
                        outputStream[0] = new FileOutputStream(PROCESSED_IMAGES_PATH+ processedImageName[0]);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    outputStream[0].write(imageRequestReply.getByteBlock().toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: Could not get processed image from request: " + requestID);
            }

            @Override
            public void onCompleted() {
                if (outputStream[0] == null) {
                    System.out.println("Completed: Image might not be fully processed, could not get image");
                }
                else {
                    System.out.println("Completed: Got image: "+processedImageName[0]);
                }
            }
        };
        noBlockStub.getResultImage(SubmitRequestReply.newBuilder().setOperID(requestID).build(),imageStream);
    }

    private static void searchForImagesInTimeLine() {
        String objectName = readFromConsole("Search for object with name?");
        double objectGrade = Double.parseDouble(readFromConsole("With which certainty?"));
        String startTime = readFromConsole("Start Date? <00/00/00>");
        String endTime = readFromConsole("End Date? <00/00/00>");
        //TODO: check inputs
        final List<String>[] imagesNames = new List[]{new ArrayList<>()};
        /*
        var form = new SimpleDateFormat("dd/MM/yyyy");

        Timestamp startTs = null, endTs = null;
        try {
            Date sDate = form.parse(startTime);
            Date eDate = form.parse(endTime);
            startTs = Timestamp.newBuilder().setSeconds(sDate.getTime()).build();
            endTs = Timestamp.newBuilder().setSeconds(eDate.getTime()).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startTs == null || endTs == null) return;
        */
        noBlockStub = ServerServiceGrpc.newStub(channel);

        StreamObserver<SearchResult> srStream = new StreamObserver<>() {
            @Override
            public void onNext(SearchResult searchResult) {
                imagesNames[0] = searchResult.getFileNameList();
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: failed to get image names");
            }

            @Override
            public void onCompleted() {
                if (imagesNames[0].size() == 0) {
                    System.out.println("No images were found within given parameters!");
                } else {
                    System.out.println("Found: ");
                    for(var s : imagesNames[0]) {
                        System.out.println(s+";");
                    }
                }
            }
        };

        noBlockStub.searchFilesBetween(SearchRequest.newBuilder()
                .setObjName(objectName)
                .setCertaintyGrade(objectGrade)
                .setStartTs(startTime)
                .setEndTs(endTime)
                .build(),
                srStream
        );
    }

    private static String readFromConsole(String message) {
        Scanner input = new Scanner(System.in);
        System.out.println(message);
        System.out.print(">");
        return input.nextLine();
    }
}
