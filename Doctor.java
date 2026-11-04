public class Doctor extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRooms treatmentRooms;
    private final SimulationState state;

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms) {
        this(id, waitingRoom, treatmentRooms, null);
    }

    public Doctor(int id, WaitingRoom waitingRoom, TreatmentRooms treatmentRooms, SimulationState state) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.state = state;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Patient p = waitingRoom.take();
            if (p == null) {
                break;
            }

            Logger.log("Doctor " + id + " is assigned to " + p + ".");

            Logger.log("Doctor " + id + " is trying to acquire a treatment room.");

            if (treatmentRooms.tryAcquire(3000)) {
                try {
                    Logger.log("Doctor " + id + " acquired a treatment room.");
                    Logger.log("Doctor " + id + " begins treating " + p + ".");

                    try {
                        int treatmentDuration = 3500 + (int) (Math.random() * 2500);
                        Thread.sleep(treatmentDuration);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    Logger.log("Doctor " + id + " finished treating " + p + ".");
                } finally {
                    treatmentRooms.release();
                    Logger.log("Doctor " + id + " released the treatment room.");
                }

                Logger.log(p + " left.");
                if (state != null) {
                    state.patientLeft();
                }

                Logger.log("Doctor " + id + " is now available.");
            } else {
                Logger.log("Doctor " + id + " could not acquire a treatment room.");
                Logger.log("Doctor " + id + " is sending " + p + " back to the waiting room.");

                p.sendBack();

                Logger.log("Doctor " + id + " is now available.");
            }
        }
    }
}
