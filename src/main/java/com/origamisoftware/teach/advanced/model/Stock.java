package com.origamisoftware.teach.advanced.model;

import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.util.Calendar;

public class Stock extends YahooFinance{

    private String symbol;
    private Calendar from;
    private Calendar to;
    private Interval interval;

    public Stock(String symbol, Calendar from, Calendar to, Interval interval) {
        this.symbol = symbol;
        this.from = from;
        this.to = to;
        this.interval = interval;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    public Calendar getTo() {
        return to;
    }

    public void setTo(Calendar to) {
        this.to = to;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
