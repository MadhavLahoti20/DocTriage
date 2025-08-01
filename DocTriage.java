import java.io.*;
import java.util.*;

public class DocTriage {
    static Scanner sc = new Scanner(System.in);
    static Map<Integer, Doctor> doctors = new HashMap<>();
    static Map<Integer, Patient> patients = new HashMap<>();
    static List<Appointment> appointments = new ArrayList<>();
    static Map<String, String> issueToSpecialization = Map.of(
        "fever", "General Physician",
        "cold", "General Physician",
        "toothache", "Dentist",
        "fracture", "Orthopedic",
        "skin", "Dermatologist",
        "heart", "Cardiologist",
        "stomach", "Gastroenterologist",
        "eye", "Ophthalmologist",
        "anxiety", "Psychiatrist"
    );

    static Set<String> registeredEmails = new HashSet<>();

    public static void main(String[] args) {
        loadFromCSV();
        System.out.println("\nWelcome to Hospital Management System");
        while (true) {
            System.out.println("\n1. Admin Login\n2. Doctor Login\n3. Patient Login\n4. Patient Register\n5. Exit");
            int choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> doctorLogin();
                case 3 -> patientLogin();
                case 4 -> registerPatient();
                case 5 -> {
                    saveToCSV();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void adminLogin() {
        System.out.print("Enter admin password: ");
        String pass = sc.nextLine();
        if (!pass.equals("admin123")) {
            System.out.println("Wrong password"); return;
        }
        while (true) {
            System.out.println("\nAdmin Menu:\n1. Add Doctor\n2. View All Doctors\n3. View All Patients\n4. Logout");
            int ch = sc.nextInt(); sc.nextLine();
            switch (ch) {
                case 1 -> addDoctor();
                case 2 -> doctors.values().forEach(System.out::println);
                case 3 -> patients.values().forEach(System.out::println);
                case 4 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    static void addDoctor() {
        System.out.print("Enter doctor name: "); String name = sc.nextLine();
        System.out.print("Specialization: "); String spec = sc.nextLine();
        System.out.print("Availability (e.g. 10AM-2PM): "); String time = sc.nextLine();
        System.out.print("Patient Limit: "); int limit = sc.nextInt(); sc.nextLine();
        System.out.print("Set password: "); String pwd = sc.nextLine();
        int id = doctors.size() + 1;
        doctors.put(id, new Doctor(id, name, spec, time, limit, pwd));
        System.out.println("Doctor added with ID: " + id);
    }

    static void registerPatient() {
        System.out.print("Enter name: "); String name = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        if (registeredEmails.contains(email)) {
            System.out.println("Email already registered."); return;
        }
        System.out.print("Set password: "); String pwd = sc.nextLine();
        int id = patients.size() + 1;
        patients.put(id, new Patient(id, name, email, pwd));
        registeredEmails.add(email);
        System.out.println("Registered with Patient ID: " + id);
    }

    static void patientLogin() {
        System.out.print("Enter Patient ID: "); int id = sc.nextInt(); sc.nextLine();
        System.out.print("Password: "); String pwd = sc.nextLine();
        Patient p = patients.get(id);
        if (p == null || !p.password.equals(pwd)) {
            System.out.println("Invalid credentials"); return;
        }
        while (true) {
            System.out.println("\nPatient Menu:\n1. Book Appointment\n2. View History\n3. Give Feedback\n4. Logout");
            int ch = sc.nextInt(); sc.nextLine();
            switch (ch) {
                case 1 -> bookAppointment(p);
                case 2 -> p.viewHistory();
                case 3 -> giveFeedback(p);
                case 4 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    static void bookAppointment(Patient p) {
        System.out.print("Enter your issue (e.g. fever, toothache): ");
        String issue = sc.nextLine().toLowerCase();
        String specialization = issueToSpecialization.getOrDefault(issue, "General Physician");
        System.out.println("Suggested specialization: " + specialization);
        List<Doctor> available = new ArrayList<>();
        for (Doctor d : doctors.values()) {
            if (d.specialization.equalsIgnoreCase(specialization) && d.patientLimit > 0)
                available.add(d);
        }
        if (available.isEmpty()) {
            System.out.println("No doctors available for this issue."); return;
        }
        System.out.println("Available Doctors:");
        for (Doctor d : available) System.out.println(d);
        System.out.print("Enter Doctor ID: "); int did = sc.nextInt(); sc.nextLine();
        Doctor doc = doctors.get(did);
        if (doc == null || !doc.specialization.equalsIgnoreCase(specialization)) {
            System.out.println("Invalid doctor"); return;
        }
        doc.patientLimit--;
        Appointment a = new Appointment(p.id, did);
        appointments.add(a);
        p.history.add(a);
        doc.patientRecords.put(p.id, a);
        System.out.println("Appointment booked.");
    }

    static void giveFeedback(Patient p) {
        for (Appointment a : p.history) {
            if (!a.completed || a.feedbackGiven) continue;
            System.out.println("Feedback for Doctor ID " + a.doctorId);
            System.out.print("Rating (1-5): "); int rating = sc.nextInt(); sc.nextLine();
            Doctor doc = doctors.get(a.doctorId);
            doc.feedback.add(rating);
            a.feedbackGiven = true;
            System.out.println("Feedback submitted.");
            return;
        }
        System.out.println("No feedback due.");
    }

    static void doctorLogin() {
        System.out.print("Doctor ID: "); int id = sc.nextInt(); sc.nextLine();
        System.out.print("Password: "); String pwd = sc.nextLine();
        Doctor d = doctors.get(id);
        if (d == null || !d.password.equals(pwd)) {
            System.out.println("Invalid credentials"); return;
        }
        while (true) {
            System.out.println("\nDoctor Menu:\n1. View Patients\n2. Mark Appointment Complete\n3. Add Notes\n4. Logout");
            int ch = sc.nextInt(); sc.nextLine();
            switch (ch) {
                case 1 -> d.viewAppointments();
                case 2 -> d.markComplete();
                case 3 -> d.addNotes();
                case 4 -> { return; }
                default -> System.out.println("Invalid");
            }
        }
    }

    static void saveToCSV() {
        try (PrintWriter pwPatients = new PrintWriter("patients.csv");
             PrintWriter pwDoctors = new PrintWriter("doctors.csv");
             PrintWriter pwAppointments = new PrintWriter("appointments.csv")) {

            for (Patient p : patients.values())
                pwPatients.println(p.id + "," + p.name + "," + p.email + "," + p.password);

            for (Doctor d : doctors.values())
                pwDoctors.println(d.id + "," + d.name + "," + d.specialization + "," + d.availableTime + "," + d.patientLimit + "," + d.password);

            for (Appointment a : appointments)
                pwAppointments.println(a.patientId + "," + a.doctorId + "," + a.completed + "," + a.feedbackGiven + "," + a.notes.replaceAll(",", " "));

        } catch (IOException e) {
            System.out.println("Error saving files: " + e.getMessage());
        }
    }

    static void loadFromCSV() {
        try (BufferedReader brPatients = new BufferedReader(new FileReader("patients.csv"))) {
            String line;
            while ((line = brPatients.readLine()) != null) {
                String[] parts = line.split(",");
                Patient p = new Patient(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]);
                patients.put(p.id, p);
                registeredEmails.add(p.email);
            }
        } catch (IOException ignored) {}

        try (BufferedReader brDoctors = new BufferedReader(new FileReader("doctors.csv"))) {
            String line;
            while ((line = brDoctors.readLine()) != null) {
                String[] parts = line.split(",");
                Doctor d = new Doctor(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5]);
                doctors.put(d.id, d);
            }
        } catch (IOException ignored) {}

        try (BufferedReader brAppointments = new BufferedReader(new FileReader("appointments.csv"))) {
            String line;
            while ((line = brAppointments.readLine()) != null) {
                String[] parts = line.split(",", 5);
                Appointment a = new Appointment(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                a.completed = Boolean.parseBoolean(parts[2]);
                a.feedbackGiven = Boolean.parseBoolean(parts[3]);
                a.notes = parts.length > 4 ? parts[4] : "";
                appointments.add(a);
                if (patients.containsKey(a.patientId)) patients.get(a.patientId).history.add(a);
                if (doctors.containsKey(a.doctorId)) doctors.get(a.doctorId).patientRecords.put(a.patientId, a);
            }
        } catch (IOException ignored) {}
    }

    static class Doctor {
        int id;
        String name, specialization, availableTime, password;
        int patientLimit;
        List<Integer> feedback = new ArrayList<>();
        Map<Integer, Appointment> patientRecords = new HashMap<>();

        Doctor(int id, String name, String spec, String time, int limit, String pwd) {
            this.id = id; this.name = name; this.specialization = spec; this.availableTime = time;
            this.patientLimit = limit; this.password = pwd;
        }

        public String toString() {
            return id + ", " + name + ", " + specialization + ", " + availableTime + ", Remaining: " + patientLimit;
        }

        void viewAppointments() {
            patientRecords.values().forEach(System.out::println);
        }

        void markComplete() {
            System.out.print("Enter Patient ID to mark complete: ");
            int pid = sc.nextInt(); sc.nextLine();
            Appointment a = patientRecords.get(pid);
            if (a != null) {
                a.completed = true;
                System.out.println("Marked complete");
            } else System.out.println("Invalid patient");
        }

        void addNotes() {
            System.out.print("Enter Patient ID to add notes: "); int pid = sc.nextInt(); sc.nextLine();
            Appointment a = patientRecords.get(pid);
            if (a != null && a.completed) {
                System.out.print("Enter prescription/notes: ");
                a.notes = sc.nextLine();
                System.out.println("Notes added");
            } else System.out.println("Complete appointment first");
        }
    }

    static class Patient {
        int id;
        String name, email, password;
        List<Appointment> history = new ArrayList<>();

        Patient(int id, String name, String email, String pwd) {
            this.id = id; this.name = name; this.email = email; this.password = pwd;
        }

        public String toString() {
            return id + ", " + name + ", " + email;
        }

        void viewHistory() {
            for (Appointment a : history) {
                Doctor doc = doctors.get(a.doctorId);
                System.out.println("Visited Dr. " + doc.name + " | Complete: " + a.completed);
                if (a.notes != null && !a.notes.isEmpty())
                    System.out.println("Notes: " + a.notes);
            }
        }
    }

    static class Appointment {
        int patientId, doctorId;
        boolean completed = false, feedbackGiven = false;
        String notes = "";

        Appointment(int pid, int did) {
            this.patientId = pid; this.doctorId = did;
        }

        public String toString() {
            return "Patient ID: " + patientId + ", Completed: " + completed + ", Notes: " + notes;
        }
    }
}
