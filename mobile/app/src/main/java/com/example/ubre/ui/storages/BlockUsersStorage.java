package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

public class BlockUsersStorage {
    private static BlockUsersStorage instance;
    private final MutableLiveData<List<UserDto>> users = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public static synchronized BlockUsersStorage getInstance() {
        if (instance == null) {
            instance = new BlockUsersStorage();
        }
        return instance;
    }

    public LiveData<List<UserDto>> getUsersReadOnly() {
        return users;
    }

    public LiveData<Boolean> getLoadingReadOnly() {
        return loading;
    }

    public void setUsers(List<UserDto> data) {
        if (data == null) {
            users.setValue(new ArrayList<>());
        } else {
            users.setValue(new ArrayList<>(data));
        }
    }

    public void setLoading(boolean isLoading) {
        loading.setValue(isLoading);
    }

    public void updateUserBlocked(Long userId, boolean isBlocked) {
        List<UserDto> current = users.getValue();
        if (current == null || current.isEmpty()) {
            return;
        }
        List<UserDto> updated = new ArrayList<>(current.size());
        for (UserDto user : current) {
            if (user != null && userId != null && userId.equals(user.getId())) {
                UserDto copy = new UserDto(user);
                copy.setBlocked(isBlocked);
                updated.add(copy);
            } else {
                updated.add(user);
            }
        }
        users.setValue(updated);
    }
}
