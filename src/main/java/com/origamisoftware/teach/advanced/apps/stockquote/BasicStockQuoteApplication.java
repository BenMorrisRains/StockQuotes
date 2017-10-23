package com.origamisoftware.teach.advanced.apps.stockquote;

import com.origamisoftware.teach.advanced.apps.Stocks;
import com.origamisoftware.teach.advanced.model.StockQuery;
import com.origamisoftware.teach.advanced.model.StockQuote;
import com.origamisoftware.teach.advanced.model.database.QuoteDAO;
import com.origamisoftware.teach.advanced.model.database.StockSymbolDAO;
import com.origamisoftware.teach.advanced.services.StockService;
import com.origamisoftware.teach.advanced.services.StockServiceException;
import com.origamisoftware.teach.advanced.util.Interval;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple application that shows the StockService in action.
 */
public class BasicStockQuoteApplication {

    private static String xmlInstance = "<stocks>\n" +
            "    <symbol=\"40\">Fred</symbol>\n" +
            "    <price=\"40\">Sally</price>\n" +
            "    <time>\n" +
            "        <child age=\"11\" grade=\"5\">Peter</child>\n" +
            "        <child age=\"15\" grade=\"9\">Bill</child>\n" +
            "        <child age=\"09\" grade=\"3\">Sally</child>\n" +
            "    </time>\n" +
            "</stocks>";

    private StockService stockService;

    // an example of how to use enum - not part of assignment 3 but useful for assignment 4

    /**
     * An enumeration that indicates how the program terminates (ends)
     */
    private enum ProgramTerminationStatusEnum {

        // for now, we just have normal or abnormal but could more specific ones as needed.
        NORMAL(0),
        ABNORMAL(-1);

        // when the program exits, this value will be reported to underlying OS
        private int statusCode;

        /**
         * Create a new  ProgramTerminationStatusEnum
         *
         * @param statusCodeValue the value to return the OS. A value of 0
         *                        indicates success or normal termination.
         *                        non 0 numbers indicate abnormal termination.
         */
        private ProgramTerminationStatusEnum(int statusCodeValue) {
            this.statusCode = statusCodeValue;
        }

        /**
         * @return The value sent to OS when the program ends.
         */
        private int getStatusCode() {
            return statusCode;
        }
    }

    /**
     * Create a new Application.
     *
     * @param stockService the StockService this application instance should use for
     *                     stock queries.
     *                     <p/>
     *                     NOTE: this is a example of Dependency Injection in action.
     */
    public BasicStockQuoteApplication(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Given a <CODE>stockQuery</CODE> get back a the info about the stock to display to th user.
     *
     * @param stockQuery the stock to get data for.
     * @return a String with the stock data in it.
     * @throws StockServiceException If data about the stock can't be retrieved. This is a
     *                               fatal error.
     */
    public String displayStockQuotes(StockQuery stockQuery) throws StockServiceException {
        StringBuilder stringBuilder = new StringBuilder();

        List<StockQuote> stockQuotes =
                stockService.getQuote(stockQuery.getSymbol(),
                        stockQuery.getFrom(),
                        stockQuery.getUntil(),
                        Interval.DAY); // get one quote for each day in the from until date range.

        stringBuilder.append("Stock quotes for: " + stockQuery.getSymbol() + "\n");
        for (StockQuote stockQuote : stockQuotes) {
            stringBuilder.append(stockQuote.toString());
        }

        return stringBuilder.toString();
    }

    /**
     * Terminate the application.
     *
     * @param statusCode        an enum value that indicates if the program terminated ok or not.
     * @param diagnosticMessage A message to display to the user when the program ends.
     *                          This should be an error message in the case of abnormal termination
     *                          <p/>
     *                          NOTE: This is an example of DRY in action.
     *                          A program should only have one exit point. This makes it easy to do any clean up
     *                          operations before a program quits from just one place in the code.
     *                          It also makes for a consistent user experience.
     */
    private static void exit(ProgramTerminationStatusEnum statusCode, String diagnosticMessage) {
        if (statusCode == ProgramTerminationStatusEnum.NORMAL) {
            System.out.println(diagnosticMessage);
        } else if (statusCode == ProgramTerminationStatusEnum.ABNORMAL) {
            System.err.println(diagnosticMessage);
        } else {
            throw new IllegalStateException("Unknown ProgramTerminationStatusEnum.");
        }
        System.exit(statusCode.getStatusCode());
    }

    /**
     * Run the StockTicker application.
     * <p/>
     *
     * @param args one or more stock symbols
     */
    public static void main(String[] args) throws JAXBException {

        try {
            // from XML to Java
            File file = new File("stock_info.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Stocks.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Stocks stocks = (Stocks) unmarshaller.unmarshal(file);

            // now we have a list of stock info objects

       //convert them to ORMs
        List<Stocks.Stock> stockList = stocks.getStock();
        List<QuoteDAO> quotes = new ArrayList<>(stockList.size());
        for (Stocks.Stock stock : stockList) {
            //do the conversion
            BigDecimal price = new BigDecimal(stock.getPrice());
            quotes.add(new QuoteDAO(Timestamp.valueOf(stock.getTime()), price));
        }
        } catch (JAXBException e) {
            System.out.println("Error " + e);
        }
    }
}
