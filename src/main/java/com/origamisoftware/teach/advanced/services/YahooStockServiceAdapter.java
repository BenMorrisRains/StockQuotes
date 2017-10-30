package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.StockQuote;

import com.origamisoftware.teach.advanced.util.Interval;
import yahoofinance.Stock;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class YahooStockServiceAdapter implements StockService {

    YahooStockService yahooStockService;

    @Override
    public StockQuote getQuote(String symbol) throws StockServiceException, IOException {
        return null;
    }

    @Override
    public List<StockQuote> getQuote(String symbol, Calendar from, Calendar until, Interval interval) throws StockServiceException {
        return null;
    }
}
