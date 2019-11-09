package ru.job4j.ee.store.util;

import javax.servlet.ServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * Contains general utility methods helping to serve the incoming servlet requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-09
 */
public class ServletUtil {
    public static final ThreadLocal<SimpleDateFormat> FORMATTER
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("d.MM.yyyy H:mm:ss"));
    
    private ServletUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Searches required id parameter inside the request parameters map, throws NPE if cannot find it
     *
     * @param request servlet request
     * @return item id
     */
    public static int getRequiredId(ServletRequest request) {
        String paramId = request.getParameter("id");
        return Integer.valueOf(requireNonNull(paramId, "id must not be null"));
    }

    /**
     * @param date date
     * @return string representation of the given date
     */
    public static String formatDate(Date date) {
        return FORMATTER.get().format(date);
    }
}