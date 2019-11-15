package ru.job4j.auto.repository.env;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.google.common.io.Resources;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.job4j.auto.util.ExceptionHandler;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Provides a database connection pool with the Bitronix JTA transaction
 * manager (http://docs.codehaus.org/display/BTM/Home).
 */
@Slf4j
@Getter
public class TransactionManager {
    public static final String DATASOURCE_NAME = "myDS";

    private final Context context = new InitialContext();

    private final String propertyResourcePath;

    private PoolingDataSource dataSource;

    @Inject
    public TransactionManager() throws NamingException {
        this("database.properties");
    }

    public TransactionManager(String propertyResourcePath) throws NamingException {
        this.propertyResourcePath = propertyResourcePath;
    }

    public void init() throws IOException {
        log.trace("Starting database connection pool");

        log.trace("Setting stable unique identifier for transaction recovery");
        var configuration = TransactionManagerServices.getConfiguration();

        configuration.setServerId("myServer1234");

        log.trace("Disabling JMX binding of manager in unit tests");
        configuration.setDisableJmx(true);

        log.trace("Disabling transaction logging for unit tests");
        configuration.setJournal("null");

//        log.trace("Disabling warnings when the database isn't accessed in a transaction");
//        configuration.setWarnAboutZeroResourceTransaction(false);

        dataSource = new PoolingDataSource();

        dataSource.setUniqueName(DATASOURCE_NAME);
        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(5);
        dataSource.setPreparedStatementCacheSize(10);

        dataSource.setIsolationLevel("READ_COMMITTED");
        dataSource.setAllowLocalTransactions(true);

        configureDataSource(propertyResourcePath);

        // https://stackoverflow.com/questions/35623780/change-btm-tlog-file-location#comment58944299_35623853
        TransactionManagerServices.getConfiguration().setLogPart1Filename("target/btm/my-btm1.tlog");
        dataSource.init();
    }

    public UserTransaction getUserTransaction() {
        return ExceptionHandler.produceEx(this::lookupUserTransaction);
    }

    UserTransaction lookupUserTransaction() throws NamingException {
        return (UserTransaction) getContext()
                .lookup("java:comp/UserTransaction");
    }

    public void rollback() {
        ExceptionHandler.executeEx(() -> {
            UserTransaction tx = lookupUserTransaction();
            if (tx.getStatus() == Status.STATUS_ACTIVE ||
                    tx.getStatus() == Status.STATUS_MARKED_ROLLBACK)
                tx.rollback();
        });
    }

    public void stop() {
        log.trace("Stopping database connection pool");
        dataSource.close();
        TransactionManagerServices.getTransactionManager().shutdown();
    }

    private void configureDataSource(String propertyResourcePath) throws IOException {
        var config = new DataSourceConfig(propertyResourcePath);

        dataSource.setClassName(config.pollRequiredProperty("xa-datasource.classname"));
        dataSource.setDriverProperties(config.getProperties());
    }

    private static class DataSourceConfig {
        private final String propertyResourcePath;

        @Getter
        private final Properties properties;

        DataSourceConfig(String propertyResourcePath) throws IOException {
            this.propertyResourcePath = propertyResourcePath;
            this.properties = loadProperties(propertyResourcePath);
        }

        public String pollRequiredProperty(String key) {
            var result = Objects.requireNonNull(properties.getProperty(key), '\'' + key + '\'' + " parameter must be present in '" + propertyResourcePath + "' file");
            properties.remove(key);
            return result;
        }

        private static Properties loadProperties(String propertyResourcePath) throws IOException {
            var properties = new Properties();
            try (var in = Resources.getResource(propertyResourcePath).openStream()) {
                properties.load(in);
            }
            return properties;
        }
    }
}
