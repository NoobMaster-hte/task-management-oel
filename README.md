# Java-Based Task Management System

Open-Ended Lab (OEL) - Swing GUI task manager built with the JDK, Git and Javadoc.

## Structure
- `src/oel/taskmanager/` - application source (`Task`, `TaskStatus`, `TaskManager`, `TaskNotFoundException`, `TaskTableModel`, `TaskManagerApp`)
- `test/oel/taskmanager/` - console test runner (`TaskManagerTest`)
- `docs/` - generated Javadoc (open `docs/index.html`)

## Build and run
```
mkdir -p out
javac -d out src/oel/taskmanager/*.java
java -cp out oel.taskmanager.TaskManagerApp
```

## Run tests
```
javac -d out -cp out test/oel/taskmanager/TaskManagerTest.java
java -cp out oel.taskmanager.TaskManagerTest
```

## Generate Javadoc
```
javadoc -d docs -sourcepath src -subpackages oel -author -private -windowtitle "Task Management System"
```
