JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        PeerClient.java \
        ChatRoomServer.java \
        ServerManager.java \
		FileObj.java \
		Receiver.java \
		Sender.java \
        ChatMessage.java \
        ClientThread.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class