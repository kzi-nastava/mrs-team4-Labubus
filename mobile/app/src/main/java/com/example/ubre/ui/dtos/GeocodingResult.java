package com.example.ubre.ui.dtos;

import com.google.gson.annotations.SerializedName;

public class GeocodingResult {
    @SerializedName("display_name")
    public String displayName;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;
}
