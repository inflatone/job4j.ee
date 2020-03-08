package ru.job4j.auto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
public class Image extends BaseEntity {
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false)
    @JsonIgnore
    private byte[] data;

    public Image(Integer id, String fileName, String contentType, byte[] data) {
        super(id);
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }
}
