package com.s23001792.thiriposa;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng position;
    private String title;

    public MyItem(double lat, double lng) {
        this.position = new LatLng(lat, lng);
        this.title = "";  // default, overwritten later
    }

    @Override public LatLng getPosition() { return position; }
    @Override public String getTitle() { return title; }
    @Override public String getSnippet() { return ""; }

    public void setTitle(String title) { this.title = title; }
}
