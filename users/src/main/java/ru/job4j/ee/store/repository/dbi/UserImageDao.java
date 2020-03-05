package ru.job4j.ee.store.repository.dbi;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.jdbi.v3.sqlobject.transaction.Transactional;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObjectManager;
import ru.job4j.ee.store.model.UserImage;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Image DAO shell
 * Contains methods to complete CRUD operations with {@link UserImage} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
@RegisterBeanMapper(UserImage.class)
public interface UserImageDao extends Transactional<UserImageDao> {
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO images (name, content_type, file_size, oid) VALUES (:name, :contentType, :size, :oid)")
    int insertAndReturnId(@BindBean UserImage userImage);

    @SqlQuery("SELECT * FROM images WHERE id=:id")
    UserImage find(@Bind(value = "id") int id);

    @SqlUpdate("UPDATE users u SET image_id=null WHERE u.id=:userId AND u.image_id=:id")
    int unbind(@Bind(value = "id") int id, @Bind(value = "userId") Integer userId);

    @SqlUpdate("DELETE FROM images WHERE id=:id")
    int delete(@Bind(value = "id") int id);

    @Transaction
    default void save(UserImage image) {
        try {
            upload(image);
        } catch (SQLException | IOException e) {
            throw new TransactionException(e);
        }
    }

    @Transaction
    default boolean delete(int id, Integer userId) {
        if (userId == null || unbind(id, userId) != 0) {
            var image = find(id);
            return image != null && erase(image);
        }
        rollback();
        return false;
    }

    default UserImage findById(int id) {
        var image = find(id);
        if (image != null) {
            var handle = getHandle().getJdbi().open().begin();
            return attachData(handle, image);
        }
        return null;
    }

    /**
     * Uploads the given user image data to DB through the given large object manager API
     *
     * @param image image data
     */
    private void upload(UserImage image) throws SQLException, IOException {
        var largeObjectManager = getLargeObjectManager(getHandle());
        image.setOid(largeObjectManager.createLO());
        try (var obj = largeObjectManager.open(image.getOid());
             var out = obj.getOutputStream();
             var in = image.getData()
        ) {
            in.transferTo(out);
            int imageId = insertAndReturnId(image);
            image.setId(imageId);
        }
    }

    /**
     * Removes the image large object data via postgres manager,
     * then asks the image dao to execute the remove query to get rid of the DB table row associated with the image obj
     *
     * @param image image obj
     * @return true if successful
     */
    private boolean erase(UserImage image) {
        try {
            var largeObjectManager = getLargeObjectManager(getHandle());
            largeObjectManager.delete(image.getOid());
            if (delete(image.getId()) == 0) {
                rollback();
                return false;
            }
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException("cannot delete image data with id=" + image.getId(), e);
        }
    }

    /**
     * Attaches the image data associated with the given image object to this image object
     * Provides keeping open the SQL connection to successful retrieving data on web layer
     *
     * @param image image obj
     * @return image obj with attached image data
     */
    private UserImage attachData(Handle handle, UserImage image) {
        try {
            long oid = image.getOid();
            var largeObjectManager = getLargeObjectManager(handle);
            var obj = largeObjectManager.open(oid, LargeObjectManager.READ);
            // postgres does not allow to read from the large object after the connection has been closed
            // https://www.programcreek.com/java-api-examples/?code=syndesisio/syndesis/syndesis-master/app/rest/filestore/src/main/java/io/syndesis/filestore/impl/SqlIconFileStore.java
            image.setData(new HandleCloserInputStream(handle, obj.getInputStream()));
            return image;
        } catch (SQLException e) {
            handle.rollback();
            handle.close();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Allows closing a handle after the given {@link InputStream} has been fully read and closed.
     */
    class HandleCloserInputStream extends FilterInputStream {
        private Handle handle;

        HandleCloserInputStream(Handle handle, InputStream in) {
            super(in);
            this.handle = handle;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                handle.commit();
                handle.close();
            }
        }
    }

    /**
     * Returns PSQL large object manager, bounded to the current dao connection
     *
     * @return postgres large object manager
     */
    private LargeObjectManager getLargeObjectManager(Handle handle) throws SQLException {
        return getPostgresConnection(handle.getConnection()).getLargeObjectAPI();
    }

    /**
     * Unwraps psql connection from the current dao connection handler object
     *
     * @return psql connection object
     */
    private PGConnection getPostgresConnection(Connection connection) throws SQLException {
        if (connection instanceof PGConnection) {
            return (PGConnection) connection;
        }
        return connection.unwrap(PGConnection.class);
    }
}