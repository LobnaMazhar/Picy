package com.example.lobna.flickr;

import java.util.ArrayList;

/**
 * Created by Lobna on 17-Jan-17.
 */

public interface ImageListener {
    public void onDownloadFinished(ArrayList<Image> images);

    public void onFail(Exception e);
}
