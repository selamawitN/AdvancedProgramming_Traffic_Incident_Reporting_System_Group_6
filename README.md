# Traffic Incident Management System

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)

---

## Overview

The **Traffic Incident Management System** addresses a critical gap between citizens who witness traffic incidents and administrators responsible for emergency response. In many situations, people are unsure where to report incidents, or the information fails to reach the right personnel in time.

This system allows any citizen to submit an incident report in under 60 seconds through a browser-based map interface. When a critical incident is reported, all connected administrators receive an instant alert on their desktop applications — with no delay and no missed emergencies.

The system consists of two components:

| Component | Users | Purpose |
|---|---|---|
| Web Dashboard | Citizens | Report incidents via an interactive map |
| Desktop Application | Administrators | Manage incidents, receive alerts, view analytics |

---

## Features

### Web Dashboard (Citizens)

- **Interactive Map** — Click anywhere on the map to pin the exact incident location
- **Use My Location** — One-click geolocation using the browser GPS API
- **Location Search** — Type a place name and the map zooms to that location automatically
- **Incident Form** — Report incident type, severity level, description, and reporter name
- **First Aid Guide** — Built-in instructions for CPR, bleeding control, and burns
- **Emergency Contacts** — Direct display of ambulance, fire brigade, and police numbers

### Desktop Application (Administrators)

- **Secure Login** — Role-based access; only accounts with the `admin` role can log in
- **Incident Table** — View all incidents with ID, type, location, severity, status, and reporter
- **Status Management** — Update incident status to *In Progress* or *Resolved*
- **Real-Time UDP Alerts** — Instant popup notification when a critical incident is submitted
- **Watchdog Thread** — Automatically re-alerts administrators every 60 seconds for unresolved critical incidents
- **Analytics Dashboard** — Bar chart grouped by incident type; pie chart showing severity distribution
- **Hotspot Detection** — Ranked list of the most incident-prone locations
- **Log Viewer** — View the last 50 system log entries without leaving the application

---

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                        CITIZENS                         │
│              Browser → http://localhost:8081            │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP (Port 8081)
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    JAVA SERVER                          │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  WebServer  │  │  TCPServer   │  │   UDPServer   │  │
│  │ (Port 8081) │  │ (Port 5000)  │  │  (Port 6000)  │  │
│  └──────┬──────┘  └──────┬───────┘  └───────┬───────┘  │
│         │                │                  │           │
│         └────────────────┼──────────────────┘           │
│                          ▼                              │
│                    ┌───────────┐                        │
│                    │  MySQL DB │                        │
│                    │  (XAMPP)  │                        │
│                    └───────────┘                        │
└─────────────────────────────────────────────────────────┘
                       │ TCP (Port 5000)
                       ▼ UDP Broadcast (Port 6000)
┌─────────────────────────────────────────────────────────┐
│               ADMIN DESKTOP APPLICATION                 │
│         Java Swing + TCPClient + UDPListener            │
└─────────────────────────────────────────────────────────┘
```

### Communication Protocols

| Protocol | Port | Purpose |
|---|---|---|
| HTTP | `8081` | Serves the web dashboard and handles incident form submissions |
| TCP | `5000` | Persistent connection between the admin desktop client and the server |
| UDP Broadcast | `6000` | Real-time emergency alerts pushed to all connected administrators |

> The web dashboard is served entirely from within the Java application via the `WebServer` class. No Apache, Tomcat, or external web server is required.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (JDK 8+) |
| Desktop UI | Java Swing |
| Web Server | Built-in Java HTTP Server |
| Web Frontend | HTML, CSS, JavaScript |
| Maps | [Leaflet.js](https://leafletjs.com/) |
| Database | MySQL via XAMPP |
| Charts | Java2D (no external charting libraries) |
| Networking | Java TCP Sockets + UDP Broadcast |

---

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- [XAMPP](https://www.apachefriends.org/) with MySQL running
- Any modern web browser

### 1. Set Up the Database

1. Start XAMPP and ensure the MySQL service is running.
2. Open **phpMyAdmin** at `http://localhost/phpmyadmin`.
3. Create a new database (e.g., `traffic_db`).
4. Run the following SQL to create the required tables:

