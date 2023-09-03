package dao;

import entity.Bank;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Bank dao.
 */
public class BankDao implements Dao<Long, Bank>{

    private static final BankDao INSTANCE = new BankDao();
    private static final String SAVE_SQL = """
            INSERT INTO bank(bank_name)
            VALUES (?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE bank
            SET bank_name = ?
            WHERE bank_id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE from bank
            WHERE bank_id = ?
            """;

    private static final String SELECT_ONE_SQL = """
            SELECT *
            FROM bank
            WHERE bank_id = ?
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT *
            FROM bank
            ORDER By bank_id ASC
            """;

    private BankDao(){
    }

    /**
     * Get instance bank dao.
     *
     * @return the bank dao
     */
    public static BankDao getInstance(){
        return INSTANCE;
    }


    public void save(Bank bank) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SAVE_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            pstmt.setString(1, bank.getBankName());
            pstmt.executeUpdate();

            con.commit();

        } catch (Exception e) {
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
    public void update(Bank t, String[] params) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(UPDATE_SQL);

            con.setAutoCommit(false);

            pstmt.setString(1, params[0]);
            pstmt.setLong(2, t.getId());
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
    public void delete(Bank t) throws SQLException {
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
    public Optional<Bank> get(Long id) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Bank bank = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ONE_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            con.commit();

            if (rs.next()) {
                bank = new Bank();
                bank.setId(rs.getLong("bank_id"));
                bank.setBankName(rs.getString("bank_name"));
                return Optional.of(bank);

            }
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
    public List<Bank> getAll() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Bank> all = new ArrayList<Bank>();
        Bank bank = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ALL_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                bank = new Bank();
                bank.setId(rs.getLong("bank_id"));
                bank.setBankName(rs.getString("bank_name"));
                all.add(bank);
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

