all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part1.jar Main -C bin .
	javadoc -private src/Main.java -d doc/javadoc

testing:
	java -jar dist/part1.jar test/Factorial.fs



clean:
	rm bin/ulb/*.class
	rm dist/part1.jar
	rm src/main/java/ulb/LexicalAnalyzer.java
	rm src/main/java/ulb/*.java~

