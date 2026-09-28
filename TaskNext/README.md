# TaskNext

A personal to-do and task manager for students. Organise assignments, club work and personal errands into
colour-coded lists, each task with a due date, a priority and completion tracking.

**Stack:** Java 17, Spring Boot 3.3, Spring Data JPA, MySQL, Maven, Thymeleaf (HTML) and CSS.

## Features
- Multiple lists (Assignments, Club Work, Personal Errands are created on first run; add your own).
- Tasks with title, notes, due date and priority (High, Medium, Low).
- One-click completion with a progress bar per list and overall.
- Views: All tasks, Due today, Upcoming, Overdue, Completed.
- Search, priority filter and status filter.
- Add, edit and delete tasks and lists.

## Run it in IntelliJ IDEA
1. **Install MySQL** (8.x) and make sure the server is running on port 3306.
2. **Open the project:** unzip, then in IntelliJ choose *File > Open* and select the `TaskNext` folder
   (the one containing `pom.xml`). Let IntelliJ import the Maven project.
3. **Set the JDK to 17 or newer:** *File > Project Structure > Project SDK*.
4. **Set your MySQL login** in `src/main/resources/application.properties`:
   ```
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
   The `tasknext` database and all tables are created automatically on first start.
5. **Run** `TaskNextApplication` (right-click > Run).
6. Open **http://localhost:8080**

## Project structure
```
src/main/java/com/tasknext
  TaskNextApplication.java
  model/        Task, TaskList, Priority (JPA entities)
  repository/   TaskRepository, TaskListRepository
  service/      TaskService, TaskListService
  controller/   TaskController, TaskListController
  dto/          TaskForm, ListForm, ListSummary, TaskStats
  config/       DataSeeder (sample data), GlobalModelAdvice (sidebar data)
src/main/resources
  templates/    tasks, task-form, list-form, fragments, error (HTML)
  static/css/   style.css
  static/js/    app.js
  application.properties
database/create_database.sql   (optional)
```

## Troubleshooting
- *Access denied for user*: fix the username/password in `application.properties`.
- *Communications link failure*: MySQL isn't running or isn't on port 3306.
- To start fresh, drop the database: `DROP DATABASE tasknext;` and run the app again.
