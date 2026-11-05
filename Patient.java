public class Patient extends Thread {
    private final int id;
    private final WaitingRoom room;

    public Patient(int id, WaitingRoom r) {
        this.id = id;
        this.room = r;
    }

    @Override
    public void run() {
        enterWaitingRoom(true);
    }

    public void enterWaitingRoom(boolean firstTime) {

        if (firstTime) {
            Logger.log(toString() + " arrived.");
        }

        if (room.tryEnter(this)) {
            return;
        }

        Logger.log(
                "The waiting room is full.",
                toString() + " is waiting outside for an available seat."
        );

        room.enter(this);
    }

    public void sendBack() {
        if (room.tryEnter(this)) {
            return;
        }

        Logger.log(
                "The waiting room is full.",
                toString() + " is waiting outside for an available seat."
        );

        new Thread(() -> room.enter(this)).start();
    }

    @Override
    public String toString() { return "P" + id; }
}