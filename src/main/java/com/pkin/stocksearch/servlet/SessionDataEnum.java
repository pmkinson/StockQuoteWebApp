package com.pkin.stocksearch.servlet;

public enum SessionDataEnum {

    SYMBOL_PARAMETER_KEY("stockSymbol"),
    START_PARAMETER_KEY("startDate"),
    END_PARAMETER_KEY("endDate"),
    INTERVAL_PARAMETER_KEY("interval"),
    QUICKSYMBOL_PARAMETER_KEY("quickSymbol"),
    USER_AGENT("User-Agent");

    private String sessionData;

    SessionDataEnum(String sessionData) {
        this.sessionData = sessionData;
    }

    public String getValue() {
        return sessionData;
    }
}
