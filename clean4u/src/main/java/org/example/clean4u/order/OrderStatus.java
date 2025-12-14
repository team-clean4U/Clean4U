package org.example.clean4u.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    RECEIVED("접수", "status-received"),
    WASHING("세탁중", "status-washing"),
    DRYING("건조중", "status-drying"),
    COMPLETED("세탁완료", "status-completed");

    private final String displayName;
    private final String cssClass;

    OrderStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }
}