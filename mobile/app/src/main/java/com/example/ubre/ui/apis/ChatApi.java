package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.ChatDto;
import com.example.ubre.ui.dtos.ChatMessageDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatApi {
    @Headers({"User-Agent: Mobile-Android"})
    @GET("api/chats")
    Call<List<ChatDto>> getChats(@Header("Authorization") String authHeader);

    @GET("api/chats/my")
    Call<ChatDto> getMyChat(@Header("Authorization") String authHeader);

    @GET("api/chats/{chatId}/messages")
    Call<List<ChatMessageDto>> getChatMessages(@Header("Authorization") String authHeader, @Path("chatId") Long chatId);

    @POST("api/chats")
    Call<ResponseBody> sendMessage(@Header("Authorization") String authHeader, @Body ChatMessageDto message);

    @PUT("api/chats/messages/{messageId}/mark")
    Call<ResponseBody> markAsRead(@Header("Authorization") String authHeader, @Path("messageId") Long messageId);

    @GET("api/chats/unread")
    Call<ResponseBody> getUnreadCount(@Header("Authorization") String authHeader);
}
