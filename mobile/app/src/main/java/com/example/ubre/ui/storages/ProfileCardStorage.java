package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileCardStorage {
    private MutableLiveData<byte[]> avatar = new MutableLiveData<>(new byte[]{});

    public LiveData<byte[]> getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar.setValue(avatar);
    }
}
