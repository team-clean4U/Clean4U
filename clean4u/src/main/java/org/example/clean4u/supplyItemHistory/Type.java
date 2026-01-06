package org.example.clean4u.supplyItemHistory;

public enum Type {
    IN("입고"),
    OUT("출고"),
    ADJUSTMENT("조정"),
    UPDATE("수정");

    private final String description;

    Type(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
