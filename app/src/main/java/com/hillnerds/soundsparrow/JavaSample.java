package com.hillnerds.soundsparrow;

/**
 * Created by Mayank on 28/01/2017.
 */

import java.net.URI;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

public class JavaSample
{
    public static void emotionRecognition()
    {
        HttpClient httpclient = HttpClients.createDefault();
        System.out.println("Starting now");
        try
        {
            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");


            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "d0372ad59f6141889b33032fb742e04f");

            System.out.println("Before requesting the body");
            // Request body
            //StringEntity reqEntity = new StringEntity("{body}");
            StringEntity reqEntity = new StringEntity("{\"url\": \"https://cdn1.vox-cdn.com/uploads/chorus_asset/file/7870535/president_official_portrait_hires.jpg}");
            request.setEntity(reqEntity);

            System.out.println("After requesting the body");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println("Before checking for entity and printing");
            if (entity != null)
            {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}

