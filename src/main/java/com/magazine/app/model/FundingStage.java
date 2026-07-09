package com.magazine.app.model;

public enum FundingStage {
    BOOTSTRAPPED,
    PRE_SEED,
    SEED,
    SERIES_A,
    SERIES_B,
    SERIES_C,
    SERIES_D_PLUS,
    IPO;

    public String getLabel() {
        switch (this) {
            case BOOTSTRAPPED:  return "Bootstrapped";
            case PRE_SEED:      return "Pre-Seed";
            case SEED:          return "Seed";
            case SERIES_A:      return "Series A";
            case SERIES_B:      return "Series B";
            case SERIES_C:      return "Series C";
            case SERIES_D_PLUS: return "Series D+";
            case IPO:           return "IPO";
            default:            return this.name();
        }
    }
}