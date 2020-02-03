package ru.job4j.jobseeker.dao;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.ParsedSql;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.google.common.io.Resources.getResource;
import static java.util.Objects.requireNonNull;
import static ru.job4j.vacancy.util.ExceptionUtil.handleAccept;
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

    public JdbiConfigurator buildUpDatabase(String resourcePath) {
        try (var handler = getJdbi().open()) {
            URL scriptUrl = getResource(resourcePath);
            File[] files = requireNonNull(new File(scriptUrl.getPath()).listFiles(),
                    "Scripts to create and populate DB are not found in " + scriptUrl);
            StreamEx.of(files).sorted().forEach(f -> handleAccept(f, s -> executeScript(handler, s)));
        }
        return this;
    }

    private static void executeScript(Handle handle, File script) throws IOException {
        String source = Files.asCharSource(script, StandardCharsets.UTF_8).read();
        handle.createScript(source).executeAsSeparateStatements();
    }
}
