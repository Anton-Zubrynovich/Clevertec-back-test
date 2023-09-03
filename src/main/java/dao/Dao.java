package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * The interface Dao.
 *
 * @param <K> the type parameter
 * @param <E> the type parameter
 */
public interface Dao<K, E> {

    /**
     * Get optional.
     *
     * @param id the id
     * @return the optional
     * @throws SQLException the sql exception
     * @throws Exception    the exception
     */
    Optional<E> get(K id) throws SQLException, Exception;

    /**
     * Gets all.
     *
     * @return the all
     * @throws SQLException the sql exception
     */
    List<E> getAll() throws SQLException;

    /**
     * Save.
     *
     * @param t the t
     * @throws SQLException the sql exception
     */
    void save(E t) throws SQLException;

    /**
     * Update.
     *
     * @param t      the t
     * @param params the params
     * @throws SQLException the sql exception
     */
    void update(E t, String[] params) throws SQLException;

    /**
     * Delete.
     *
     * @param t the t
     * @throws SQLException the sql exception
     * @throws Exception    the exception
     */
    void delete(E t) throws SQLException, Exception;

    /**
     * Close.
     *
     * @param rs    the rs
     * @param pstmt the pstmt
     * @param con   the con
     */
    default void close(ResultSet rs, PreparedStatement pstmt, Connection con){
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}