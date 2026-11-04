public class Semaphore {
    private int value;
    private int wakeups = 0; // Ensures signals are not lost or double-counted during timeouts

    public Semaphore(int initialValue) {
        this.value = initialValue;
    }

    /**
     * Standard wait operation P() / acquire()
     */
    public synchronized void P() {
        value--;
        if (value < 0) {
            while (wakeups <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            wakeups--;
        }
    }

    /**
     * Standard signal operation V() / release()
     */
    public synchronized void V() {
        value++;
        if (value <= 0) {
            wakeups++;
            notify();
        }
    }

    /**
     * Try to acquire a resource within a given time limit.
     * @param timeoutMs Timeout in milliseconds (e.g., 3000ms for 3 seconds)
     * @return true if acquired successfully, false if timed out
     */
    public synchronized boolean tryP(long timeoutMs) {
        value--;
        if (value >= 0) {
            return true; // Resource was immediately available
        }

        long deadline = System.currentTimeMillis() + timeoutMs;

        while (wakeups <= 0) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                // Timeout expired without receiving a signal
                value++; // Undo decrement
                return false;
            }

            try {
                wait(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Successfully received a V() signal before or at timeout
        wakeups--;
        return true;
    }

    public synchronized int getValue() {
        return value;
    }
}
