package com.origamisoftware.teach.advanced.util;

import com.ibatis.common.jdbc.ScriptRunner;
import com.origamisoftware.teach.advanced.model.database.DatabasesAccessObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * A class that contains database related utility methods.
 */
public class DatabaseUtils {

    public static final String initializationFile = "./src/main/sql/stocks_db_initialization.sql";

    private static SessionFactory sessionFactory;
    private static Configuration configuration;
    private static String HIBERNATE_CONFIGURATION_FILE = "hibernate.cfg.xml";
    private static String JDBC_DRIVER_CLASS_PROPERTY_KEY = "connection.driver_class";
    private static String DATABASE_USER_NAME = "hibernate.connection.username";
    private static String DATABASE_USER_PASSWORD = "hibernate.connection.password";
    private static String DATABASE_URL = "connection.url";


    /**
     * Gets the value of a key from config file
     *
     * @param property
     * @return
     */
    private static String getPropFromConfig(String property) {
        String s = null;
        Configuration cfg = new Configuration();
        cfg.configure(HIBERNATE_CONFIGURATION_FILE); //hibernate config xml file name
        return cfg.getProperties().get(property).toString();
    }

    /**
     * @return a connection to the DBMS which is used for DBMS
     * @throws DatabaseConnectionException if a connection cannot be made.
     */
    public static Connection getConnection() throws DatabaseConnectionException {
        Connection connection = null;
        try {
            Class.forName(getPropFromConfig(JDBC_DRIVER_CLASS_PROPERTY_KEY));
            connection = DriverManager.getConnection(getPropFromConfig(DATABASE_URL),
                    getPropFromConfig(DATABASE_USER_NAME),
                    getPropFromConfig(DATABASE_USER_PASSWORD));
            // an example of throwing an exception appropriate to the abstraction.
        } catch (ClassNotFoundException | SQLException e) {
            String message = e.getMessage();
            throw new DatabaseConnectionException("Could not connection to database." + message, e);
        }
        return connection;
    }

    /**
     * A utility method that runs a db initialize script.
     *
     * @param initializationScript full path to the script to run to create the schema
     * @throws DatabaseInitializationException
     */
    public static void initializeDatabase(String initializationScript) throws DatabaseInitializationException {

        Connection connection = null;
        final StringBuilder errorLog = new StringBuilder();
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(connection, false, false);
            InputStream inputStream = new FileInputStream(initializationScript);

            InputStreamReader reader = new InputStreamReader(inputStream);

            runner.setErrorLogWriter(new PrintWriter(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    errorLog.append((char) b);
                }
            }));
            runner.runScript(reader);

            reader.close();
            connection.commit();
            connection.close();

        } catch (DatabaseConnectionException | SQLException | IOException e) {
            throw new DatabaseInitializationException("Could not initialize db because of:"
                    + e.getMessage(), e);
        }
        // improvement - previous versions would not single errors in the SQL itself.
        if (errorLog.length() != 0) {
            throw new DatabaseInitializationException("SQL init script contained errors:" + errorLog.toString());


        }
    }

    /**
     * Execute SQL code
     *
     * @param someSQL the code to execute
     * @return true if the operation succeeded.
     * @throws DatabaseException if accessing and executing the sql failed in an unexpected way.
     */
    public static boolean executeSQL(String someSQL) throws DatabaseException {
        Connection connection = null;
        boolean returnValue = false;
        try {
            connection = DatabaseUtils.getConnection();
            Statement statement = connection.createStatement();
            returnValue = statement.execute(someSQL);
        } catch (DatabaseConnectionException | SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
        return returnValue;
    }


    /*
   * @return SessionFactory for use with database transactions
   */
    public static SessionFactory getSessionFactory() {

        // singleton pattern
        synchronized (DatabaseUtils.class) {
            if (sessionFactory == null) {

                Configuration configuration = getConfiguration();

                ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .buildServiceRegistry();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            }
        }
        return sessionFactory;
    }

    /**
     * Create a new or return an existing database configuration object.
     *
     * @return a Hibernate Configuration instance.
     */
    private static Configuration getConfiguration() {

        synchronized (DatabaseUtils.class) {
            if (configuration == null) {
                configuration = new Configuration();
                configuration.configure(HIBERNATE_CONFIGURATION_FILE);
            }
        }
        return configuration;
    }

    /**
     * A generic finder method where the caller specifies the
     * property to match as a <CODE>String</CODE> and its value as an arbitrary <CODE>Object</CODE>
     * A specific entity will be returned as a domain model or null not entities matching
     * the search criteria are found.
     * <p/>
     * NOTE: the caller is responsible for closing the transaction.
     * This allows collections that are lazily initialized to read after calling this method.
     *
     * @param property          a member of the class that represents the column being search in.
     * @param value             the value that is being match against in the column
     * @param <T>               a class that implements DatabasesAccessObject
     * @param handleTransaction true, if this method should open and close transaction false
     *                          if calling code will be responsible for transaction management
     * @return an instance of T or NULL
     * if no value was found matching the criteria.
     */
    @SuppressWarnings("unchecked")  // API requires unchecked OK per guidelines
    public static <T extends DatabasesAccessObject> T findUniqueResultBy(String property,
                                                                         Object value,
                                                                         Class T,
                                                                         boolean handleTransaction) {
        T returnValue = null;
        Session session = null;
        try {
            Transaction transaction = null;
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(T);
            criteria = criteria.add(Restrictions.eq(property, value));

            returnValue = (T) criteria.uniqueResult();
            if (handleTransaction) {
                transaction.commit();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return returnValue;
    }

    /**
     * A generic finder  method where the caller specifies the
     * property to match as a <CODE>String</CODE> and its value as an <CODE>Object</CODE> which
     * contains that value.
     * <p/>
     * A list of the specified entity will be returned as a domain model or an empty list if no
     * entities matching the search criteria are found.
     * <p/>
     * NOTE: the caller is responsible for closing the transaction if handleTransaction is false.
     * This allows collections that are lazily initialized to read after calling this method.
     * <p/>
     *
     * @param property          a member of the class that represents the column being search in.
     * @param value             the value that is being match against in the column
     * @param <T>               a class that implements DatabasesAccessObject
     * @param handleTransaction true, if this method should open and close transaction false
     *                          if calling code will be responsible for transaction management
     * @return a list of type <T> objects
     * if no value was found matching the criteria.
     */
    @SuppressWarnings("unchecked")  // API requires unchecked OK per guidelines
    public static <T extends DatabasesAccessObject> List<T> findResultsBy(String property,
                                                                          Object value,
                                                                          Class T,
                                                                          boolean handleTransaction) {
        Session session = null;
        List<T> returnValue;
        try {
            Transaction transaction = null;
            session = getSessionFactory().openSession();
            if (handleTransaction) {
                transaction = session.beginTransaction();
            }
            Criteria criteria = session.createCriteria(T);
            criteria = criteria.add(Restrictions.eq(property, value));
            returnValue = (List<T>) criteria.list();
            if (handleTransaction) {
                transaction.commit();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return returnValue;
    }


}
