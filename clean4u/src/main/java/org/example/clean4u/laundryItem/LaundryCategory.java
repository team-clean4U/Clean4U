package org.example.clean4u.laundryItem;

public enum LaundryCategory {
    TOP("shirt"),
    BOTTOM("pants"),
    OUTER("vest"),
    BEDDING("bed"),
    ACCESSORY("hat-cowboy"),
    SPECIAL("star"),
    ETC("box");

    private final String icon;

    LaundryCategory(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
