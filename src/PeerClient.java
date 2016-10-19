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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Stream;
import java.math.*;

/**
 * This class is the gui for the client, it handles all user input. 
 * @author frank
 *
 */
public class PeerClient extends JFrame implements ActionListener,ListSelectionListener  {

    private static final long serialVersionUID = 1L;
    private JList<String> onlineUserList;
    private DefaultListModel<String> OnlineUserListModel;
   
    static final int MAXIMUM = 100;
    static final int MINIMUM = 0;
    private JButton searchBtn;
    private JButton chooseDLFolderBtn;
    private JButton chooseFolderBtn;
    private Vector<File> sharedFiles;
    private String[] foundItems;
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
    private String downloader;
    private String uploader;
    private String selectedFile;
    private boolean successConnect = false;
    private String key;
    private Key masterkey;
    private String generatedKey;
    private JFileChooser   folderchooser;
    @SuppressWarnings("unchecked")
    public PeerClient() {
        this.serverIP = "lol";
        this.screenName = "";
        key = "Bar12345Bar12345"; // 128 bit key
        masterkey = new SecretKeySpec(key.getBytes(), "AES");

        JTextField hostNameField = new JTextField(5);
        JTextField portField = new JTextField(5);
        JTextField userNameField = new JTextField(5);
        JTextField myIPField = new JTextField(5);
        hostNameField.setText("localhost");
        sharedFiles = new Vector<File>();
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
        System.out.println("Working Directory = " +
                           System.getProperty("user.dir"));
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
/**
 *  returns the port of the client
 * @return
 */
    public int getMyport() {
        return myport;
    }

    /**
     * returns the generated key of the client.
     * @return
     */
    public String getGeneratedKey() {
        return generatedKey;
    }
    
    /**
     * Returns the IP of the client.
     * @return
     */

    public String getMyIP() {
        return myIP;
    }



/**
 * This function shows the file chooser
 */
    public void showChooser() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                               chooser.getSelectedFile().getName());
            file = chooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());
            displayOnScreen(file.getAbsolutePath());

        }
    }
    
    /**
     * This function shows the folder chooser.
     */
    public void showFolderChooser() {
        folderchooser = new JFileChooser();
        folderchooser.setCurrentDirectory(new java.io.File("."));
        folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderchooser.setAcceptAllFileFilterUsed(false);
        if (folderchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                               +  folderchooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                               +  folderchooser.getSelectedFile());
        }
        else {
            System.out.println("No Selection ");
        }

    }

    /*
     * 
     * This method displays msg on screen.
     */
    public void displayOnScreen(String s) {
        textArea.insert(s + "\n", textArea.getText().length());
        textArea.setCaretPosition(textArea.getText().length());

    }

