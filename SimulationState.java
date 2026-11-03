
public class SimulationState {
    private final int totalPatients;
    private int patientsLeft = 0;

    public SimulationState(int totalPatients) {
        this.totalPatients = totalPatients;
    }

    public synchronized void patientLeft() {
        patientsLeft++;
        if (patientsLeft == totalPatients) {
            notifyAll();                
        }
    }

    public synchronized boolean allDone() {
        return patientsLeft == totalPatients;
    }

    public synchronized void awaitAllDone() throws InterruptedException {
        while (patientsLeft < totalPatients) wait();
    }
}