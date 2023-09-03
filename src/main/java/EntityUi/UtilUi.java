package EntityUi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The interface Util ui.
 */
public interface UtilUi {
    /**
     * Read command line string.
     *
     * @return the string
     * @throws IOException the io exception
     */
    default String readCommandLine() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }

    /**
     * Start work.
     */
    void startWork();


}
