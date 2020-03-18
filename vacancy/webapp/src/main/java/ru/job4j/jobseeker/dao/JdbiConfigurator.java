package ru.job4j.jobseeker.dao;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.ParsedSql;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Date;

import static ru.job4j.vacancy.util.ExceptionUtil.nullSafely;

@Slf4j
@Getter
public class JdbiConfigurator {
    private final Jdbi jdbi;

    @Inject
    public JdbiConfigurator(DataSource dataSource) {
        this.jdbi = Jdbi.create(dataSource)
                .installPlugins()
                .registerColumnMapper(Date.class, buildDateColumnMapper())
                .setSqlLogger(buildSqlLogger());
    }

    /**
     * Creates a new sql object based on the given interface class object
     *
     * @param daoClass given extension class
     * @return DAO
     */
    public <SqlObjectType> SqlObjectType getDao(Class<SqlObjectType> daoClass) {
        return jdbi.onDemand(daoClass);
    }

    private static ColumnMapper<Date> buildDateColumnMapper() {
        return (rs, i, c) -> nullSafely(rs.getTimestamp(i), t -> new Date(t.getTime()));
    }

    private static SqlLogger buildSqlLogger() {
        return new SqlLogger() {
            @Override
            public void logBeforeExecution(StatementContext context) {
                log.info(nullSafely(context.getParsedSql(), ParsedSql::getSql));
            }
        };
    }

}
