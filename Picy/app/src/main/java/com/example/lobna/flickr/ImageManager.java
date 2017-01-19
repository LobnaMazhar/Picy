package com.example.lobna.flickr;

import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Lobna on 17-Jan-17.
 */

public class ImageManager {
    private static ImageManager imageManager;

    private ArrayList<Image> images;

    private String imageTag;

    Handler handler;

    public static ImageManager getInstance() {
        if (imageManager == null) {
            imageManager = new ImageManager();
            imageManager.handler = new Handler();
        }
        return imageManager;
    }

    public void getImages(final String tag, final ImageListener imageListener) {
        this.imageTag = tag;
        if (!Utilities.networkConnectivity()) {
            imageListener.onFail(new Exception("No internet connection!"));
        } else {
            Toast.makeText(PiccyApplication.getPiccyApp().getApplicationContext(), "Searching ... " + tag, Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;

                    BufferedReader bufferedReader = null;

                    final String imageJSONStr;

                    try {
                        String baseURL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&safe_search=for%20safe&extras=url_l&per_page=20&format=json&nojsoncallback=1";
                        Uri builtUri = Uri.parse(baseURL).buildUpon().appendQueryParameter("api_key", BuildConfig.API_KEY).appendQueryParameter("tags", tag).build();
                        URL url = new URL(builtUri.toString());

                        // Open connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuffer buffer = new StringBuffer();
                        if (inputStream == null) {
                            return;
                        }
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {
                            // Stream was empty.  No point in parsing.
                            return;
                        }
                        imageJSONStr = buffer.toString();

                        getImagesFromJSONStr(imageJSONStr);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageListener.onDownloadFinished(images);
                            }
                        });
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        }
    }

    private void getImagesFromJSONStr(String imageJSONStr) {
        images = new ArrayList<>();
        try {
            JSONObject reader = new JSONObject(imageJSONStr);
            if(reader.getString("stat").equals("ok")) {
                JSONObject photos = reader.getJSONObject("photos");
                JSONArray results = photos.getJSONArray("photo");
                for (int i = 0; i < results.length(); ++i) {
                    JSONObject photo = results.getJSONObject(i);

                    if(photo.has("url_l")) {
                        Image image = new Image();

                        image.setID(photo.getInt("id"));
                        image.setTitle(photo.getString("title"));
                        image.setURL(photo.getString("url_l"));
                        image.setTag(imageTag);

                        images.add(image);
                    }
                }
            }else{
                Toast.makeText(PiccyApplication.getPiccyApp().getApplicationContext(), "Failed to get images, check URL", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
