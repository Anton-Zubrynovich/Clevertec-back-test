package dao;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import entity.Account;
import entity.Bank;
import entity.Transaction;
import pdf.ReceiptPdfService;
import pdf.ReceiptPdfServiceImpl;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Transaction dao.
 */
public class TransactionDao implements Dao<Long, Transaction>{

    /**
     * The Deposit.
     */
    static final String DEPOSIT = "DEPOSIT";
    /**
     * The Withdraw.
     */
    static final String WITHDRAW = "WITHDRAW";
    /**
     * The Transfer.
     */
    static final String TRANSFER = "TRANSFER";

    /**
     * The Formatter for date.
     */
    static final DateTimeFormatter formatterForDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    /**
     * The Formatter for time.
     */
    static final DateTimeFormatter formatterForTime = DateTimeFormatter.ofPattern("HH:mm:ss");


    private static final TransactionDao INSTANCE = new TransactionDao();
    private static final String SAVE_SQL = """
            INSERT INTO transactions(date_of_transaction, transaction_type, amount, sender_account, receiver_account)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE transactions
            SET date_of_transaction = ?, transaction_type = ?, amount = ?, sender_account = ?, receiver_account = ?
            WHERE transaction_id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE from transactions
            WHERE transaction_id = ?
            """;

    private static final String SELECT_ONE_SQL = """
            SELECT *
            FROM transactions
            WHERE transaction_id = ?
            """;
    private static final String SELECT_ALL_SQL = """
            SELECT *
            FROM transactions
            ORDER By transaction_id ASC
            """;

    private static final String SELECT_LAST_SQL = """
            SELECT *
            FROM transactions
            ORDER By transaction_id DESC
            LIMIT 1
            """;


    private TransactionDao(){

    }

    /**
     * Get instance transaction dao.
     *
     * @return the transaction dao
     */
    public static TransactionDao getInstance(){
        return INSTANCE;
    }


