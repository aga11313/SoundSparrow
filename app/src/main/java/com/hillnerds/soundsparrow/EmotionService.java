package com.hillnerds.soundsparrow;

/**
 * Created by mayan on 28/01/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
import java.text.MessageFormat;
import java.util.UUID;

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
    private Context ctx;
    private UUID deviceUUID;

    public EmotionService(Context c) {
        this.ctx = c;
    }

    /**
     * The function called on .execute(), stores the image provided in Azure storage and sends it
     * to the Microsoft Cognitive Services API
     * @param params A String that contains the full path to the image file that you want analysed.
     * @return       A String representing the current emotion of the user in the photo.
     */
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
        Log.d("EmotionService", result);
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

            deviceUUID = BluetoothHelper.getDeviceUUID(ctx);
            // Create or overwrite the "face.jpeg" blob with contents from a local file.
            CloudBlockBlob blob = container.getBlockBlobReference(deviceUUID.toString());
            File source = new File(imgPath);
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
        String r = "";
        try {
            URIBuilder builder = new URIBuilder(
                    "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", emotionPrimaryKey);

            // Request body
            StringEntity reqEntity = new StringEntity(
                    "{ \"url\": \"" + storageURL + "/" + deviceUUID.toString() + "\" }");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                r = EntityUtils.toString(entity);
                Log.d("EmotionService", MessageFormat.format("Response received\n{0}",
                        r));
            }
        } catch (Exception e) {
            Log.w("EmotionService", MessageFormat.format("Exception encountered\n{0}",
                    e.getMessage()));
        }

        return r;
    }

}