package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.YahooStockQuote;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;

import java.util.Calendar;
import java.util.List;

public interface YahooStockService {

 Stock getQuote(String symbol, Calendar to, Calendar from, Interval interval);
}
