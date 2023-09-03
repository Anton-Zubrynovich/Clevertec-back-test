package EntityUi;

import dao.AccountDao;
import dao.BankDao;
import dao.TransactionDao;
import entity.Account;
import entity.Bank;
import entity.Transaction;
import util.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * The type Transaction ui.
 */
public class TransactionUi implements UtilUi{

    private final TransactionDao transaction = TransactionDao.getInstance();
    private final AccountDao accountDao = AccountDao.getInstance();


    public void startWork() {

        String menu = null;

        do {
            System.out.println(" ** Please select menu ** ");
            System.out.println(" 1 ** New transaction registration:    ");
            System.out.println(" 2 ** All transactions:    ");
            System.out.println(" 3 ** Get transaction by id:    ");
            System.out.println(" 4 ** Update transaction:  ");
            System.out.println(" 5 ** Delete transaction:    ");
            System.out.println(" 6 ** Withdrawal transaction:    ");
            System.out.println(" 7 ** Deposit transaction:    ");
            System.out.println(" 8 ** Transfer transaction:    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");

            try {
                menu = readCommandLine();

                String id = null;
                String dateOfTransaction = null;
                String timeOfTransaction = null;
                String transactionType = null;
                String senderAccount = null;
                String receiverAccount = null;
                String amount = null;

                if (menu.equals("1")) {

                    System.out.println("Enter date of transaction in dd.mm.yyyy format: ");
                    dateOfTransaction = this.readCommandLine();
                    System.out.println("Enter time of transaction in hh.mm.ss format: ");
                    timeOfTransaction = this.readCommandLine();
                    System.out.println("Enter type of the transaction: ");
                    transactionType = this.readCommandLine();
                    System.out.println("Enter amount of money: ");
                    amount = this.readCommandLine();
                    System.out.println("Enter sender account number: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Enter receiver account number: ");
                    receiverAccount = this.readCommandLine();

                    Transaction tran = new Transaction(dateOfTransaction,timeOfTransaction, transactionType.toUpperCase(), Double.parseDouble(amount),Long.parseLong(senderAccount), Long.parseLong(receiverAccount));
                    transaction.save(tran);

                } else if (menu.equals("2")) {

                    List<Transaction> transactions = transaction.getAll();
                    for (Transaction t : transactions) {
                        System.out.println(t);
                    }
                } else if (menu.equals("3")) {

                    System.out.println("Please enter transaction id: ");
                    id = this.readCommandLine();
                    System.out.println(transaction.get(Long.parseLong(id)));

                } else if (menu.equals("4")) {

                    System.out.println("Please enter id of the transaction you want to update: ");
                    id = this.readCommandLine();
                    System.out.println("Please enter new date of the transaction: ");
                    dateOfTransaction = this.readCommandLine();
                    System.out.println("Please enter new time of the transaction: ");
                    timeOfTransaction = this.readCommandLine();
                    System.out.println("Please enter new type of the transaction: ");
                    transactionType = this.readCommandLine();
                    System.out.println("Please enter new amount of money: ");
                    amount = this.readCommandLine();
                    System.out.println("Please enter new sender of the transaction: (12 digits)");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter new receiver of the transaction: (12 digits)");
                    receiverAccount = this.readCommandLine();
                    String[] params = {dateOfTransaction, timeOfTransaction, transactionType.toUpperCase(), amount, senderAccount, receiverAccount};
                    Optional<Transaction> transactionOptional = transaction.get(Long.parseLong(id));
                    Transaction tran = transactionOptional.get();
                    transaction.update(tran, params);

                } else if (menu.equals("5")) {
                    System.out.println("Please enter id of the transaction you want to delete: ");
                    id = this.readCommandLine();
                    Optional<Transaction> transactionOptional = transaction.get(Long.parseLong(id));
                    Transaction tran = transactionOptional.get();
                    transaction.delete(tran);
                }
                else if (menu.equals("6")){
                    System.out.println("Please enter account number from which you want to withdraw money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want to withdraw: ");
                    amount = this.readCommandLine();
                    Optional<Account> accountOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account acc = accountOptional.get();
                    transaction.withdrawalTransaction(acc, Double.parseDouble(amount));
                }
                else if (menu.equals("7")){
                    System.out.println("Please enter account number on which you want to deposit money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want to deposit: ");
                    amount = this.readCommandLine();
                    Optional<Account> accountOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account acc = accountOptional.get();
                    transaction.depositTransaction(acc, Double.parseDouble(amount));
                }
                else if (menu.equals("8")){
                    System.out.println("Please enter account number from which you want to withdraw money: ");
                    senderAccount = this.readCommandLine();
                    System.out.println("Please enter account number on which you want to refill money: ");
                    receiverAccount = this.readCommandLine();
                    System.out.println("Please enter amount of money you want tot withdraw: ");
                    amount = this.readCommandLine();
                    Optional<Account> senderAccOptional = accountDao.get(Long.parseLong(senderAccount));
                    Account senderAcc = senderAccOptional.get();
                    Optional<Account> receiverAccOptional = accountDao.get(Long.parseLong(receiverAccount));
                    Account receiverAcc = receiverAccOptional.get();
                    transaction.transferTransaction(senderAcc, receiverAcc, Double.parseDouble(amount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } while (!menu.equals("q"));

    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        UtilUi ui = new TransactionUi();
            ui.startWork();
    }
}
