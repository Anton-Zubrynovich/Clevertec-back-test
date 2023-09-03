package EntityUi;

import dao.BankDao;
import entity.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * The type Bank ui.
 */
public class BankUi implements UtilUi{

    private final BankDao bank = BankDao.getInstance();

    public void startWork() {

        String menu = null;

        do {
            System.out.println(" ** Please select menu ** ");
            System.out.println(" 1 ** New bank registration    ");
            System.out.println(" 2 ** All banks    ");
            System.out.println(" 3 ** get bank by id    ");
            System.out.println(" 4 ** update bank   ");
            System.out.println(" 5 ** delete bank    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");

            try {
                menu = readCommandLine();

                String bankName = null;
                String id = null;

                if (menu.equals("1")) {
                    //TODO New Customer registration
                    System.out.println("Enter name of the bank you want to create: ");
                    bankName = this.readCommandLine();
                    Bank b = new Bank(bankName);
                    bank.save(b);

                } else if (menu.equals("2")) {
                    //TODO All Customers
                    List<Bank> banks = bank.getAll();
                    for (Bank bank : banks) {
                        System.out.println(bank);
                    }
                } else if (menu.equals("3")) {
                    //TODO Get Customer
                    System.out.println("Please enter id of the bank: ");
                    id = this.readCommandLine();
                    System.out.println(bank.get(Long.parseLong(id)));

                } else if (menu.equals("4")) {
                    //TODO Update Customer
                    System.out.println("Please enter id of the bank you want to update: ");
                    id = this.readCommandLine();
                    System.out.println("Please enter new name of the bank: ");
                    bankName = this.readCommandLine();
                    String[] params = {bankName};
                    Optional<Bank> bankOptional = bank.get(Long.parseLong(id));
                    Bank b = bankOptional.get();
                    bank.update(b, params);

                } else if (menu.equals("5")) {
                    System.out.println("Please enter id of the bank you want to delete: ");
                    id = this.readCommandLine();
                    Optional<Bank> bankOptional = bank.get(Long.parseLong(id));
                    Bank b = bankOptional.get();
                    bank.delete(b);
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
        UtilUi ui = new BankUi();
        ui.startWork();
    }

}
