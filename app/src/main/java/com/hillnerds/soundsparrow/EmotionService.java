package com.hillnerds.soundsparrow;

/**
 * Created by mayan on 28/01/2017.
 */

import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/** GetEmotion Class */

@SuppressWarnings("deprecation")
public class EmotionService extends AsyncTask<String, Integer, String> {

    private static final String storageURL = "https://soundsparrow.blob.core.windows.net/sparrow";
    private static final String storageContainer = "sparrow";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=soundsparrow;AccountKey=jZzZsr8d9TCaX/8lrIWkoZ5My9AY08nX5XHrr96rnWsG0yXl6vmp7iwFpzMK+EFPM75BNLNajEPyMnEgQDXsbg==";
    private static final String emotionPrimaryKey = "d0372ad59f6141889b33032fb742e04f";
    private static final String imageName = "face.jpg";

    @Override
    protected String doInBackground(String... params) {

        //Store image in blob storage
        storeImageInBlobStorage(params[0]);

        //Get the happiness score from API
        String result = getEmotionScore();

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        //COMMENTED OUT FOR TESTING
        //double happiness = Double.parseDouble(result);
    }

    protected void storeImageInBlobStorage(String imgPath){
        try
        {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the blob client.
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Retrieve reference to a previously created container.
            CloudBlobContainer container = blobClient.getContainerReference(storageContainer);

            // Create or overwrite the "face.jpeg" blob with contents from a local file.
            CloudBlockBlob blob = container.getBlockBlobReference(imageName);
            File source = new File(imgPath + '/' + imageName);
            blob.upload(new FileInputStream(source), source.length());
        }
        catch (Exception e)
        {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    protected String getEmotionScore(){

        HttpClient httpclient = HttpClients.createDefault();
        System.out.println("Starting now");
        String r = "";
        try
        {
            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");


            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", emotionPrimaryKey);

            System.out.println("Before requesting the body");
            // Request body
            //StringEntity reqEntity = new StringEntity("{body}");
            StringEntity reqEntity = new StringEntity("{ \"url\": \"" + storageURL + "/" + "face.jpg" + "\" }");
            request.setEntity(reqEntity);

            System.out.println("After requesting the body");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println("Before checking for entity and printing");
            if (entity != null)
            {
                r = EntityUtils.toString(entity);
                System.out.println(r);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        Gson gson = new Gson();

        System.out.println(
//                gson.fromJson(r,mayank.class)

        );
        return r;
    }

}