package dao;

import entity.Customer;
import exception.DaoException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The type Customer dao.
 */
public class CustomerDao implements Dao<Long, Customer>{

    private static final CustomerDao INSTANCE = new CustomerDao();
    private static final String SAVE_SQL = """
            INSERT INTO customer(firstname, lastname)
            VALUES (?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE customer
            SET firstname = ?, lastname = ?
            WHERE customer_id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE from customer
            WHERE customer_id = ?
            """;

    private static final String SELECT_ONE_SQL = """
            SELECT * 
            FROM customer
            WHERE customer_id = ?
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT *
            FROM customer
            ORDER By customer_id ASC
            """;

    private CustomerDao(){

    }

    /**
     * Get instance customer dao.
     *
     * @return the customer dao
     */
    public static CustomerDao getInstance(){
        return INSTANCE;
    }


    public void save(Customer customer) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SAVE_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.executeUpdate();

            con.commit();

        }catch (Exception e) {
            if (con != null){
                con.rollback();
            }
            System.out.println("Adding failure");
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
        }

    }


    @Override
    public void update(Customer t, String[] params) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(UPDATE_SQL);

            con.setAutoCommit(false);

            pstmt.setString(1, params[0]);
            pstmt.setString(2, params[1]);
            pstmt.setLong(3, t.getId());
            pstmt.executeUpdate();

            con.commit();

        } catch (Exception e) {
            if (con != null){
                con.rollback();
            }
            System.out.println("Update failure");
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
        }

    }

    @Override
    public void delete(Customer t) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(DELETE_SQL);

            con.setAutoCommit(false);

            pstmt.setLong(1, t.getId());
            pstmt.executeUpdate();

            con.commit();

        } catch (Exception e) {
            if (con != null){
                con.rollback();
            }
            System.out.println("Delete failure");
            throw new RuntimeException(e);
        } finally {
            close(null, pstmt, con);
        }
    }


    @Override
    public Optional<Customer> get(Long id) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Customer customer = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ONE_SQL, pstmt.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            con.setAutoCommit(false);

            if (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getLong("customer_id"));
                customer.setFirstName(rs.getString("firstname"));
                customer.setLastName(rs.getString("lastname"));
                return Optional.of(customer);
            }

            con.commit();

        } catch (Exception e) {
            if (con != null){
                con.rollback();
            }
            System.out.println("Get failure");
            throw new RuntimeException(e);
        } finally {
            close(rs, pstmt, con);
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Customer> all = new ArrayList<Customer>();
        Customer customer = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ALL_SQL, pstmt.RETURN_GENERATED_KEYS);
            rs = pstmt.executeQuery();
            con.setAutoCommit(false);

            while (rs.next()) {
                customer = new Customer();
                customer.setId(rs.getLong("customer_id"));
                customer.setFirstName(rs.getString("firstname"));
                customer.setLastName(rs.getString("lastname"));
                all.add(customer);
            }

            con.commit();

        } catch (Exception e) {
            if (con != null){
                con.rollback();
            }
            System.out.println("Get failure");
            throw new RuntimeException(e);
        } finally {
            close(rs, pstmt, con);
        }
        return all;
    }
}
