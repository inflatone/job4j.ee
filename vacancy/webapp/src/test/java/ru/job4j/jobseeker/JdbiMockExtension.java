package ru.job4j.jobseeker;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.sql.SQLException;

public class JdbiMockExtension implements AfterTestExecutionCallback, BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) throws SQLException {
        JdbiConfiguratorHolder.start();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        JdbiConfiguratorHolder.rollback();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        JdbiConfiguratorHolder.close();
    }
}