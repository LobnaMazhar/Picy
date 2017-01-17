package com.example.lobna.flickr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ImageListener {

    private static ListView imagesListView;
    private ImageAdapter imageAdapter;

    private SearchView searchImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesListView = (ListView) findViewById(R.id.imagesListView);

        searchImages = (SearchView) findViewById(R.id.searchImages);
        searchImages.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String tag = searchImages.getQuery().toString();
                if (!tag.equals("")) {
                    searchImagesByTag(tag);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void searchImagesByTag(String tag) {
        ImageManager.getInstance().getImages(tag, this);
    }

    @Override
    public void onDownloadFinished(final ArrayList<Image> images) {
        imageAdapter = new ImageAdapter(this, this, images);
        imagesListView.setAdapter(imageAdapter);
    }

    @Override
    public void onFail(Exception e) {
        Utilities.noInternet(this);
    }
}
