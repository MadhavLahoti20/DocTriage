# 🏥 DocTriage – Java-Based Hospital Management System

**DocTriage** is a console-based hospital management system built in Java using core Object-Oriented Programming concepts. It simulates real-world doctor-patient interactions, appointment scheduling, and CSV-based persistent storage.

---

## 🚀 Features

### 👤 Role-Based Login
- **Admin**: Add/View doctors, monitor patient registrations
- **Doctor**: View patients, mark appointments complete, add prescriptions
- **Patient**: Register, book appointments, view history, give feedback

### 📋 Appointments & Specialization
- Maps patient issues (e.g., fever, fracture) to correct **doctor specialization**
- Prevents overbooking using **patient limit tracking**
- Doctors have fixed **availability slots**

### 💾 Persistent CSV Storage
- Automatically saves and loads data from:
  - `patients.csv`
  - `doctors.csv`
  - `appointments.csv`

### 📝 Feedback & Medical History
- Patients can rate doctors (1–5)
- Doctors can add consultation notes/prescriptions
- History is saved and shown per patient


## 🗂 File Structure

├── DocTriage.java          // Main Java file (all logic + classes inside)
├── patients.csv            // Sample patient data
├── doctors.csv             // Sample doctor data
├── appointments.csv        // Sample appointment data
├── README.md               // Project overview and usage
