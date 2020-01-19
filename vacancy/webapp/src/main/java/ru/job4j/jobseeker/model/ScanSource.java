package ru.job4j.jobseeker.model;

import lombok.*;
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

    public ScanSource(Integer id, SourceTitle title, String url, String iconUrl) {
        this(title, url, iconUrl);
        setId(id);
    }
}
