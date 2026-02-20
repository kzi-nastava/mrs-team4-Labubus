package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.ChatDto;
import com.example.ubre.ui.dtos.ChatMessageDto;

import java.util.ArrayList;
import java.util.List;

public class ChatStorage {
    private static ChatStorage instance;

    private MutableLiveData<List<ChatDto>> chats = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ChatDto> currentChat = new MutableLiveData<>(null);
    private MutableLiveData<List<ChatMessageDto>> messages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Integer> unreadCount = new MutableLiveData<>(0);

    private ChatStorage() {}

    public static ChatStorage getInstance() {
        if (instance == null)
            instance = new ChatStorage();
        return instance;
    }

    public LiveData<List<ChatDto>> getChatsReadOnly() { return chats; }
    public void setChats(List<ChatDto> list) { chats.setValue(list != null ? list : new ArrayList<>()); }

    public LiveData<ChatDto> getCurrentChat() { return currentChat; }
    public ChatDto getCurrentChatValue() { return currentChat.getValue(); }
    public void setCurrentChat(ChatDto chat) { currentChat.setValue(chat); }

    public LiveData<List<ChatMessageDto>> getMessagesReadOnly() { return messages; }
    public List<ChatMessageDto> getMessagesValue() { return messages.getValue(); }
    public void setMessages(List<ChatMessageDto> list) { messages.setValue(list != null ? list : new ArrayList<>()); }

    public void addMessageToTop(ChatMessageDto message) {
        List<ChatMessageDto> current = messages.getValue();
        List<ChatMessageDto> updated = current != null ? new ArrayList<>(current) : new ArrayList<>();
        updated.add(0, message);
        messages.setValue(updated);
    }

    public LiveData<Integer> getUnreadCount() { return unreadCount; }
    public Integer getUnreadCountValue() { return unreadCount.getValue() != null ? unreadCount.getValue() : 0; }
    public void setUnreadCount(int count) { unreadCount.setValue(count); }
    public void incrementUnread() { unreadCount.setValue(getUnreadCountValue() + 1); }
    public void decrementUnread() { unreadCount.setValue(Math.max(0, getUnreadCountValue() - 1)); }

    public void clear() {
        chats.setValue(new ArrayList<>());
        currentChat.setValue(null);
        messages.setValue(new ArrayList<>());
        unreadCount.setValue(0);
    }
}
