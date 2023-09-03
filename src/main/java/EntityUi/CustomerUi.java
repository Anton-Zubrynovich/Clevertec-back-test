package EntityUi;

import dao.CustomerDao;
import entity.Customer;
import util.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * The type Customer ui.
 */
public class CustomerUi implements UtilUi{

    private final CustomerDao cust = CustomerDao.getInstance();


    public void startWork() {

        String menu = null;

        do {
            System.out.println(" ** Please select menu ** ");
            System.out.println(" 1 ** New customer registration    ");
            System.out.println(" 2 ** All customers    ");
            System.out.println(" 3 ** get customer by id    ");
            System.out.println(" 4 ** update customer    ");
            System.out.println(" 5 ** delete customer    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");

            try {
                menu = readCommandLine();

                String firstName = null;
                String lastName = null;
                String id = null;

                if (menu.equals("1")) {
                    //TODO New Customer registration
                    System.out.println("Enter firstname of the customer you want to create: ");
                    firstName = this.readCommandLine();
                    System.out.println("Enter lastname of the customer you want to create: ");
                    lastName = this.readCommandLine();
                    Customer customer = new Customer(firstName, lastName);
                    cust.save(customer);
                } else if (menu.equals("2")) {
                    //TODO All Customers
                    List<Customer> customers = cust.getAll();
                    for (Customer customer : customers) {
                        System.out.println(customer);
                    }
                } else if (menu.equals("3")) {
                    //TODO Get Customer
                    System.out.println("Please enter id of the customer: ");
                    id = this.readCommandLine();
                    System.out.println(cust.get(Long.parseLong(id)));

                } else if (menu.equals("4")) {
                    //TODO Update Customer
                    System.out.println("Please enter id of the customer you want to update: ");
                    id = this.readCommandLine();
                    System.out.println("Please enter new firstname: ");
                    firstName = this.readCommandLine();
                    System.out.println("Please enter new lastname: ");
                    lastName = this.readCommandLine();
                    String[] params = {firstName, lastName};
                    Optional<Customer> customerOptional = cust.get(Long.parseLong(id));
                    Customer customer = customerOptional.get();
                    cust.update(customer, params);

                } else if (menu.equals("5")) {
                    System.out.println("Please enter id of the customer you want to delete: ");
                    id = this.readCommandLine();
                    Optional<Customer> customerOptional = cust.get(Long.parseLong(id));
                    Customer customer = customerOptional.get();
                    cust.delete(customer);
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
        UtilUi ui = new CustomerUi();
            ui.startWork();
    }
}
