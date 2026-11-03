
public class TreatmentRooms {
    private final Semaphore rooms = new Semaphore(2);

    public boolean tryAcquire(long timeoutMs) {
        return rooms.tryP(timeoutMs);
    }

    public void release() {
        rooms.V();
    }
}