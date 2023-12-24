package com.example;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

public class LookUpFunctionHttp implements HttpFunction {
    private static final Logger logger = Logger.getLogger(LookUpFunctionHttp.class.getName());
    private static final String projectID = "cn2122-t2-g06";
    private static final String instanceZone = "europe-southwest1-a";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        BufferedWriter writer = response.getWriter();
        String name = request.getFirstQueryParameter("instanceGroupName").orElse("");
        if(name.equals("")) {
            response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST); return;
        }

        try (InstancesClient client = InstancesClient.create()) {
            StringBuilder sb = new StringBuilder(); //IPsResponse
            for (Instance instance : client.list(projectID, instanceZone).iterateAll()) {
                if (instance.getStatus().compareTo("RUNNING") == 0 && instance.getName().contains(name)) {
                    logger.info("Name: " + instance.getName() +"\n");
                    String ip = instance.getNetworkInterfaces(0).getAccessConfigs(0).getNatIP();
                    logger.info(" Last Start time: " + instance.getLastStartTimestamp()+"\n");
                    logger.info(" IP: " + ip+"\n");
                    sb.append(ip+" ");
                }
            }
            response.setStatusCode(HttpURLConnection.HTTP_OK);
            if(sb.toString().isEmpty()){
                sb.append("empty");
            } else sb.deleteCharAt(sb.length()-1); //delete last " "
            writer.write(sb.toString());
            //response.getOutputStream().write(IPsResponse.getBytes()); //alternativa ao que está acima
        } catch (Exception e){
            writer.write(e.getMessage());
        }
    }
}
