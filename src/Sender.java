

import java.awt.FontFormatException;
import java.awt.PageAttributes;
import java.io.*;
import java.net.*;

import java.util.Arrays;
import java.util.Random;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class handles the uploading of files and security on the sending side.  It also listen for a pause message from the receiver
 * @author frank
 *
 */
public class Sender extends Thread {
    static final int FILESIZE = 0, PACKETSIZE = 1, NUMPACKETS = 2, ALL_PACKETS_RECEIVED = 5, PACKETS_MISSING = 6, STILLSENDING = 7, SIGNAL = 8, DONE = 9,RETRANSMISSION = 10;
    static final int TCP = 3,  UDP = 4;
    private DatagramSocket socket = null;
    private Socket TCPsocket = null;

    private String sourceFilePath = "/home/frank/Dropbox/WorkSpace_ONCLOUD/RBUDP/files/Proj2.pdf";
    private String destinationPath = "/home/student/17020247/workspace/RBUDP/dest/";
    private String hostName = "localhost";
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;

    private int transMissionProtocol = 0;
    private int packetSize = 0;
    private boolean pauseUpload = false;
    File file;
    private int packet_loss_prob;
    private int seqSize = 0;
    private long fileLength;
    private long current;
    private int sendport = 0;
    private boolean uploadDone;
    private String fileName;
    private boolean showpause = false;
    private FileObj filedata;

    private Object lock = new Object();
    private boolean clearToTx;
    private String key;

    public Sender(File file, int port, String key) throws IOException, ClassNotFoundException {
        clearToTx = false;
        this.file = file;
        sendport = port;
        sourceFilePath = this.file.getAbsolutePath();
        fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        filedata = new FileObj(file.getAbsolutePath(), file.length(),key);
        TCPsocket = new Socket(hostName,port);
        sOutput = new ObjectOutputStream(TCPsocket.getOutputStream());
        sInput  = new ObjectInputStream(TCPsocket.getInputStream());
        uploadDone = false;
        this.lock = lock;

        this.key = key;
    }

    public boolean getClearToTx() {
        return clearToTx;
    }

/**
 * This function sends a message to the receiver telling him that anything.
 * @param msg
 */
    public void informReceiver(String msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            //   textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }

    }

/**
 * Get the filename that is being transmitted.
 * @return
 */
    public String getFileName() {
        return fileName;
    }
/**
 * Set the boolean to pause the DL.
 * @param pauseUpload
 */
    public void setPauseUpload(boolean pauseUpload) {
        this.pauseUpload = pauseUpload;
    }

    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public void run()  {
        try {
            createConnection();

        } catch (ClassNotFoundException | InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public long getCurrent() {
        return current;
    }

    public long getFileLength() {
        return fileLength;
    }

    public boolean getUploadDone() {
        return uploadDone;
    }
    /**
     * This function sends the file to the receiver by write onto the stream.
     * @throws IOException
     * @throws InterruptedException
     */

    public void txWithTCP() throws IOException, InterruptedException {
        InetAddress IA = InetAddress.getByName(hostName);
        File file = this.file;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream os = TCPsocket.getOutputStream();
        byte[] contents;
        fileLength = file.length();
        current = 0;
        System.out.println("txing FILE SIZE" + fileLength);
        while(current!=fileLength) {
            int x = 0;
            if(pauseUpload) {
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                    System.out.println("hello");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
 
            }
          
            int size = 8192;
            if(fileLength - current >= size)
                current += size;
            else {
                size = (int)(fileLength - current);
                current = fileLength;
            }
            contents = new byte[size];
            bis.read(contents, 0, size);
            os.write(contents);
        }
        os.flush();
        TCPsocket.close();
        uploadDone = true;
        System.out.println("File sent succesfully!");
    }
    /**
     * This function creates a connection between them and ensures security.
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws IOException
     */

    public void createConnection() throws InterruptedException, ClassNotFoundException, IOException {
        try {
            sOutput.writeObject(filedata);
            ChatMessage response = (ChatMessage) sInput.readObject();

            if(response.getType() == ChatMessage.CORRECTKEY) {
                clearToTx = true;
                listenForMsg();
                txWithTCP();

            } else {

            }


        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


/**
 * This function listens for a pause/resume msg from receiver.
 * @throws IOException
 */
    public void listenForMsg() throws IOException {
        Thread thread = new Thread() {

            public void run() {
                System.out.println("listening on private port...");
                try {
                    while(!TCPsocket.isClosed()) {
                    	String s = null;
                    	if(TCPsocket.isConnected())
                        s = (String) sInput.readObject();
                        
                    	if(s.equals("pause")) {
                            System.out.println("waiting");
                            // halt();
                            pauseUpload = true;
                            showpause = true;
                        } else {
                            // wake();
                            synchronized (lock) {
                                lock.notify();
                            }
                            pauseUpload = false;
                            showpause = false;
                        }
                        System.out.println("Mess rxd" + s);

                    }
                } catch (ClassNotFoundException | IOException e) {
                    try {
                        TCPsocket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        
                    	//e1.printStackTrace();
                    }
                    // TODO Auto-generated catch block
                   // e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
