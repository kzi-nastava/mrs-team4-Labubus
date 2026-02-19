package com.example.ubre.ui.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.ChatApi;
import com.example.ubre.ui.dtos.ChatDto;
import com.example.ubre.ui.dtos.ChatMessageDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.storages.ChatStorage;
import com.example.ubre.ui.storages.UserStorage;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatService {
    private static final String TAG = "ChatService";
    private static ChatService instance;

    private final ChatApi api;

    private ChatService() {
        api = ApiClient.getClient().create(ChatApi.class);
    }

    public static ChatService getInstance() {
        if (instance == null)
            instance = new ChatService();
        return instance;
    }

    private String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return sp.getString("jwt", null);
    }

    public void loadChats(Context context) {
        String token = getToken(context);
        if (token == null) return;

        api.getChats("Bearer " + token).enqueue(new Callback<List<ChatDto>>() {
            @Override
            public void onResponse(Call<List<ChatDto>> call, Response<List<ChatDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatDto> sorted = new ArrayList<>(response.body());
                    sorted.sort((a, b) -> {
                        boolean aUnread = Boolean.TRUE.equals(a.getHasUnreadMessages());
                        boolean bUnread = Boolean.TRUE.equals(b.getHasUnreadMessages());
                        return Boolean.compare(bUnread, aUnread);
                    });
                    ChatStorage.getInstance().setChats(sorted);
                }
            }

            @Override
            public void onFailure(Call<List<ChatDto>> call, Throwable t) {
                Log.e(TAG, "Failed to load chats", t);
            }
        });
    }

    public void loadMyChat(Context context) {
        String token = getToken(context);
        if (token == null) return;

        api.getMyChat("Bearer " + token).enqueue(new Callback<ChatDto>() {
            @Override
            public void onResponse(Call<ChatDto> call, Response<ChatDto> response) {
                if (response.isSuccessful()) {
                    ChatStorage.getInstance().setCurrentChat(response.body());
                }
            }

            @Override
            public void onFailure(Call<ChatDto> call, Throwable t) {
                Log.e(TAG, "Failed to load my chat", t);
            }
        });
    }

    public void loadMessages(Context context) {
        String token = getToken(context);
        if (token == null) return;

        ChatDto chat = ChatStorage.getInstance().getCurrentChatValue();
        if (chat == null || chat.getId() == null) return;

        api.getChatMessages("Bearer " + token, chat.getId()).enqueue(new Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDto>> call, Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatMessageDto> msgs = response.body();
                    ChatStorage.getInstance().setMessages(msgs);

                    UserDto currentUser = UserStorage.getInstance().getCurrentUser().getValue();
                    if (currentUser != null) {
                        int markedCount = 0;
                        for (ChatMessageDto msg : msgs) {
                            if (!Boolean.TRUE.equals(msg.getIsRead())
                                    && msg.getSender() != null
                                    && msg.getSender().getRole() != currentUser.getRole()) {
                                msg.setIsRead(true);
                                markedCount++;
                                markAsRead(context, msg.getId());
                            }
                        }
                        if (markedCount > 0) {
                            int current = ChatStorage.getInstance().getUnreadCountValue();
                            ChatStorage.getInstance().setUnreadCount(Math.max(0, current - markedCount));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDto>> call, Throwable t) {
                Log.e(TAG, "Failed to load messages", t);
            }
        });
    }

    public void sendMessage(Context context, ChatMessageDto message) {
        String token = getToken(context);
        if (token == null) return;

        boolean hadNoChat = ChatStorage.getInstance().getCurrentChatValue() == null
                || ChatStorage.getInstance().getCurrentChatValue().getId() == null;

        api.sendMessage("Bearer " + token, message).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // If no chat existed before sending, reload to get the newly created chat
                    // so incoming WS messages can match the chatId and display correctly
                    if (hadNoChat) {
                        loadMyChat(context);
                    }
                } else {
                    Toast.makeText(context, "Failed to send message: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to send message", t);
            }
        });
    }

    public void markAsRead(Context context, Long messageId) {
        if (messageId == null) return;
        String token = getToken(context);
        if (token == null) return;

        api.markAsRead("Bearer " + token, messageId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to mark as read", t);
            }
        });
    }

    public void loadUnreadCount(Context context) {
        String token = getToken(context);
        if (token == null) return;

        api.getUnreadCount("Bearer " + token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        int count = Integer.parseInt(response.body().string().trim());
                        ChatStorage.getInstance().setUnreadCount(count);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse unread count", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to load unread count", t);
            }
        });
    }
}
