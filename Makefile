.PHONY: build
build:
	@mkdir -p target/classes
	@javac -cp ".:src/main/java" -d target/classes $(shell find src/main/java -name "*.java")

.PHONY: run
run: build
	@java -cp ".:target/classes" app.soundlab.ui.MainWindow