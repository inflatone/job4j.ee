package ru.job4j.jobseeker.model;

import lombok.*;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import ru.job4j.vacancy.model.SourceTitle;

/**
 * Scan source model
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScanSource extends BaseEntity {
    private SourceTitle title;

    private String url;
    private String iconUrl;

    @JdbiConstructor
    public ScanSource(@ColumnName("id") Integer id,
                      @ColumnName("code") String titleCode,
                      @ColumnName("url") String url,
                      @ColumnName("icon_url") String iconUrl) {
        this(SourceTitle.valueOf(titleCode), url, iconUrl);
        setId(id);
    }
}
