package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.YahooStockQuote;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class YahooSimpleStockService implements YahooStockService {

    @Override
    public Stock getQuote(String symbol, Calendar from, Calendar to, Interval interval) {
        List<Stock> stockQuotes = new ArrayList<>();
        Date aDay = from.getTime();
        while (to.after(aDay)) {
            stockQuotes.add(new Stock(symbol));
            from.add(Calendar.DAY_OF_YEAR, 1);
            aDay = from.getTime();
        }
        return (Stock) stockQuotes;
    }
}

