package com.exmoney.payload.response.notification;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
public class NotificationResponse {

    public static final String PROP_ID = "id";
    public static final String PROP_TYPE = "type";
    public static final String PROP_TITLE = "title";
    public static final String PROP_CONTENT = "content";
    public static final String PROP_PRIORITY = "priority";
    public static final String PROP_CREATED_AT = "createdAt";
    public static final String PROP_SEEN = "seen";
    public static final String PROP_SEEN_AT = "seenAt";

    private Long id;

    private String type; //USER, WALLET, CATEGORY, SYSTEM

    private String title;

    private String content;

    private String priority; //LOW, NORMAL, HIGH, CRITICAL

    private LocalDateTime createdAt;

    private boolean seen;

    private LocalDateTime seenAt;

    public NotificationResponse(HashMap<String, Object> map) {
        this.id = (Long) map.get(PROP_ID);
        this.type = (String) map.get(PROP_TYPE);
        this.title = (String) map.get(PROP_TITLE);
        this.content = (String) map.get(PROP_CONTENT);
        this.priority = (String) map.get(PROP_PRIORITY);
        this.createdAt = (LocalDateTime) map.get(PROP_CREATED_AT);
        this.seen = (Boolean) map.get(PROP_SEEN);
        this.seenAt = (LocalDateTime) map.get(PROP_SEEN_AT);
    }
}
