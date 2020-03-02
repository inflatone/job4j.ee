package ru.job4j.auto;

import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfilesResolver;

public class ActiveDbProfileResolver implements ActiveProfilesResolver {
    public static final String POSTGRES_PROFILE = "postgres";
    public static final String HSQL_PROFILE = "hsqldb";

    @NonNull
    @Override
    public String[] resolve(@NonNull Class<?> testClass) {
        return new String[]{getActiveDbProfile()};
    }

    public static String getActiveDbProfile() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            return HSQL_PROFILE;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("org.postgresql.Driver");
                return POSTGRES_PROFILE;
            } catch (ClassNotFoundException e1) {
                throw new IllegalStateException("Could not find DB driver");
            }
        }
    }
}
