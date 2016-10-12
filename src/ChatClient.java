/******************************************************************************
 *  Compilation:  javac ChatClient.java
 *  Execution:    java ChatClient name host
 *  Dependencies: In.java Out.java
 *
 *  Connects to host server on port 4444, enables an interactive
 *  chat client.
 *
 *  % java ChatClient alice localhost
 *
 *  % java ChatClient bob localhost
 *
 ******************************************************************************/
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.Random;
import java.math.*;
//Hello //sdfsdf
public class ChatClient extends JFrame implements ActionListener,ListSelectionListener  {

    private static final long serialVersionUID = 1L;
    private JList<String> onlineUserList;
    private DefaultListModel<String> OnlineUserListModel;
    private ListeningPortPrivate lpp;
    static final int MAXIMUM = 100;
    static final int MINIMUM = 0;
    private JButton searchBtn;
    private JTextField searchField;
    private JLabel downloadProgLabel;
    private JLabel uploadProgLabel;
    private File file;
    private Sender sender;
    private JList<String> searchResultlist;
    private DefaultListModel<String> listModelSearches;

    private JList<String> myFilesList;
    private DefaultListModel<String> myFilesListModel;

    private JButton pauseBtn;
    private JButton resumeBtn;
    private JButton downloadBtn;
    private JProgressBar downloadbar;
    private Receiver rxThread;
    private JProgressBar uploadbar;

    private int progress = 0 ;
    private String screenName;
    private JButton whisperBtn;


    private JTextArea  textArea = new JTextArea(20, 42);

    private JTextField typedText   = new JTextField(32);

    private Socket socket;
    private Container content;

    private String serverIP;
    private String port;
    private String myIP;
    private int myport;
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private boolean InChatRoom = false;

