package org.example.clean4u.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CANCELLED("접수취소", "status-cancelled"),
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

    public boolean isBefore(OrderStatus changeStatus) {
        // 취소단계에서 상태변경 안됨
        if(this == CANCELLED || changeStatus == CANCELLED) {
            return false;
        }
        // 현재 단계보다 변경할 단계(매개변수)가 뒤 인경우 (옳은 흐름)
        return this.compareTo(changeStatus) < 0;
    }

    // 접수 -> 접수취소 순서는 허용(결제 대기 상태인 경우에만)
    public boolean canCancel(OrderStatus changeStatus, Order order) {
        return this == RECEIVED && changeStatus == CANCELLED && order.isPending();
    }

    public boolean canChangeTo(OrderStatus changeStatus, Order order) {
        if(this.isBefore(changeStatus) && !order.isPending()) return true;
        return this.canCancel(changeStatus, order);
    }
}