```sql
CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    email    VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL
);

CREATE TABLE incidents (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(50),
    location    VARCHAR(100),
    severity    VARCHAR(20),
    description TEXT,
    status      VARCHAR(30),
    reported_by VARCHAR(100),
    timestamp   DATETIME,
    latitude    DOUBLE,
    longitude   DOUBLE
);
```

5. Insert a default administrator account:

```sql
INSERT INTO users (email, password, role)
VALUES ('admin@traffic.com', 'admin123', 'admin');
```

### 2. Compile and Run the Server

```bash
javac *.java
java Main
```

The application will start all three services simultaneously:

- Web Dashboard — `http://localhost:8081`
- TCP Server — port `5000`
- UDP Broadcast — port `6000`

### 3. Launch the Admin Desktop Application

Run the desktop JAR or main class. Log in using the admin credentials created in step 1.

### 4. Access the Web Dashboard

Open a browser and navigate to:

```
http://localhost:8081
```

---

## Testing

The following steps verify that all system components are functioning correctly.

**Step 1 — Submit a Critical Incident**
Open `http://localhost:8081`, click the map to select a location, complete the form with **Severity: Critical**, and submit.

**Step 2 — Verify the UDP Alert**
The admin desktop application should display an immediate popup alert for the critical incident.

**Step 3 — Check the Incident Table**
The new incident should appear in the admin table with a status of `Open`.

**Step 4 — Update Incident Status**
Select the incident row and click one of the three action buttons: *In Progress*, *Resolved*, or *Delete*.

**Step 5 — View Analytics**
Click the **Analytics** button. A window should open displaying a bar chart by incident type and a pie chart by severity level.

**Step 6 — Review the System Log**
Click **View Logs** and verify the log contains entries for: admin login, incident submission, UDP broadcast, and the status change.

**Step 7 — Confirm Low-Severity Behavior**
Submit another incident with **Severity: Low**. Confirm that no UDP popup appears — broadcast alerts are triggered exclusively for Critical incidents.

---

## Database Schema

### `users`

| Column | Type | Description |
|---|---|---|
| `id` | INT | Primary key |
| `email` | VARCHAR | Administrator email address |
| `password` | VARCHAR | Administrator password |
| `role` | VARCHAR | Must be `admin` to access the desktop application |

### `incidents`

| Column | Type | Description |
|---|---|---|
| `id` | INT | Primary key |
| `type` | VARCHAR | Incident type (e.g., Accident, Flood) |
| `location` | VARCHAR | Human-readable location name |
| `severity` | VARCHAR | `Low`, `Medium`, `High`, or `Critical` |
| `description` | TEXT | Full incident description provided by the citizen |
| `status` | VARCHAR | `Open`, `In Progress`, or `Resolved` |
| `reported_by` | VARCHAR | Name of the reporting citizen |
| `timestamp` | DATETIME | Date and time the incident was submitted |
| `latitude` | DOUBLE | Map latitude coordinate |
| `longitude` | DOUBLE | Map longitude coordinate |

---

## Project Structure

```
traffic-incident-system/
│
├── src/
│   ├── Main.java                # Entry point — initializes and starts all servers
│   ├── TCPServer.java           # Manages admin desktop client connections (Port 5000)
│   ├── WebServer.java           # Serves the web dashboard and handles POST submissions (Port 8081)
│   ├── UDPServer.java           # Broadcasts emergency alerts to all clients (Port 6000)
│   ├── UDPListener.java         # Admin-side background thread for receiving UDP alerts
│   ├── DatabaseManager.java     # Centralizes all MySQL database operations
│   ├── WatchdogThread.java      # Periodically checks for unresolved critical incidents
│   ├── Logger.java              # Thread-safe file logger with file locking
│   │
│   └── ui/
│       ├── LoginFrame.java      # Administrator login screen
│       ├── DashboardFrame.java  # Main incident management table
│       └── AnalyticsFrame.java  # Charts and hotspot analysis window
│
├── logs/
│   └── system.log               # Auto-generated at runtime
│
└── README.md
```

---

## Authors

Developed as a university project to address the real-world problem of slow emergency response times in traffic incident management.
