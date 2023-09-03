package Threads;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * The type Check for end of month.
 */
public class CheckForEndOfMonth implements Runnable{

    /**
     * The constant time.
     */
    public static volatile boolean time = false;
    public void run() {

        YearMonth month = YearMonth.now();
        if (LocalDate.now().equals(month.atEndOfMonth())) {
            if(!DepositInterest.block) {
                time = true;
            }
        } else {
            DepositInterest.block = false;
        }
    }
}
