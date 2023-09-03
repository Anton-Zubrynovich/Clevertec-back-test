package app;

import EntityUi.UtilUi;
import dao.AccountDao;
import dao.TransactionDao;
import entity.Account;
import entity.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The type Banking app ui.
 */
public class BankingAppUi implements UtilUi {

    private final TransactionDao transaction = TransactionDao.getInstance();
    private final AccountDao accountDao = AccountDao.getInstance();


    @Override
    public void startWork() {
        String menu = null;

        do {
            System.out.println(" ** Please select menu ** ");
            System.out.println(" 1 ** List accounts    ");
            System.out.println(" 2 ** Withdrawal transaction:    ");
            System.out.println(" 3 ** Deposit transaction:    ");
            System.out.println(" 4 ** Transfer transaction:    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");

            try {
                menu = readCommandLine();

                String senderAccount = null;
                String receiverAccount = null;
                String amount = null;

                if (menu.equals("1")){
                    List<Account> accounts = accountDao.getAll();
                    for (Account a : accounts) {
                        System.out.println(a);
                    }
                }
                else if (menu.equals("2")){
                    System.out.println("Please enter account number from which you want to withdraw money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want to withdraw: ");
                    amount = this.readCommandLine();
                    Optional<Account> accountOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account acc = new Account();
                    if (accountOptional.isPresent()){
                        acc = accountOptional.get();
                    }
                    transaction.withdrawalTransaction(acc, Double.parseDouble(amount));
                }
                else if (menu.equals("3")){
                    System.out.println("Please enter account number on which you want to deposit money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want to deposit: ");
                    amount = this.readCommandLine();
                    Optional<Account> accountOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account acc = new Account();
                    if (accountOptional.isPresent()){
                        acc = accountOptional.get();
                    }
                    transaction.depositTransaction(acc, Double.parseDouble(amount));
                }
                else if (menu.equals("4")){
                    System.out.println("Please enter account number from which you want to withdraw money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter account number on which you want to refill money: ");
                    receiverAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want tot withdraw: ");
                    amount = this.readCommandLine();
                    Optional<Account> senderAccOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account senderAcc = new Account();
                    if (senderAccOptional.isPresent()){
                        senderAcc = senderAccOptional.get();
                    }
                    Optional<Account> receiverAccOptional = accountDao.get(Long.parseLong(receiverAccount));
                    Account receiverAcc = new Account();
                    if (receiverAccOptional.isPresent()){
                        receiverAcc = receiverAccOptional.get();
                    }
                    transaction.transferTransaction(senderAcc, receiverAcc, Double.parseDouble(amount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } while (!menu.equals("q"));
    }
}
