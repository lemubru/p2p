make
astyle *.java
mv *.orig ./temp
java ChatRoomServer 4444
