
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class ServerManager extends Thread {
    private Vector<ClientThread> connections;
    private String serverMessage;
    private int users = 0;
    public ServerManager(Vector<ClientThread> connections)    {
        this.connections = connections;
        users =  connections.size();
        serverMessage = null;
    }

    public void setServerMessage(String msg) {
        serverMessage = msg;
    }

    public void updateUserList() {
        String[] userList = new String[connections.size()];
        int k = 0;
        for (ClientThread jth : connections) {
            String screenName = jth.getScreenName();
            userList[k] = screenName;
            k++;
        }
        for (ClientThread kth : connections) {

            try {
                kth.println(new ChatMessage(ChatMessage.USERSONLINE,userList));
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void run() {
        while (true) {
            for (int i = 0; i < connections.size(); i++) {
                ClientThread ith = connections.get(i);
                // if client disconnected remove from vector of connections and notify other connection
                if (!ith.isAlive()) {
                    for (ClientThread jth : connections) {
                        try {
                            if(!ith.getScreenName().equals(jth.getScreenName())) {
                                jth.println(new ChatMessage(ChatMessage.MESSAGE,ith.getScreenName() + " disconnected"));
                            }
                        } catch (ClassNotFoundException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    connections.remove(i);
                }
                // broadcast to everyone
                if(this.users != connections.size()) {
                    if(this.users < connections.size()) {
                        //someone connect , inform all threads about it
                        for (ClientThread jth : connections) {
                            try {
                                if(!jth.getScreenName().equals(connections.get(connections.size()-1).getScreenName())) {

                                    jth.println(new ChatMessage(ChatMessage.MESSAGE,connections.get(connections.size()-1).getScreenName() + " connected"));
                                } else {
                                    jth.println(new ChatMessage(ChatMessage.MESSAGE,"You connected to the server"));
                                }
                            } catch (ClassNotFoundException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    updateUserList();
                    this.users = connections.size();
                }

                if(serverMessage != null) {
                    serverMessage = null;
                }
                ChatMessage message = ith.getChatMessage();
                if (message != null) {

                    if(message.getMessage().startsWith("@")) {
                        if(message.getMessage().indexOf(" ") == -1) {
                            try {
                                ith.println(new ChatMessage(ChatMessage.MESSAGE, "invalid message: put space after username and then type message"));
                            } catch (ClassNotFoundException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;
                        }
                        boolean userfound = false;
                        System.out.println("Private msg!!");
                        for (ClientThread jth : connections) {
                            if(jth.getScreenName().equals(message.getMessage().substring(1, message.getMessage().indexOf(" ")))) {
                                userfound = true;
                                try {
                                    message.setMessage(ith.getScreenName() + ":" + message.getMessage() +">"+ith.getMyport());
                                    System.out.println("Message for "+ jth.getScreenName());
                                    jth.println(message);
                                    //ith.println(message);
                                } catch (ClassNotFoundException | IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                        }
                        if(!userfound) {
                            try {
                                ith.println(new ChatMessage(ChatMessage.MESSAGE, "username not found"));
                            } catch (ClassNotFoundException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }


                    } else {
                        message.setMessage(ith.getScreenName() + ":" + message.getMessage());
                        for (ClientThread jth : connections) {
                            try {
                                jth.println(message);
                            } catch (ClassNotFoundException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(100);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
