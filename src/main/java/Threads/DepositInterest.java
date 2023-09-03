package Threads;

import dao.AccountDao;
import dao.TransactionDao;
import entity.Account;
import entity.Transaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Deposit interest.
 */
public class DepositInterest implements Runnable{

    /**
     * The constant block.
     */
    public static volatile boolean block = false;
    public void run() {
        if(block || !CheckForEndOfMonth.time ){return;}
        try {
            System.getProperties().load(ClassLoader.getSystemResourceAsStream("config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String percent = System.getProperty("percent");
        double temp = Double.parseDouble(percent) / 100;
        AccountDao accountDao = AccountDao.getInstance();
        TransactionDao transactionDao = TransactionDao.getInstance();
        List<Account> accounts = null;
        try {
            accounts = accountDao.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for(Account account: accounts){
            transactionDao.depositTransaction(account, (account.getBalance() * temp));
        }
        CheckForEndOfMonth.time = false;
        block = true;
    }
}
