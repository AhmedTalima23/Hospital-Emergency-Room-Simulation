public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Testing Mutual Exclusion (P / V) ===");
        testMutualExclusion();

        Thread.sleep(1000);
        System.out.println("\n=== 2. Testing tryP Timeout (Failure Case) ===");
        testTryPTimeout();

        Thread.sleep(1000);
        System.out.println("\n=== 3. Testing tryP Success (V arrives in time) ===");
        testTryPSuccess();
    }

    // 1. Test Mutual Exclusion for multiple threads on Semaphore(1)
    private static void testMutualExclusion() throws InterruptedException {
        Semaphore sem = new Semaphore(1);
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int id = i + 1;
            threads[i] = new Thread(() -> {
                sem.P();
                System.out.println("Thread " + id + " entered critical section.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                System.out.println("Thread " + id + " leaving critical section.");
                sem.V();
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Mutual Exclusion Test Completed Successfully.");
    }

    // 2. Test tryP timeout failure returning false after deadline
    private static void testTryPTimeout() throws InterruptedException {
        Semaphore sem = new Semaphore(0); // No permits available

        long start = System.currentTimeMillis();
        boolean acquired = sem.tryP(1000); // Try for 1 second
        long duration = System.currentTimeMillis() - start;

        System.out.println("Acquired resource: " + acquired);
        System.out.println("Time elapsed: " + duration + " ms (Expected ~1000ms)");
        System.out.println("Semaphore final value: " + sem.getValue() + " (Expected 0)");
    }

    // 3. Test tryP success when V() arrives before timeout
    private static void testTryPSuccess() throws InterruptedException {
        Semaphore sem = new Semaphore(0);

        new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println("Signaling V() now...");
                sem.V();
            } catch (InterruptedException ignored) {}
        }).start();

        long start = System.currentTimeMillis();
        boolean acquired = sem.tryP(2000); // 2 second timeout
        long duration = System.currentTimeMillis() - start;

        System.out.println("Acquired resource: " + acquired);
        System.out.println("Time elapsed: " + duration + " ms (Expected ~500ms)");
        System.out.println("Semaphore final value: " + sem.getValue() + " (Expected 0)");
    }
}