    @Override
    public void save(Transaction transaction) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SAVE_SQL, pstmt.RETURN_GENERATED_KEYS);

            con.setAutoCommit(false);

            String pattern = "dd.MM.yyyy HH:mm:ss";
            String timestampAsString = transaction.getDateOfTransaction() + " " + transaction.getTimeOfTransaction();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timestampAsString));
            Timestamp timestamp = Timestamp.valueOf(localDateTime);

            if (transaction.getTransactionType().equals(DEPOSIT)
                    || transaction.getTransactionType().equals(WITHDRAW) ||
                    transaction.getTransactionType().equals(TRANSFER) &&
                    (transaction.getSenderAccount().toString().length() == 12) &&
                    (transaction.getReceiverAccount().toString().length() == 12)){

                pstmt.setTimestamp(1, timestamp);
                pstmt.setString(2, transaction.getTransactionType());
                pstmt.setDouble(3, transaction.getAmount());
                pstmt.setLong(4, transaction.getSenderAccount());
                pstmt.setLong(5, transaction.getReceiverAccount());
                pstmt.executeUpdate();
            }
            else {throw new RuntimeException();}

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
    public void update(Transaction transaction, String[] params) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(UPDATE_SQL);

            con.setAutoCommit(false);

            String pattern = "dd.MM.yyyy HH:mm:ss";
            String timestampAsString = transaction.getDateOfTransaction() + " " + transaction.getTimeOfTransaction();;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timestampAsString));
            Timestamp timestamp = Timestamp.valueOf(localDateTime);

            if (transaction.getTransactionType().equals(DEPOSIT)
                    || transaction.getTransactionType().equals(WITHDRAW) ||
                    transaction.getTransactionType().equals(TRANSFER) &&
                            (transaction.getSenderAccount().toString().length() == 12) &&
                            (transaction.getReceiverAccount().toString().length() == 12)) {

                pstmt.setTimestamp(1, timestamp);
                pstmt.setString(2, params[2]);
                pstmt.setDouble(3, Double.parseDouble(params[3]));
                pstmt.setLong(4, Long.parseLong(params[4]));
                pstmt.setLong(5, Long.parseLong(params[5]));
                pstmt.setLong(6, transaction.getId());
                pstmt.executeUpdate();

            }
            else {
                throw new RuntimeException();
            }

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
    public void delete(Transaction t) throws Exception {
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
    public Optional<Transaction> get(Long id) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Transaction transaction = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ONE_SQL, pstmt.RETURN_GENERATED_KEYS);

            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            con.setAutoCommit(false);


            if (rs.next()) {
                transaction = new Transaction();

                Timestamp timestampForDateAndTime = rs.getTimestamp("date_of_transaction");
                String timestampAsStringForDate = formatterForDate.format(timestampForDateAndTime.toLocalDateTime());
                String timestampAsStringForTime = formatterForTime.format(timestampForDateAndTime.toLocalDateTime());

                transaction.setId(rs.getLong("transaction_id"));
                transaction.setDateOfTransaction(timestampAsStringForDate);
                transaction.setTimeOfTransaction(timestampAsStringForTime);
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setSenderAccount(rs.getLong("sender_account"));
                transaction.setReceiverAccount(rs.getLong("receiver_account"));

                return Optional.of(transaction);
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
    public List<Transaction> getAll() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Transaction> all = new ArrayList<Transaction>();
        Transaction transaction = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_ALL_SQL, pstmt.RETURN_GENERATED_KEYS);
            rs = pstmt.executeQuery();
            con.setAutoCommit(false);

            while (rs.next()) {
                transaction = new Transaction();

                Timestamp timestampForDateAndTime = rs.getTimestamp("date_of_transaction");
                String timestampAsStringForDate = formatterForDate.format(timestampForDateAndTime.toLocalDateTime());
                String timestampAsStringForTime = formatterForTime.format(timestampForDateAndTime.toLocalDateTime());

                transaction.setId(rs.getLong("transaction_id"));
                transaction.setDateOfTransaction(timestampAsStringForDate);
                transaction.setTimeOfTransaction(timestampAsStringForTime);
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setSenderAccount(rs.getLong("sender_account"));
                transaction.setReceiverAccount(rs.getLong("receiver_account"));
                all.add(transaction);
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

    /**
     * Gets last.
     *
     * @return the last
     * @throws Exception the exception
     */
    public Optional<Transaction> getLast() throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Transaction transaction = null;

        try {
            con = ConnectionManager.get();
            pstmt = con.prepareStatement(SELECT_LAST_SQL, pstmt.RETURN_GENERATED_KEYS);
            rs = pstmt.executeQuery();

            con.setAutoCommit(false);


            if (rs.next()) {
                transaction = new Transaction();

                Timestamp timestampForDateAndTime = rs.getTimestamp("date_of_transaction");
                String timestampAsStringForDate = formatterForDate.format(timestampForDateAndTime.toLocalDateTime());
                String timestampAsStringForTime = formatterForTime.format(timestampForDateAndTime.toLocalDateTime());

                transaction.setId(rs.getLong("transaction_id"));
                transaction.setDateOfTransaction(timestampAsStringForDate);
                transaction.setTimeOfTransaction(timestampAsStringForTime);
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setSenderAccount(rs.getLong("sender_account"));
                transaction.setReceiverAccount(rs.getLong("receiver_account"));

                return Optional.of(transaction);
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

    /**
     * Withdrawal transaction.
     *
     * @param acc   the acc
     * @param value the value
     * @throws SQLException the sql exception
     */
    public void withdrawalTransaction(Account acc, double value) throws SQLException {
        final AccountDao accountDao = AccountDao.getInstance();

        String[] params = {String.valueOf(acc.getAccountNumber()), String.valueOf((acc.getBalance() - value)), String.valueOf(acc.getCustomerId()), String.valueOf(acc.getBankId()), String.valueOf(acc.getAccountNumber())};

        try{
            if(acc.getBalance() < value){
                System.out.println("Insufficient funds");
                return;
            }
            accountDao.update(acc, params);

            String timestampAsStringForDate = formatterForDate.format(LocalDateTime.now());
            String timestampAsStringForTime = formatterForTime.format(LocalDateTime.now());

            Transaction transaction = new Transaction(timestampAsStringForDate, timestampAsStringForTime, WITHDRAW, value, acc.getAccountNumber(), 0L);

            save(transaction);

            Optional<Transaction> transactionOptional = getLast();
            Transaction lastTransaction = new Transaction();
            if (transactionOptional.isPresent()){
                lastTransaction = transactionOptional.get();
            }


            ReceiptPdfService receiptPdf = new ReceiptPdfServiceImpl();
            Document document = receiptPdf.createDocument("src//check//check" + lastTransaction.getId() + ".pdf");
            receiptPdf.generatePdf(lastTransaction, document);


            System.out.println("Withdrawal " + value);
        } catch (Exception e) {
            System.out.println("Withdrawal failure");
            throw new RuntimeException(e);
        } finally {
            close(null, null, null);
        }
    }

    /**
     * Deposit transaction.
     *
     * @param acc   the acc
     * @param value the value
     */
    public  void depositTransaction(Account acc, double value){

        final AccountDao accountDao = AccountDao.getInstance();

        String[] params = {String.valueOf(acc.getAccountNumber()), String.valueOf((acc.getBalance() + value)), String.valueOf(acc.getCustomerId()), String.valueOf(acc.getBankId()), String.valueOf(acc.getAccountNumber())};

        try{

            accountDao.update(acc, params);

            String timestampAsStringForDate = formatterForDate.format(LocalDateTime.now());
            String timestampAsStringForTime = formatterForTime.format(LocalDateTime.now());

            Transaction transaction = new Transaction(timestampAsStringForDate, timestampAsStringForTime, DEPOSIT, value, acc.getAccountNumber(), 0L);

            save(transaction);

            Optional<Transaction> transactionOptional = getLast();
            Transaction lastTransaction = new Transaction();
            if (transactionOptional.isPresent()){
                lastTransaction = transactionOptional.get();
            }

            ReceiptPdfService receiptPdf = new ReceiptPdfServiceImpl();
            Document document = receiptPdf.createDocument("src//check//check" + lastTransaction.getId() + ".pdf");
            receiptPdf.generatePdf(lastTransaction, document);

            System.out.println("Deposit " + value);
        } catch (Exception e) {
            System.out.println("Deposit failure");
            throw new RuntimeException(e);
        } finally {
            close(null, null, null);
        }
    }

    /**
     * Transfer transaction.
     *
     * @param senderAccount   the sender account
     * @param receiverAccount the receiver account
     * @param value           the value
     */
    public void transferTransaction(Account senderAccount, Account receiverAccount, double value){
        final AccountDao accountDao = AccountDao.getInstance();

        String[] paramsForWithdraw = {String.valueOf(senderAccount.getAccountNumber()), String.valueOf((senderAccount.getBalance() - value)), String.valueOf(senderAccount.getCustomerId()), String.valueOf(senderAccount.getBankId()), String.valueOf(senderAccount.getAccountNumber())};
        String[] paramsForDeposit = {String.valueOf(receiverAccount.getAccountNumber()), String.valueOf((receiverAccount.getBalance() + value)), String.valueOf(receiverAccount.getCustomerId()), String.valueOf(receiverAccount.getBankId()), String.valueOf(receiverAccount.getAccountNumber())};

        try{
                if(senderAccount.getBalance() < value){
                    System.out.println("Insufficient funds");
                    return;
                }

            accountDao.update(senderAccount, paramsForWithdraw);
            accountDao.update(receiverAccount, paramsForDeposit);

            String timestampAsStringForDate = formatterForDate.format(LocalDateTime.now());
            String timestampAsStringForTime = formatterForTime.format(LocalDateTime.now());

            Transaction transaction = new Transaction(timestampAsStringForDate, timestampAsStringForTime, TRANSFER, value, senderAccount.getAccountNumber(), receiverAccount.getAccountNumber());

            save(transaction);

            Optional<Transaction> transactionOptional = getLast();
            Transaction lastTransaction = new Transaction();
            if (transactionOptional.isPresent()){
                lastTransaction = transactionOptional.get();
            }

            ReceiptPdfService receiptPdf = new ReceiptPdfServiceImpl();
            Document document = receiptPdf.createDocument("src//check//check" + lastTransaction.getId() + ".pdf");
            receiptPdf.generatePdf(lastTransaction, document);


            System.out.println("Transfer " + value);
        } catch (Exception e) {
            System.out.println("Transfer failure");
            throw new RuntimeException(e);
        } finally {
            close(null, null, null);
        }
    }
}
