package com.magazine.app.model;

public enum MagazineRegion {
    AFRICA,
    ASIA,
    AUSTRALIA,
    EUROPE,
    MIDDLE_EAST,
    NORTH_AMERICA,
    SOUTH_AMERICA;

    // Human-readable label for display in templates
    public String getLabel() {
        switch (this) {
            case AFRICA:        return "Africa";
            case ASIA:          return "Asia";
            case AUSTRALIA:     return "Australia";
            case EUROPE:        return "Europe";
            case MIDDLE_EAST:   return "Middle East";
            case NORTH_AMERICA: return "North America";
            case SOUTH_AMERICA: return "South America";
            default:            return this.name();
        }
    }
}