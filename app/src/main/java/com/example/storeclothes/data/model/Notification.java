package com.example.storeclothes.data.model;

import java.util.Date;

public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private Boolean isRead;
    private Date createdAt;

    public Notification(String notificationId, String userId, String message, Boolean isRead, Date createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