/**
 * This function builds the client GUI.
 */

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
                rxThread.informSender("resume");
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
                rxThread.informSender("pause");
                System.out.println("pause pressed");
            }
        });

        myFilesListModel = new DefaultListModel();
        //Create the list and put it in a scroll pane.
        myFilesList= new JList(myFilesListModel);
        myFilesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myFilesList.setSelectedIndex(0);
        myFilesList.addListSelectionListener((ListSelectionListener) this);
        myFilesList.setVisibleRowCount(10);
        myFilesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 1) {
                    if(!myFilesListModel.isEmpty()) {
                        int index = list.locationToIndex(evt.getPoint());
                        String name = (String) myFilesListModel.getElementAt(index);
                        System.out.println(name);
                    }
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
        searchResultlist.setVisibleRowCount(10);
        searchResultlist.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {

                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 1) {

                    // Double-click detected
                    if(!listModelSearches.isEmpty()) {
                        int index = list.locationToIndex(evt.getPoint());
                        String name = (String) listModelSearches.getElementAt(index);
                        selectedFile = name.substring(0, name.lastIndexOf(":"));
                        uploader = name.substring(name.lastIndexOf(":")+1,name.length());
                        System.out.println(uploader);
                        System.out.println(name);
                    }

                }
            }

        });

        JScrollPane searchfilesScrollPane = new JScrollPane(searchResultlist);

        OnlineUserListModel = new DefaultListModel();

        JPanel OnlineUsers = new JPanel(new BorderLayout());
        OnlineUsers.setPreferredSize(new Dimension(120,80));
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
        JPanel lowersearchPane = new JPanel();
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
        JButton deleteFile = new JButton("Remove file");
        JButton chooseFolder = new JButton("Choose Folder");
        chooseFolder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                showFolderChooser();
                if(folderchooser != null) {

                    try(Stream<Path> paths = Files.walk(Paths.get(folderchooser.getSelectedFile().toString()))) {
                        paths.forEach(filePath -> {
                            if (Files.isRegularFile(filePath)) {
                                File f = new File(filePath.toString());
                                System.out.println(f.getName());
                                if(!sharedFiles.contains(f)) {
                                    sharedFiles.add(f);
                                    myFilesListModel.insertElementAt(f.getAbsolutePath(), 0);
                                }
                            }
                        });
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        JButton chooseFile = new JButton("Choose File");
        shareFilePane.add(new JLabel("Share your files:"));
        shareFilePane.add(chooseFile);
        shareFilePane.add(chooseFolder);
        chooseFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                showChooser();
                if(file != null) {
                    if(!sharedFiles.contains(file)) {
                        sharedFiles.add(file);
                        myFilesListModel.insertElementAt(file.getAbsolutePath(), 0);
                    }
                }



            }
        });
        shareFilePane.add(myFileScrollPane);
        lowerPane.add(progressPane, BorderLayout.WEST);
        lowerPane.add(shareFilePane);
        searchPane.setLayout(new BoxLayout(searchPane,BoxLayout.LINE_AXIS));
        downloadBtn = new JButton("Download");
        downloadBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    try {
                        System.out.println("sending with TCP");

                        chooseDLFolderBtn.setEnabled(false);
                        Random rng = new Random();
                        //sender.staRandomrt();
                        byte[] r = new byte[16]; //Means 2048 bit
                        rng.nextBytes(r);
                        generatedKey = Base64.getUrlEncoder().encodeToString(r);
                        rxThread.setKey(generatedKey);
                        System.out.println("random key");
                        displayOnScreen(generatedKey);
                        String encryptthis = selectedFile+":"+generatedKey;
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.ENCRYPT_MODE, masterkey);
                        byte[] encrypted = cipher.doFinal(encryptthis.getBytes());
                        sendMessage(new ChatMessage(ChatMessage.DOWNLOADREQUEST,"@"+uploader + " +" + "hi", encrypted));
                        typedText.setText("");
                        typedText.requestFocusInWindow();

                    } catch (InvalidKeyException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        chooseDLFolderBtn = new JButton("Choose Download Folder");
        chooseDLFolderBtn.setToolTipText("This is the folder where all the files will be downloaded to");

        chooseDLFolderBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                rxThread.setDLPath(chooseDLFolder().getSelectedFile().getAbsolutePath());
            }
        });

        searchBtn = new JButton("Search");

        searchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listModelSearches.removeAllElements();
                // TODO Auto-generated method stub
                if(!searchField.getText().equals(""))
                    sendMessage(new ChatMessage(ChatMessage.SEARCH, searchField.getText().toLowerCase(), myport));
            }
        });

        searchField = new JTextField();
        searchPane.add(searchField);
        searchPane.add(searchBtn);
        searchPane.add(downloadBtn);
        lowersearchPane.add(chooseDLFolderBtn);
        JScrollPane listScrollPane = new JScrollPane(onlineUserList);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(typedText);
        OnlineUsers.add(listScrollPane, BorderLayout.CENTER);
        OnlineUsers.add(buttonPane, BorderLayout.PAGE_END);
        searchPanel.add(searchPane, BorderLayout.BEFORE_FIRST_LINE);
        searchPanel.add(searchfilesScrollPane,BorderLayout.CENTER);
        searchPanel.add(lowersearchPane, BorderLayout.AFTER_LAST_LINE);
        textArea.setEditable(false);
        textArea.setBackground(new Color(182, 208, 217));

        typedText.addActionListener(this);

        Container content = getContentPane();

        content.add(new JScrollPane(textArea), BorderLayout.CENTER);
        content.add(OnlineUsers, BorderLayout.EAST);
        JPanel chatbar = new JPanel();
        //chatbar.add(typedText);
        //content.add(chatbar);cd .
        // content.add(typedText, BorderLayout.AFTER_LAST_LINE);
        content.add(lowerPane, BorderLayout.PAGE_END);
        content.add(searchPanel, BorderLayout.WEST);
        this.successConnect = true;
        // display the window, with focus on typing box
        setTitle("Peer 2 Peer: " + screenName );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        typedText.requestFocusInWindow();
        setVisible(true);
    }

