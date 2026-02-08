package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.ReviewDto;

public class ReviewStorage {
    private static ReviewStorage instance;

    private MutableLiveData<Integer> rating = new MutableLiveData<>(3);

    private MutableLiveData<String> text = new MutableLiveData<>("");

    private MutableLiveData<Long> rideId = new MutableLiveData<>(null);

    private ReviewStorage() {

    }

    public static ReviewStorage getInstance() {
        if (instance == null)
            instance = new ReviewStorage();
        return instance;
    }

    public LiveData<Integer> getRating() {return rating;}

    public LiveData<String> getReviewText() {return text;}

    public LiveData<Long> getRideId() {return rideId;}

    public void setRating(Integer rating) {
        this.rating.setValue(rating);
    }

    public void setReviewText(String text) {
        this.text.setValue(text);
    }

    public void setRideId(Long rideId) {
        this.rideId.setValue(rideId);
    }
}
