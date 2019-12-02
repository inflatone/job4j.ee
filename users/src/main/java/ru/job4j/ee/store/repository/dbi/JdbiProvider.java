package ru.job4j.ee.store.repository.dbi;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Contains the logic of managing DBI connection
 * <p>
 * By default retrieves the DBI connection object via JNDI (from servlet container)
 * <p>
 * Otherwise it can be customized by submitting the alternative connection factory
 * through  {@link #init(DataSource)} call before the first access to {@link JdbiHolder}
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-08
 */
public class JdbiProvider {
    private static final Logger log = getLogger(JdbiProvider.class);

    /**
     * Alternative connection factory may be submitted
     */
    private static DataSource dataSource;

    /**
     * Holder class of JDBI connection
     */
    private static class JdbiHolder {
        private static final Jdbi JDBI;

        static {
            final Jdbi dbi;
            if (dataSource != null) {
                log.info("init JDBI with given data source");
                dbi = Jdbi.create(dataSource);
            } else {
                try {
                    log.info("init JDBI with JNDI (from servlet container)");
                    InitialContext ctx = new InitialContext();
                    dbi = Jdbi.create(((DataSource) ctx.lookup("java:/comp/env/jdbc/store")));
                } catch (NamingException e) {
                    throw new IllegalStateException("DB connection initialization failed", e);
                }
            }
            JDBI = dbi.installPlugin(new SqlObjectPlugin())
                    .registerColumnMapper(Date.class, (rs, i, c) -> new Date(rs.getTimestamp(i).getTime()))
                    .setSqlLogger(new SqlLogger() {
                                      @Override
                                      public void logBeforeExecution(StatementContext context) {
                                          log.info(context.getParsedSql().getSql());
                                      }
                                  }
                    );
        }
    }

    /**
     * Has to use to submit alternative connection factory object
     *
     * @param dataSource connection factory
     */
    public static void init(DataSource dataSource) {
        JdbiProvider.dataSource = dataSource;
    }

    /**
     * Creates a new sql object based on the given interface class object
     *
     * @param daoClass given extension class
     * @return DAO
     */
    private static <SqlObjectType> SqlObjectType getDao(Class<SqlObjectType> daoClass) {
        return JdbiHolder.JDBI.onDemand(daoClass);
    }

    /**
     * Creates a new sql object of user DAO
     *
     * @return user DAO
     */
    public static UserDao getUserDao() {
        return getDao(UserDao.class);
    }

    /**
     * Creates a new sql object of user image DAO
     *
     * @return user image DAO
     */
    public static UserImageDao getUserImageDao() {
        return getDao(UserImageDao.class);
    }
}