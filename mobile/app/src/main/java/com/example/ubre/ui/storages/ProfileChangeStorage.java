// java
package com.example.ubre.ui.storages;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.ProfileChangeDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileChangeStorage {

    private static ProfileChangeStorage instance;
    private final MutableLiveData<List<ProfileChangeDto>> profileChanges = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Map<Long, byte[]>> avatars = new MutableLiveData<>(new HashMap<>());

    public LiveData<List<ProfileChangeDto>> getProfileChanges() {
        return profileChanges;
    }

    public LiveData<Map<Long, byte[]>> getAvatars() {
        return avatars;
    }

    public void putAvatar(Long userId, byte[] avatar) {
        Map<Long, byte[]> current = avatars.getValue();
        if (current == null) current = new HashMap<>();
        else current = new HashMap<>(current);
        current.put(userId, avatar);
        avatars.postValue(current);
        // main difference between set and post is that set must be called from main thread,
        // and post can be called from any thread, and it will update the value on main thread,
    }

    public static synchronized ProfileChangeStorage getInstance() {
        if (instance == null) {
            instance = new ProfileChangeStorage();
        }
        return instance;
    }

    @MainThread
    public void setProfileChanges(List<ProfileChangeDto> list) { // only from main thread, otherwise use postProfileChanges
        profileChanges.setValue(list == null ? new ArrayList<>() : new ArrayList<>(list));
    }

    public void postProfileChanges(List<ProfileChangeDto> list) {
        profileChanges.postValue(list == null ? new ArrayList<>() : new ArrayList<>(list));
    }

    @MainThread
    public void add(ProfileChangeDto item) {
        List<ProfileChangeDto> current = profileChanges.getValue();
        current = (current == null) ? new ArrayList<>() : new ArrayList<>(current);
        current.add(item);
        profileChanges.setValue(current);
    }

    public void postAdd(ProfileChangeDto item) {
        List<ProfileChangeDto> current = profileChanges.getValue();
        current = (current == null) ? new ArrayList<>() : new ArrayList<>(current);
        current.add(item);
        profileChanges.postValue(current);
    }

    @MainThread
    public void remove(ProfileChangeDto item) {
        List<ProfileChangeDto> current = profileChanges.getValue();
        if (current == null) return;
        current = new ArrayList<>(current);
        if (current.remove(item)) {
            profileChanges.setValue(current);
        }
    }

    public void postRemove(ProfileChangeDto item) {
        List<ProfileChangeDto> current = profileChanges.getValue();
        if (current == null) return;
        current = new ArrayList<>(current);
        if (current.remove(item)) {
            profileChanges.postValue(current);
        }
    }

    @MainThread
    public void clear() {
        profileChanges.setValue(new ArrayList<>());
    }

    public void postClear() {
        profileChanges.postValue(new ArrayList<>());
    }
}
