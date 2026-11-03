public class WaitingRoom {
    private final Patient[] store;
    private final int capacity;
    private int inptr = 0, outptr = 0;

    private final Semaphore spaces;
    private final Semaphore elements;
    private final Semaphore mutex;

    private final SimulationState state;

    public WaitingRoom(int cap, SimulationState state) {
        this.capacity = cap;
        this.store = new Patient[cap];
        this.spaces = new Semaphore(cap);
        this.elements = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.state = state;
    }
    private void insert(Patient p) {
        mutex.P();

        try {
            store[inptr] = p;
            inptr = (inptr + 1) % capacity;

            Logger.log(p + " entered the waiting room.");

        } finally {
            mutex.V();
        }

        elements.V();
    }

    public void enter(Patient p) {
        spaces.P();
        insert(p);
    }

    public boolean tryEnter(Patient p) {
        if (!spaces.tryP(0)) return false;
        insert(p);
        return true;
    }
    public Patient take() {
        while (true) {
            if (elements.tryP(200)) {
                mutex.P();
                Patient p;
                try {
                    p = store[outptr];
                    store[outptr] = null;
                    outptr = (outptr + 1) % capacity;
                } finally {
                    mutex.V();
                }
                spaces.V();
                return p;
            }
            if (state.allDone()) return null;
        }
    }
}
