package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.StockQuote;

import com.origamisoftware.teach.advanced.model.YahooStockQuote;
import com.origamisoftware.teach.advanced.util.Interval;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class YahooStockServiceAdapter implements StockService {

    YahooStockService yahooStockService;

    public YahooStockServiceAdapter(YahooStockService yahooStockService) {
        this.yahooStockService = yahooStockService;
    }


    @Override
    public StockQuote getQuote(String symbol) throws StockServiceException, IOException {
    YahooStockQuote yahooStockQuote = new YahooStockQuote();
    yahooStockQuote.setFrom(Calendar.getInstance());
    yahooStockQuote.setTo(Calendar.getInstance());
    yahooStockQuote.setInterval(yahoofinance.histquotes.Interval.DAILY);
    yahooStockQuote.setSymbol("Goog");



    }
    @Override
    public List<StockQuote> getQuote(String symbol, Calendar from, Calendar until, Interval interval) throws StockServiceException {
        return null;
    }
}
