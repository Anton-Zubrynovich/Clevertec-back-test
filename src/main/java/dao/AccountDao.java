package dao;

import entity.Account;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Account dao.
 */
public class AccountDao implements Dao<Long, Account>{

    private static final AccountDao INSTANCE = new AccountDao();
    private static final String SAVE_SQL = """
            INSERT INTO account(account_number, balance, customer_id, bank_id)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE account
            SET account_number = ?, balance = ?, customer_id = ?, bank_id = ?
            WHERE account_number = ?
            """;
    private static final String DELETE_SQL = """
            DELETE from account
            WHERE account_number = ?
            """;

    private static final String SELECT_ONE_SQL = """
            SELECT *
            FROM account
            WHERE account_number = ?
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT *
            FROM account
            ORDER By account_number ASC
            """;

    private AccountDao(){

    }

    /**
     * Get instance account dao.
     *
     * @return the account dao
     */
    public static AccountDao getInstance(){
        return INSTANCE;
    }


    public void save(Account account) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SAVE_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            if (account.getAccountNumber().toString().length() == 12) {
                pstmt.setLong(1, account.getAccountNumber());
                pstmt.setDouble(2, account.getBalance());
                pstmt.setLong(3, account.getCustomerId());
                pstmt.setLong(4, account.getBankId());
                pstmt.executeUpdate();
            }

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

    public void update(Account t, String[] params) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(UPDATE_SQL);

            con.setAutoCommit(false);

            pstmt.setLong(1, Long.parseLong(params[0]));
            pstmt.setDouble(2, Double.parseDouble(params[1]));
            pstmt.setLong(3, Long.parseLong(params[2]));
            pstmt.setLong(4, Long.parseLong(params[3]));
            pstmt.setLong(5, t.getAccountNumber());
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

    public void delete(Account t) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(DELETE_SQL);

            con.setAutoCommit(false);

            pstmt.setLong(1, t.getAccountNumber());
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


    public Optional<Account> get(Long accountNumber) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Account account = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ONE_SQL, pstmt.RETURN_GENERATED_KEYS);

            pstmt.setLong(1, accountNumber);
            rs = pstmt.executeQuery();

            con.setAutoCommit(false);

            if (rs.next()) {
                account = new Account();
                account.setAccountNumber(rs.getLong("account_number"));
                account.setBalance(rs.getDouble("balance"));
                account.setCustomerId(rs.getLong("customer_id"));
                account.setBankId(rs.getLong("bank_id"));

                return Optional.of(account);
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

    public List<Account> getAll() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Account> all = new ArrayList<Account>();
        Account account = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ALL_SQL, pstmt.RETURN_GENERATED_KEYS);
            rs = pstmt.executeQuery();
            con.setAutoCommit(false);

            while (rs.next()) {
                account = new Account();
                account.setAccountNumber(rs.getLong("account_number"));
                account.setBalance(rs.getDouble("balance"));
                account.setCustomerId(rs.getLong("customer_id"));
                account.setBankId(rs.getLong("bank_id"));
                all.add(account);
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
