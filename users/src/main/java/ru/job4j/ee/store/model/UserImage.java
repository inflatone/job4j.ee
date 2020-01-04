package ru.job4j.ee.store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.io.InputStream;

/**
 * Model to transfer user image data layer-to-layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public class UserImage extends BaseEntity {
    private String name;
    private String contentType;
    private long size;
    private long oid;
    private transient InputStream data;

    public UserImage() {
    }

    public UserImage(UserImage image) {
        super(image.getId());
        this.contentType = image.contentType;
        this.name = image.name;
        this.size = image.size;
        this.oid = image.oid;
    }

    @JdbiConstructor
    public UserImage(@ColumnName("id") Integer id) {
        super(id);
    }

    public UserImage(String name, String contentType, long size, InputStream data) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
    }

    @JsonIgnore
    public String getContentType() {
        return contentType;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public long getSize() {
        return size;
    }

    @JsonIgnore
    public long getOid() {
        return oid;
    }

    @JsonIgnore
    public InputStream getData() {
        return data;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ColumnName("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @ColumnName("file_size")
    public void setSize(long size) {
        this.size = size;
    }
}
