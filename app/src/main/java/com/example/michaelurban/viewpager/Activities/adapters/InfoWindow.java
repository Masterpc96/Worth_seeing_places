package com.example.michaelurban.viewpager.Activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.michaelurban.viewpager.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class InfoWindow implements GoogleMap.InfoWindowAdapter {
    private final View view;
    Context context;

    public InfoWindow(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ImageView imageView = view.findViewById(R.id.imageView);
        // set image using picasso from url
        Picasso.get().load("https://" + marker.getSnippet()).into(imageView);


        TextView description = view.findViewById(R.id.description);
        // set description
        description.setText(marker.getTitle());
        return view;
    }
}
