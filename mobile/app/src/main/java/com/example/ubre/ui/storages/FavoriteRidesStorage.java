package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.RideCardDto;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRidesStorage {
    private static FavoriteRidesStorage instance;

    private final MutableLiveData<List<RideCardDto>> favorites = new MutableLiveData<>(new ArrayList<>());

    public static synchronized FavoriteRidesStorage getInstance() {
        if (instance == null) {
            instance = new FavoriteRidesStorage();
        }
        return instance;
    }

    public LiveData<List<RideCardDto>> getFavoritesReadOnly() {
        return favorites;
    }

    public void setFavorites(List<RideCardDto> rides) {
        favorites.setValue(rides);
    }

    public void clear() {
        favorites.setValue(new ArrayList<>());
    }
}
