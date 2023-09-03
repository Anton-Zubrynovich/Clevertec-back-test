package app;

import EntityUi.*;
import Threads.CheckForEndOfMonth;
import Threads.DepositInterest;
import util.ConnectionManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The type Main page ui.
 */
public class MainPageUi {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();

        List<UtilUi> utilUiList = new ArrayList<>();

        utilUiList.add(new BankingAppUi());
        utilUiList.add(new AccountUi());
        utilUiList.add(new BankUi());
        utilUiList.add(new CustomerUi());
        utilUiList.add(new TransactionUi());

        MainPageUi mainPageUi = new MainPageUi();
        try{
            mainPageUi.startWork(utilUiList, scheduler, scheduler2);
        }
        finally {
            ConnectionManager.closePool();
        }
    }


    /**
     * Start work.
     *
     * @param utilUiList the util ui list
     * @param scheduler  the scheduler
     * @param scheduler2 the scheduler 2
     */
    public void startWork(List<UtilUi> utilUiList, ScheduledExecutorService scheduler, ScheduledExecutorService scheduler2) {
        String menu = null;
        scheduler.scheduleAtFixedRate(new CheckForEndOfMonth(), 0, 30, TimeUnit.SECONDS);
        scheduler2.scheduleAtFixedRate(new DepositInterest(), 0, 30, TimeUnit.SECONDS);
        do {
            System.out.println(" ** Please choose Ui ** ");
            System.out.println(" 1 ** BankingAppUi    ");
            System.out.println(" 2 ** AccountUi    ");
            System.out.println(" 3 ** BankUi    ");
            System.out.println(" 4 ** CustomerUi   ");
            System.out.println(" 5 ** TransactionUi    ");
            System.out.println(" q ** Quit    ");
            System.out.println(" ********************** ");
            System.out.println(">>");


            try {
                menu = readCommandLine();

                if (menu.equals("1")) {

                    utilUiList.get(0).startWork();

                } else if (menu.equals("2")) {

                    utilUiList.get(1).startWork();

                } else if (menu.equals("3")) {

                    utilUiList.get(2).startWork();

                } else if (menu.equals("4")) {

                    utilUiList.get(3).startWork();
            } else if (menu.equals("5")) {

                utilUiList.get(4).startWork();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } while (!menu.equals("q"));
        scheduler.close();
        scheduler2.close();
        System.out.println("over");
    }

    private String readCommandLine() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }
}
