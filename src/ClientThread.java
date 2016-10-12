
import java.io.*;
import java.net.Socket;

public class ClientThread  extends Thread {
    private Socket socket;
    private ObjectInputStream sInput;       // to read from the socket
    private ObjectOutputStream sOutput;     // to
    private ChatMessage chatMsg;
    private String screenName;
    private int myport;
    private String myIP;

    public ClientThread(Socket insocket, String screenName,ObjectInputStream sInput, ObjectOutputStream sOutput, String ip, int port) throws ClassNotFoundException, IOException {
        this.screenName = screenName;
        this.sOutput = sOutput;
        this.sInput  = sInput;
        this.socket = insocket;
        this.myIP = ip;
        this.myport = port;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getMyIP() {
        return myIP;
    }
    public int getMyport() {
        return myport;
    }
    /**
     * This method send a ChatMessage Object to the server
     * @param s input ChatMessage
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void println(ChatMessage s) throws ClassNotFoundException, IOException {
        sOutput.writeObject(s);
    }
    /**
     * This method checks for incoming messages from the server.
     */
    public void run() {
        while(true) {
            try {
                chatMsg = (ChatMessage) sInput.readObject();
            }
            catch(IOException e) {
                System.out.println("Server has close the connection:");

                break;
            }
            catch(ClassNotFoundException e2) {
            }
        }

        try {
            if(sInput != null) sInput.close();

        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try {
            if(socket != null) socket.close();
        }
        catch(Exception e) {}
        System.err.println("Closed client socket");
    }

    /**
     * These methods are synchronized as to alleviate any race conditions that
     * may occur when getting and setting a message for a client
     *
     * @return ChatMessage Object
     */
    public synchronized ChatMessage getChatMessage() {
        if (chatMsg == null) return null;
        ChatMessage temp = chatMsg;

        chatMsg = null;
        notifyAll();
        return temp;
    }

    public synchronized void setChatMessage(ChatMessage s) {
        if (chatMsg != null) {
            try                  {
                wait();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        chatMsg = s;
    }

}
