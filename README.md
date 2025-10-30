How to build and run

From the `AudioEditor` directory:

Build:

```bash
mkdir -p target/classes
javac -cp ".:src/main/java" -d target/classes $(find src/main/java -name "*.java")
```

Run:

```bash
java -cp target/classes app.soundlab.ui.MainWindow
```

Or using Makefile (recommended):

```bash
cd AudioEditor
make run
```

Requirements:
- JDK 17+ installed and on PATH (`java -version`, `javac -version`).

