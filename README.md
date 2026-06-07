# Exam and Project Management System — Java

A desktop application for managing academic exams and projects, built with Java Swing and connected to a Microsoft SQL Server database. The system supports two user roles: students and professors, each with a dedicated dashboard and set of features.

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java |
| UI | Java Swing (GUI) |
| Database | Microsoft SQL Server |
| Connectivity | JDBC |
| IDE | IntelliJ IDEA |

---

## Features

### Authentication
- Registration and login for both students and professors
- Role-based routing to dedicated dashboards

### Professor Dashboard
- Manage subjects (add, edit, delete)
- Manage classrooms (add, edit, delete)
- Create and manage exams (date, time, room, session, type)
- Create and manage projects (title, description, deadline, requirements)
- View enrolled students per exam/project
- Assign grades

### Student Dashboard
- Browse and enroll in available exams
- View exam details (date, time, room, session)
- Browse and enroll in projects
- Update project status (Not started / In progress / Finished)
- View grades for exams and projects

---

## Database Schema

7 main tables with relational integrity:

| Table | Description |
|---|---|
| Student | Student accounts and study year |
| Profesor | Professor accounts and department |
| Materie | Subjects linked to professors |
| Sala | Classrooms with capacity |
| Examen | Exams with date, time, room, session type |
| Proiect | Projects with deadlines and requirements |
| StudentExamen | Enrollment + grade + status per exam |
| StudentProiect | Enrollment + grade + status per project |

### Key relationships
- Professor → Subject (1:N)
- Subject → Exam (1:N)
- Subject → Project (1:N)
- Room → Exam (1:N)
- Student ↔ Exam (N:N via StudentExamen)
- Student ↔ Project (N:N via StudentProiect)

---

## SQL Highlights

The application uses both simple and complex queries:

**Simple queries:** multi-table JOINs for exam/project listings, filtering by study year, UNION for combined grade views, deadline proximity filtering with `DATEADD`

**Complex queries:** subqueries with `HAVING` for above-average analysis, `NOT EXISTS` for completeness checks, correlated subqueries for room efficiency and student performance

---

## Setup

### Prerequisites
- Java 17+
- Microsoft SQL Server (local instance)
- IntelliJ IDEA

### Database
1. Open SQL Server Management Studio
2. Run `Backup_BD.sql` to create the schema and seed data

### Connection
Update `DbConnection.java` with your local SQL Server instance:
```java
private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
        "databaseName=ExameneProiecte;" +
        "integratedSecurity=true;" +
        "trustServerCertificate=true;";
```

### Run
Open the project in IntelliJ IDEA and run `Main.java`.

---
