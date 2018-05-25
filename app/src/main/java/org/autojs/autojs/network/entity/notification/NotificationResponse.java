package org.autojs.autojs.network.entity.notification;

import java.util.List;

public class NotificationResponse {

    private List<Notification> notifications;

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }


    @Override
    public String toString() {
        return
                "NotificationResponse{" +
                        "notifications = '" + notifications + '\'' +
                        "}";
    }
}