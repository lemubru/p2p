/******************************************************************************
 *  Compilation:  javac ChatRoomServer.java
 *  Execution:    java ChatRoomServer
 *  Dependencies: In.java Out.java Connection.java ConnectionListener.java
 *
 *  Creates a server to listen for incoming connection requests on
 *  port 4444.
 *
 *  % java ChatRoomServer
 *
 *  Remark
 *  -------
 *    - Use Vector instead of ArrayList since it's synchronized.
 *
 ******************************************************************************/
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.*;
import javax.swing.*;

import java.net.Socket;

import java.net.ServerSocket;
import java.util.Vector;

public class ChatRoomServer extends JFrame implements ActionListener  {

    private static final long serialVersionUID = 1L;
    private JButton startBtn;
    private JButton stopBtn
    ;
    private ServerSocket serverSocket;
    private Vector<ClientThread> connections;
    private ServerManager connectionListener;
    private boolean endServer;
    public ChatRoomServer(int port) {
        endServer = false;
        connections        = new Vector<ClientThread>();
        try {
            serverSocket             = new ServerSocket(port);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        connectionListener = new ServerManager(connections);

        connectionListener.start();

        addWindowListener(
        new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeServer();
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                System.exit(1);
//                    try                   { socket.close();        }
//                    catch (Exception ioe) { ioe.printStackTrace(); }
            }
        }
        );


        Container content = getContentPane();
        content.setSize(800,400);
        startBtn = new JButton("Start");
        startBtn.setPreferredSize(new Dimension(400, 400));
        stopBtn = new JButton();
        startBtn.addActionListener(this);
        content.add(startBtn);
        content.add(new JLabel("Server running"));
        pack();
        startBtn.requestFocusInWindow();
        setVisible(true);

    }

    public void runServer() throws IOException, ClassNotFoundException {
        System.err.println("ChatRoomServer started");
        while (true) {
            // wait for next client connection request
            Socket clientSocket = serverSocket.accept();
            System.err.println("Created socket with client");
            ObjectInputStream sInput  = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream sOutput = new ObjectOutputStream(clientSocket.getOutputStream());

            String  s;
            String clientIP;
            String clientport;
            String clientuser;
            try {
                s = (String) sInput.readObject();
                clientIP = s.substring(s.indexOf("+"), s.indexOf(":"));
                clientport = s.substring(s.indexOf(":"), s.length());
                clientuser = s.substring(0, s.indexOf("+"));

                System.out.println(clientuser + " has connected IP , port:" +clientIP + ":"  + clientport);
            }
            catch(IOException e) {
                break;
            }
            boolean usertaken = false;
            for (ClientThread jth : connections) {

                if(jth.getScreenName().equals(s)) {
                    sOutput.writeObject(new ChatMessage(ChatMessage.ERROR, "username taken, sorry"));
                    clientSocket.close();
                    usertaken = true;
                    break;
                }
            }

            if(!usertaken) {
                sOutput.writeObject(new ChatMessage(ChatMessage.SUCCESS, "Success!"));
                ClientThread connection = new ClientThread(clientSocket,clientuser,sInput, sOutput);
                connections.add(connection);
                connection.start();
            }
        }
    }
    public void closeServer() {
        endServer = true;
    }


    public static void main(String[] args) {
        ChatRoomServer cs = new ChatRoomServer(Integer.parseInt(args[0]));
        try {
            cs.runServer();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }


}
