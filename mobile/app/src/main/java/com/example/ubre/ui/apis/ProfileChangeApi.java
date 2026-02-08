package com.example.ubre.ui.apis;

import com.example.ubre.ui.dtos.ProfileChangeDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProfileChangeApi {
    // admin only, load pending profile changes
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/drivers/profile-changes/pending")
    Call<List<ProfileChangeDto>> getPendingProfileChanges(@Header("Authorization") String authHeader);

    // approve a profile change (admin only)
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @PUT("api/drivers/profile-changes/{changeId}/approve")
    Call<Void> approveProfileChange(@Header("Authorization") String authHeader, @Path("changeId") Long changeId);

    // reject a profile change (admin only)
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @PUT("api/drivers/profile-changes/{changeId}/reject")
    Call<Void> rejectProfileChange(@Header("Authorization") String authHeader, @Path("changeId") Long changeId);

    // get drivers avatar for display
    @Headers({
            "User-Agent: Mobile-Android",
    })
    @GET("api/users/{id}/avatar")
    Call<ResponseBody> getDriverAvatar(@Header("Authorization") String authHeader, @Path("id") Long id);
}
