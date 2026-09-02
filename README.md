# Yachiyo

Yachiyo is a JavaFX desktop task manager for tracking to-dos, deadlines, and events through a
chat-style interface. Tasks are stored locally and restored the next time the application starts.

## Features

- Add to-dos, deadlines, and events.
- Mark tasks as complete or incomplete.
- Find tasks by description or date.
- Delete tasks and list the current lineup.
- Save every change automatically to `data/yachiyo.txt`.

See the [user guide](docs/README.md) for command formats and examples.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional)

The repository includes the Gradle wrapper, so a separate Gradle installation is not required.

## Running with Gradle

On macOS or Linux:

```shell
./gradlew run
```

On Windows:

```bat
gradlew.bat run
```

## Setting up in IntelliJ IDEA

1. Open the project directory and import it as a Gradle project.
2. Configure the project and Gradle JVMs to use **JDK 25**. Keep the project language level set to
   `SDK default`.
3. Open `src/main/java/yachiyo/Launcher.java`.
4. Run `Launcher.main()` to start the JavaFX interface.

Keep `src/main/java` as the source root because Gradle and IntelliJ IDEA expect Java source files in
that directory.

For the terminal interface, run `Yachiyo.main()` from
`src/main/java/yachiyo/Yachiyo.java` instead.

## Building the application

Create an executable JAR containing the application and its dependencies:

```shell
./gradlew shadowJar
```

Run the generated JAR:

```shell
java -jar build/libs/yachiyo.jar
```

## Running tests and code checks

Run the test suite and Checkstyle checks together:

```shell
./gradlew check
```

Run only Checkstyle:

```shell
./gradlew checkstyleMain checkstyleTest
```

## Project structure

```text
src/main/java/yachiyo/       Application source code
src/main/resources/          JavaFX views, styles, and images
src/test/java/yachiyo/       Automated tests
docs/README.md               User guide
data/yachiyo.txt             Local task data created at runtime
```
