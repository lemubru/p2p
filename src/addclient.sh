javac *.java
astyle *.java
mv *.orig ./temp
java ChatClient
