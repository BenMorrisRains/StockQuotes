package com.origamisoftware.teach.advanced.services;

public class YahooStockServiceFactory {


    public YahooStockServiceFactory() {
    }

    public static YahooStockService getYahooStockService() {
        return new YahooStockService();
    }
}