    private boolean successConnect = false;
    @SuppressWarnings("unchecked")
    public ChatClient() {
        this.serverIP = "lol";
        this.screenName = "";
        JTextField hostNameField = new JTextField(5);
        JTextField portField = new JTextField(5);
        JTextField userNameField = new JTextField(5);
        JTextField myIPField = new JTextField(5);
        hostNameField.setText("localhost");
        myIPField.setText("localhost");
        portField.setText("4444");
        String[] testname = new String[20];
        testname[0] = "mike";
        testname[1] = "fred";
        testname[2] = "dan";
        testname[3] = "ben";
        //sdfsd
        testname[4] = "alex";
        testname[5] = "shaun";
        testname[6] = "frank";
        testname[7] = "bob";
        testname[8] = "alice";
        testname[9] = "sara";
        testname[10] = "matt";
        testname[11] = "sam";
        testname[12] = "ozzy";
        testname[13] = "bill";
        testname[14] = "deb";
        testname[15] = "andy";
        testname[16] = "molly";
        testname[17] = "john";
        testname[18] = "sean";
        testname[19] = "ian";
        Random ran = new Random();
        userNameField.setText(testname[ran.nextInt(testname.length)]);
        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("myIP:"));
        myPanel.add(myIPField);
        myPanel.add(new JLabel("hostname:"));
        myPanel.add(hostNameField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("port"));
        myPanel.add(portField);
        myPanel.add(new JLabel("username:"));
        myPanel.add(userNameField);
        myport = (int)(Math.random()*9000)+1000;
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                     "Please enter host address and username", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            this.screenName =  userNameField.getText();
            this.serverIP = hostNameField.getText();
            this.port = portField.getText();
            this.myIP = myIPField.getText();
        } else {
            System.exit(1);
        }
        boolean success = false;
        while(!success) {
            try {
                socket = new Socket(this.serverIP, Integer.parseInt(this.port));
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sOutput.writeObject(this.screenName +"+"+ myIP +":"+myport);
                sInput  = new ObjectInputStream(socket.getInputStream());
                ChatMessage s_msg= (ChatMessage) sInput.readObject();
                if(s_msg.getType() == ChatMessage.SUCCESS) {
                    success = true;
                    displayGUI();
                } else {
                    System.out.println("FAIL TO CONNECT");
                    System.out.println(s_msg.getMessage());
                    socket.close();
                    result = JOptionPane.showConfirmDialog(null, myPanel,
                                                           "Username taken : Please enter Host address and username", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        this.screenName =  userNameField.getText();
                        this.serverIP = hostNameField.getText();
                        this.port = portField.getText();
                    } else {
                        System.exit(1);
                    }
                }
            }
            catch (Exception ex) {
                System.out.println("failed");
                ex.printStackTrace();
                result = JOptionPane.showConfirmDialog(null, myPanel,
                                                       "HostName unknown : Please enter Host address and username", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    System.out.println("x value: " + hostNameField.getText());
                    System.out.println("y value: " + userNameField.getText());
                    this.screenName =  userNameField.getText();
                    this.serverIP = hostNameField.getText();
                    this.port = portField.getText();
                } else {
                    System.exit(1);
                }
            }
        }
    }

    public int getMyport() {
        return myport;
    }

    public String getMyIP() {
        return myIP;
    }




    public void showChooser() {
        JFileChooser chooser = new JFileChooser();
        //FileNameExtensionFilter filter = new FileNameExtensionFilter(
        //   "JPG & GIF Images", "jpg", "gif");
        // chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                               chooser.getSelectedFile().getName());
            file = chooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());
            displayOnScreen(file.getAbsolutePath());

        }
    }

    public void displayOnScreen(String s) {
        textArea.insert(s + "\n", textArea.getText().length());
        textArea.setCaretPosition(textArea.getText().length());

    }

    public void startRxThread() {
        rxThread = new Receiver(myIP, myport);
        rxThread.start();


        Thread adminListen = new Thread(new Runnable() {
            public void run() {
                int rxp = rxThread.getBytesRead();
                while(rxThread.isAlive()) {
                    if(!rxThread.getfileDownloadDOne()) {
                        downloadProgLabel.setText("Download progress...");
                        downloadbar.setMaximum(100);
                        //if received packets changes get total packets and calculate the percentage completed
                        // System.out.println("sdfsdff");
                        float perc = (float) rxThread.getCurrent()  / (float) rxThread.getFileLength();
                        int percentage = (int)Math.round(perc*100);
                        // System.out.println(percentage);
                        downloadbar.setValue(percentage);

                        // System.out.println(rxp);
                        // it.getFile().setText(it.getRxObject().getGetFileName());
                        //  it.getTp().setText(""+ rounding( (((it.getRxObject().getPs()*rxp)/it.getRxObject().getTimerUDP().elapsedTime()) / 1000000), 2 ));
                    } else {
                        downloadbar.setValue(100);
                        downloadProgLabel.setText("download complete! " + rxThread.getFileName());
                    }
                }
            }
        });
        adminListen.start();



    }


    public void displayGUI() {

        addWindowListener(
        new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            }
        }
        );


        downloadbar = new JProgressBar();
        downloadbar.setMinimum(MINIMUM);


        uploadbar = new JProgressBar();
        uploadbar.setMinimum(MINIMUM);
        uploadbar.setMaximum(MAXIMUM);
        // add to JPanel

        resumeBtn = new JButton("resume");
        resumeBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                //rxThread.setPauseDL(false);
                System.out.println("resume pressed");
                // rxThread.informSender("resume");
            }
        });
        pauseBtn = new JButton("pause");
        pauseBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                //rxThread.setPauseDL(true);
                //rxThread.informSender("pause");
                System.out.println("pause pressed");
            }
        });



        myFilesListModel = new DefaultListModel();


        //Create the list and put it in a scroll pane.
        myFilesList= new JList(myFilesListModel);
        myFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myFilesList.setSelectedIndex(0);
        myFilesList.addListSelectionListener((ListSelectionListener) this);
        myFilesList.setVisibleRowCount(5);
        myFilesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 1) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());


                    String name = (String) myFilesListModel.getElementAt(index);
                    System.out.println(name);

                }

            }

        });

        JScrollPane myFileScrollPane = new JScrollPane(myFilesList);
        listModelSearches = new DefaultListModel();
        JPanel searchPanel = new JPanel(new BorderLayout());
        //Create the list and put it in a scroll pane.
        searchResultlist= new JList(listModelSearches);
        searchResultlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultlist.setSelectedIndex(0);
        searchResultlist.addListSelectionListener((ListSelectionListener) this);
        searchResultlist.setVisibleRowCount(5);
        searchResultlist.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 1) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    String name = (String) listModelSearches.getElementAt(index);
                    System.out.println(name);

                }
            }

        });

        JScrollPane searchfilesScrollPane = new JScrollPane(searchResultlist);

        OnlineUserListModel = new DefaultListModel();

        JPanel OnlineUsers = new JPanel(new BorderLayout());
        //Create the list and put it in a scroll pane.
        onlineUserList = new JList(OnlineUserListModel);
        onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUserList.setSelectedIndex(0);
        onlineUserList.addListSelectionListener((ListSelectionListener) this);
        onlineUserList.setVisibleRowCount(5);


        onlineUserList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());


                    String name = (String) OnlineUserListModel.getElementAt(index);
                    System.out.println(name);
                    typedText.setText("@"+name+" ");


                } else if (evt.getClickCount() == 3) {

                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                }
            }
        });



        downloadProgLabel = new JLabel("Download progress..");
        uploadProgLabel = new JLabel("Upload progress..");

        JPanel searchPane = new JPanel();
        JPanel lowerPane = new JPanel();
        lowerPane.setLayout(new GridLayout(1, 2));
        JPanel progressPane = new JPanel();
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(new JLabel("Progress Information:"));
        progressPane.add(pauseBtn);
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(resumeBtn);
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(downloadProgLabel);
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(downloadbar);
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(uploadProgLabel);
        progressPane.add(Box.createVerticalStrut(5));
        progressPane.add(uploadbar);
        progressPane.add(Box.createVerticalStrut(5));

        progressPane.setLayout(new BoxLayout(progressPane,BoxLayout.Y_AXIS));


        JPanel shareFilePane = new JPanel();
        shareFilePane.setLayout(new BoxLayout(shareFilePane,BoxLayout.Y_AXIS));
        JButton chooseFile = new JButton("Choose File");
        shareFilePane.add(new JLabel("Share your files:"));
        shareFilePane.add(chooseFile);
        chooseFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                showChooser();
            }
        });
        // shareFilePane.add(chooser);

        shareFilePane.add(myFileScrollPane);



        lowerPane.add(progressPane, BorderLayout.WEST);
        // lowerPane.add(typedText);
        //lowerPane.add(Box.createHorizontalStrut(5));
        lowerPane.add(shareFilePane);

        searchPane.setLayout(new BoxLayout(searchPane,BoxLayout.LINE_AXIS));

        downloadBtn = new JButton("Download");
        downloadBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    System.out.println("sending with TCP");
                    //sender.start();
                    if(typedText.getText().startsWith("@")) {

                        sendMessage(new ChatMessage(ChatMessage.DOWNLOADREQUEST,typedText.getText()));
                        typedText.setText("");
                        typedText.requestFocusInWindow();
                    }
                }
            }
        });

        searchBtn = new JButton("Search");

        searchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

            }
        });

        searchField = new JTextField();


        searchPane.add(searchField);
        searchPane.add(searchBtn);
        searchPane.add(downloadBtn);


        JScrollPane listScrollPane = new JScrollPane(onlineUserList);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        whisperBtn = new JButton("Whisper");
        whisperBtn.addActionListener(new WhisperListener());
        buttonPane.add(typedText);
        buttonPane.add(whisperBtn);

        OnlineUsers.add(listScrollPane, BorderLayout.CENTER);
        OnlineUsers.add(buttonPane, BorderLayout.PAGE_END);

        searchPanel.add(searchfilesScrollPane,BorderLayout.CENTER);

        searchPanel.add(searchPane, BorderLayout.PAGE_END);


        textArea.setEditable(false);
        textArea.setBackground(new Color(182, 208, 217));

        typedText.addActionListener(this);

        Container content = getContentPane();

        content.add(new JScrollPane(textArea), BorderLayout.CENTER);
        content.add(OnlineUsers, BorderLayout.EAST);
        JPanel chatbar = new JPanel();
        //chatbar.add(typedText);
        //content.add(chatbar);
        // content.add(typedText, BorderLayout.AFTER_LAST_LINE);
        content.add(lowerPane, BorderLayout.PAGE_END);
        content.add(searchPanel, BorderLayout.WEST);
        this.successConnect = true;
        // display the window, with focus on typing box
        setTitle("Chat Room: " + screenName );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        typedText.requestFocusInWindow();
        setVisible(true);
        this.Connect();

    }


    public void actionPerformed(ActionEvent e) {
        sendMessage(new ChatMessage(ChatMessage.MESSAGE,typedText.getText()));
        typedText.setText("");
        typedText.requestFocusInWindow();
    }


    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }
    }


    // listen to socket and print everything that server broadcasts
    public void listen() throws InterruptedException {
        ChatMessage s;
        while(true) {

            try {
                s = (ChatMessage) sInput.readObject();
                if(s != null) {

                    int type = s.getType();
                    switch(type) {
                    case ChatMessage.MESSAGE:
                        textArea.insert(s.getMessage() + "\n", textArea.getText().length());
                        textArea.setCaretPosition(textArea.getText().length());
                        break;
                    case ChatMessage.DOWNLOADREQUEST:
                        textArea.insert(s.getMessage() + "\n", textArea.getText().length());
                        textArea.setCaretPosition(textArea.getText().length());
                        int reqport = Integer.parseInt(s.getMessage().substring(s.getMessage().indexOf(">")+1,s.getMessage().length()));
                        System.out.println(reqport);
                        if (file != null) {
                            try {
                                sender = new Sender(file, reqport);
                                sender.start();



                                Thread adminListen = new Thread(new Runnable() {
                                    public void run() {
                                        while(sender.isAlive()) {
                                            uploadProgLabel.setText("Upload progress...");

                                            uploadbar.setMaximum(100);
                                            //if received packets changes get total packets and calculate the percentage completed
                                            // System.out.println("sdfsdff");
                                            float perc = (float) sender.getCurrent()  / (float) sender.getFileLength();
                                            int percentage = (int)Math.round(perc*100);
                                            // System.out.println(percentage);
                                            uploadbar.setValue(percentage);

                                            // System.out.println(rxp);
                                            // it.getFile().setText(it.getRxObject().getGetFileName());
                                            //  it.getTp().setText(""+ rounding( (((it.getRxObject().getPs()*rxp)/it.getRxObject().getTimerUDP().elapsedTime()) / 1000000), 2 ));

                                        }
                                        uploadProgLabel.setText("upload complete! " + sender.getFileName());
                                    }
                                });
                                adminListen.start();


//df












                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } else {
                            displayOnScreen("no files");
                        }
                        break;
                    case ChatMessage.USERSONLINE:
                        OnlineUserListModel.removeAllElements();
                        String[] onlineUsers = s.getUserList();
                        for(int i = 0; i < onlineUsers.length; i++) {
                            Thread.sleep(50);
                            OnlineUserListModel.insertElementAt(onlineUsers[i], 0);
                        }
                        break;
                    default:
                        break;
                    }
                }

            }
            catch(IOException e) {
                textArea.insert("Server has closed" + "\n", textArea.getText().length());
                break;
            }

            catch(ClassNotFoundException e2) {
            }
        }

        try {
            if(sInput != null) sInput.close();

        }
        catch(Exception e) {} // not much else I can do
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} // not much else I can do
        try {
            if(socket != null) socket.close();
        }
        catch(Exception e) {} // not much else I can do
        System.err.println("Closed client socket");
    }
    public void Connect() {

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ChatClient client = new ChatClient();
        client.startRxThread();

        //client.listenForPrivateCalls();
        client.listen();
    }

    class WhisperListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = onlineUserList.getSelectedIndex();
            String name = (String) OnlineUserListModel.getElementAt(index);
            System.out.println(name);
            typedText.setText("@"+name+" ");
            typedText.requestFocusInWindow();
            //  listModel.remove(index);

            int size = OnlineUserListModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                whisperBtn.setEnabled(false);

            } else { //Select an index.
                if (index == OnlineUserListModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                onlineUserList.setSelectedIndex(index);
                onlineUserList.ensureIndexIsVisible(index);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
