
public class Logger {
    public static synchronized void log(String... messages) {
        for (String m : messages) System.out.println(m);
    }
}