package com.ubre.backend.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private static final String PROFILE_CHANGE_TOPIC_PREFIX = "/topic/profile-changes/";

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendProfileChangeApproved(Long userId, ProfileChangeNotification notification) {
        messagingTemplate.convertAndSend(PROFILE_CHANGE_TOPIC_PREFIX + userId, notification);
    }

    public void sendProfileChangeRejected(Long userId, ProfileChangeNotification notification) {
        messagingTemplate.convertAndSend(PROFILE_CHANGE_TOPIC_PREFIX + userId, notification);
    }
}
