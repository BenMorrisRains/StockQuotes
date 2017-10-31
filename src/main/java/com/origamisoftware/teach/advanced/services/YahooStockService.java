package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.StockQuote;
import com.origamisoftware.teach.advanced.util.Interval;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class YahooStockService implements StockService {
    @Override
    public StockQuote getQuote(String symbol) throws StockServiceException, IOException {
        StockQuote stockQuote = null;
        try {
            Stock stock = YahooFinance.get(symbol);
            BigDecimal price = stock.getQuote().getPrice();
            Calendar now = Calendar.getInstance();
            Date date = now.getTime();
            stockQuote = new StockQuote(price, date, symbol);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        return stockQuote;
    }

    @Override
    public List<StockQuote> getQuote(String symbol, Calendar from, Calendar until, Interval interval) throws StockServiceException {
        return null;
    }
}
