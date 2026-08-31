# Yachiyo

Yachiyo is a command-line task manager for tracking to-dos, deadlines, and events. It stores tasks
locally so they remain available between sessions.

See the [user guide](docs/README.md) for the supported commands.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional)

## Setting up in IntelliJ IDEA

1. Open the project directory in IntelliJ IDEA.
2. Configure the project to use **JDK 25** and set the **Project language level** to `SDK default`.
3. Open `src/main/java/yachiyo/Yachiyo.java`.
4. Run `Yachiyo.main()`.

If the setup is correct, the application displays the Yachiyo banner followed by:

```text
Hello! Yachiyo here!
What shall we accomplish today?
```

Keep `src/main/java` as the source root because Gradle and IntelliJ IDEA expect Java source files in
that directory.

## Running with Gradle

On macOS or Linux:

```shell
./gradlew run
```

On Windows:

```bat
gradlew.bat run
```

Yachiyo saves task data to `data/yachiyo.txt`.

## Building the application

Create an executable JAR:

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
