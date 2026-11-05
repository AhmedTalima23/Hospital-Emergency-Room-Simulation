import java.util.Scanner;


public class Hospital {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        int capacity = readPositiveInt(in, "Waiting room capacity: ");
        int numDoctors = readPositiveInt(in, "Number of doctors: ");
        int numPatients = readPositiveInt(in, "Number of patients: ");
        System.out.println();

        SimulationState state = new SimulationState(numPatients);
        WaitingRoom waitingRoom = new WaitingRoom(capacity, state);
        TreatmentRooms treatmentRooms = new TreatmentRooms();

        Doctor[] doctors = new Doctor[numDoctors];
        for (int i = 0; i < numDoctors; i++) {
            doctors[i] = new Doctor(i + 1, waitingRoom, treatmentRooms, state);
        }

        Patient[] patients = new Patient[numPatients];
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < numPatients; i++) {
            patients[i] = new Patient(i + 1, waitingRoom);
            names.append(patients[i]);
            if (i < numPatients - 1) {
                names.append(' ');
            }
        }

        Logger.log("Patients arriving:");
        Logger.log(names.toString());

        for (Doctor d : doctors) {
            d.start();
        }

        for (int i = 0; i < patients.length; i++) {
            patients[i].start();
            sleepQuietly(500); // Simulate arrival intervals between incoming patients
        }

        joinAll(patients);
        try {
            state.awaitAllDone();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Logger.log("All patients have been treated.");

        joinAll(doctors);
        Logger.log("Simulation completed.");
    }

    private static int readPositiveInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNext()) {
                System.err.println("No input available.");
                System.exit(1);
            }
            String token = in.next();
            try {
                int value = Integer.parseInt(token);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Please enter a positive integer.");
        }
    }

    private static void joinAll(Thread[] threads) {
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
