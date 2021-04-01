package Code;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Utils {

    public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Parameters.threadPoolSize);

}
