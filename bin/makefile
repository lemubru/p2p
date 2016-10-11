JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        ChatClient.java \
        ChatRoomServer.java \
        ServerManager.java \
        ChatMessage.java \
        ClientThread.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class