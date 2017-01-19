package com.example.lobna.flickr;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Lobna on 17-Jan-17.
 */

public class ImageAdapter extends ArrayAdapter<Image> {
    private Activity activity;
    private Context context;
    private ArrayList<Image> images;

    public ImageAdapter(Activity activity, Context context, ArrayList<Image> images) {
        super(context, R.layout.image_row, images);
        this.activity = activity;
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.image_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder); // save view holder
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final Image imageObject = images.get(position);

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        String URL = imageObject.getURL();
        Picasso.with(context).load(URL).resize(width,height/2).onlyScaleDown().into(holder.imageView);

        holder.titleTextView.setText(imageObject.getTitle());

        // save image on long click
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Put up the Yes/No message box
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setTitle("Save image")
                        .setMessage("Do you want to save this image?")
                        .setIcon(android.R.drawable.ic_menu_save)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                Bitmap imageBitmap =((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
                                Utilities.saveImage(activity, imageBitmap ,imageObject.getTitle());
                            }
                        })
                        .setNegativeButton("No", null) //Do nothing on no
                        .show();
                return true;
            }
        });

        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView titleTextView;

        ViewHolder(View imagesList){
            imageView = (ImageView) imagesList.findViewById(R.id.imageInRow);
            titleTextView = (TextView) imagesList.findViewById(R.id.titleInRow);
        }
    }
}
