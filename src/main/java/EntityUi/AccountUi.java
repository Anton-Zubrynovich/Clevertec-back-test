package EntityUi;

import dao.AccountDao;
import dao.BankDao;
import entity.Account;
import entity.Bank;
import util.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * The type Account ui.
 */
public class AccountUi implements UtilUi{

    private final AccountDao account = AccountDao.getInstance();

    public void startWork() {

        String menu = null;

        do {
            System.out.println(" ** Please select menu ** ");
            System.out.println(" 1 ** New account registration    ");
            System.out.println(" 2 ** All accounts    ");
            System.out.println(" 3 ** get account by account number    ");
            System.out.println(" 4 ** update account   ");
            System.out.println(" 5 ** delete account    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");

            try {
                menu = readCommandLine();

                String accountNumber = null;
                String balance = null;
                String customerId = null;
                String bankId = null;

                if (menu.equals("1")) {

                    System.out.println("Enter number of the account you want to create: (12 digits)");
                    accountNumber = this.readCommandLine();
                    System.out.println("Enter balance of the account: ");
                    balance = this.readCommandLine();
                    System.out.println("Enter customer id: ");
                    customerId = this.readCommandLine();
                    System.out.println("Enter bank id: ");
                    bankId = this.readCommandLine();

                    Account acc = new Account(Long.parseLong(accountNumber), Double.parseDouble(balance), Long.parseLong(customerId), Long.parseLong(bankId));
                    account.save(acc);

                } else if (menu.equals("2")) {

                    List<Account> accounts = account.getAll();
                    for (Account a : accounts) {
                        System.out.println(a);
                    }
                } else if (menu.equals("3")) {

                    System.out.println("Please enter account number: ");
                    accountNumber = this.readCommandLine();

                    Optional<Account> accountOptional = account.get(Long.parseLong(accountNumber));
                    Account acc = accountOptional.get();

                    System.out.println(acc);

                } else if (menu.equals("4")) {

                    System.out.println("Please enter number of the account you want to update: ");
                    String oldAccountNumber = this.readCommandLine();
                    System.out.println("Please enter new number of the account: ");
                    accountNumber = this.readCommandLine();
                    System.out.println("Please enter new balance of the account: ");
                    balance = this.readCommandLine();
                    System.out.println("Please enter new customerId of the account: ");
                    customerId = this.readCommandLine();
                    System.out.println("Please enter new bankId of the account: ");
                    bankId = this.readCommandLine();
                    String[] params = {accountNumber, balance, customerId, bankId};
                    Optional<Account> accountOptional = account.get(Long.parseLong(oldAccountNumber));
                    Account acc = accountOptional.get();
                    account.update(acc, params);

                } else if (menu.equals("5")) {
                    System.out.println("Please enter number of the account you want to delete: ");
                    accountNumber = this.readCommandLine();
                    Optional<Account> accountOptional = account.get(Long.parseLong(accountNumber));
                    Account acc = accountOptional.get();
                    account.delete(acc);
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
        UtilUi ui = new AccountUi();
            ui.startWork();
    }
}