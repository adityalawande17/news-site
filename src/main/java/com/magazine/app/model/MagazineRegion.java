package com.magazine.app.model;

public enum MagazineRegion {
    AFRICA,
    ASIA,
    EUROPE,
    MIDDLE_EAST,
    AMERICA;

    // Human-readable label for display in templates
    public String getLabel() {
        switch (this) {
            case AFRICA:        return "Africa";
            case ASIA:          return "Asia";
            case EUROPE:        return "Europe";
            case MIDDLE_EAST:   return "Middle East";
            case AMERICA:       return "America";
            default:            return this.name();
        }
    }
}