/**
 * This function returns the folder that the client chose to download in.
 * @return folder for download.
 */

    public JFileChooser chooseDLFolder() {
        JFileChooser folderchooser = new JFileChooser();
        folderchooser.setCurrentDirectory(new java.io.File("."));
        folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderchooser.setAcceptAllFileFilterUsed(false);
        if (folderchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                               +  folderchooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                               +  folderchooser.getSelectedFile());
            return folderchooser;
        }
        else {
            System.out.println("No Selection ");
        }
        return null;


    }
    /**
     * function handles actions on text input field.
     */
    public void actionPerformed(ActionEvent e) {
        sendMessage(new ChatMessage(ChatMessage.MESSAGE,typedText.getText()));
        typedText.setText("");
        typedText.requestFocusInWindow();
    }

    /**
     * This function writes to the TCP socket, or the server.
     * @param msg the chatmessage obj to send the server.
     */

    void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }
    }

/**
 * This function starts the receiving thread.
 */

    public void startRxThread() {
        rxThread = new Receiver(myIP, myport);
        rxThread.start();
        Thread dbarThread = new Thread(new Runnable() {
            public void run() {
                while(rxThread.isAlive()) {
                    if(!rxThread.getfileDownloadDOne()) {
                        downloadProgLabel.setText("Download progress...");
                        downloadbar.setMaximum(100);
                        float perc = (float) rxThread.getCurrent()  / (float) rxThread.getFileLength();
                        int percentage = (int)Math.round(perc*100);
                        downloadbar.setValue(percentage);
                    } else {
                        downloadbar.setValue(100);
                        downloadProgLabel.setText("download complete! " + rxThread.getFileName());
                        chooseDLFolderBtn.setEnabled(true);
                    }
                }
            }
        });
        dbarThread.start();
    }


 /**
  * This function listens for messages from the server.
  * @throws InterruptedException
  * @throws InvalidKeyException
  * @throws NoSuchAlgorithmException
  * @throws NoSuchPaddingException
  * @throws IllegalBlockSizeException
  * @throws BadPaddingException
  */
    public void listen() throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
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
                        String msg = s.getMessage();
                        // String reqfile = msg.substring(msg.indexOf("+")+1, msg.indexOf(">"));
                        byte[] enc = s.getEncryptedMsg();
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.DECRYPT_MODE, masterkey);
                        String totalstr = new String(cipher.doFinal(enc));

                        String key = totalstr.substring(totalstr.indexOf(":")+1, totalstr.length());
                        String filename = totalstr.substring(0,totalstr.indexOf(":"));

                        File toup = null;
                        for (File kth :sharedFiles) {
                            if(filename.equals(kth.getName())) {
                                toup = kth;
                            }
                        }

                        displayOnScreen(key + " :" +filename);
                        if (toup != null) {
                            try {
                                sender = new Sender(toup, reqport,key);
                                sender.start();
                                Thread adminListen = new Thread(new Runnable() {
                                    public void run() {
                                        while(sender.isAlive()) {
                                            uploadProgLabel.setText("Upload progress...");
                                            uploadbar.setMaximum(100);
                                            float perc = (float) sender.getCurrent()  / (float) sender.getFileLength();
                                            int percentage = (int)Math.round(perc*100);
                                            uploadbar.setValue(percentage);
                                        }
                                        uploadProgLabel.setText("upload complete! " + sender.getFileName());
                                    }
                                });
                                adminListen.start();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } else {
                            displayOnScreen("No such file.");
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

                    case ChatMessage.SEARCH:
                        foundItems = new String[sharedFiles.size()];
                        int i = 0;
                        for (File file : sharedFiles) {
                            if (file.getName().toLowerCase().indexOf(s.getMessage()) != -1) {
                                foundItems[i] = file.getName() + ":"+ screenName;
                                i++;
                            }
                        }
                        sendMessage(new ChatMessage(ChatMessage.SEARCH_RESULT, foundItems, s.getPort()));

                        break;
                    case ChatMessage.SEARCH_RESULT:
                        String results[] = s.getSearchResults();

                        for (int j = 0; j < results.length; j++) {
                            listModelSearches.insertElementAt(results[j], 0);
                            Thread.sleep(50);
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
 

    public static void main(String[] args) throws InterruptedException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        PeerClient client = new PeerClient();
        client.startRxThread();
        client.listen();
    }



    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }
}