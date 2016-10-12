import java.awt.FontFormatException;
import java.awt.PageAttributes;
import java.io.*;
import java.net.*;
<<<<<<< HEAD
import java.util.Arrays;
import java.util.Random;

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
    private int sendport = 0;
    private boolean showpause = false;
    private FileObj filedata;
    public Sender(File file, int port) throws IOException {
        this.file = file;
        sendport = port;
        sourceFilePath = this.file.getAbsolutePath();
        filedata = new FileObj(file.getAbsolutePath(), (int)file.length());
        TCPsocket = new Socket(hostName,port);
        sOutput = new ObjectOutputStream(TCPsocket.getOutputStream());
        sInput  = new ObjectInputStream(TCPsocket.getInputStream());

        //listenForMsg();

    }

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

    synchronized void waits() {
        try {
            // Calling wait() will block this thread until another thread
            // calls notify() on the object.
            this.wait();
        } catch (InterruptedException e) {
            // Happens if someone interrupts your thread.
        }
    }

    public void txWithTCP() throws IOException, InterruptedException {
        InetAddress IA = InetAddress.getByName(hostName);
        File file = this.file;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        OutputStream os = TCPsocket.getOutputStream();
        byte[] contents;
        long fileLength = file.length();
        long current = 0;
        System.out.println("txing");
        while(current!=fileLength) {
            int x = 0;
            //while(pauseUpload == true) {
            //   x = 3+ 3 + 3*3;

            // System.out.println("pause");
            if(pauseUpload) {
                while(!Thread.currentThread().isInterrupted()) {
                    if(pauseUpload == false) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            //}
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
        System.out.println("File sent succesfully!");
        //System.exit(0);
    }

    public void createConnection() throws InterruptedException, ClassNotFoundException, IOException {

        //event = createFileObj();


        int[] transmission_data = new int[4];
        // transmission_data[FILESIZE] = filebyteArr.length;
        //transmission_data[PACKETSIZE] = packetsize;
        //transmission_data[NUMPACKETS] = numpackets;
        transmission_data[3] = transMissionProtocol;

        try {
            //Connect to the receiver

            /**
             * Send receiver info about the file over TCP
             */


            //Output.writeObject(sourceFilePath);
            sOutput.writeObject(filedata);
            //sOutput.writeObject(this.file.length());


            txWithTCP();




        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }



    public void listenForMsg() throws IOException {
        Thread thread = new Thread() {

            public void run() {
                System.out.println("listening on private port...");
                try {
                    while(true) {

                        String s = (String) sInput.readObject();
                        if(s.equals("pause")) {
                            System.out.println("waiting");
                            // halt();
                            pauseUpload = true;
                            showpause = true;
                        } else {
                            // wake();
=======
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





    public Sender(File file, int port) throws IOException {
        this.file = file;
        sendport = port;
        sourceFilePath = this.file.getAbsolutePath();
        fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        filedata = new FileObj(file.getAbsolutePath(), file.length());
        TCPsocket = new Socket(hostName,port);
        sOutput = new ObjectOutputStream(TCPsocket.getOutputStream());
        sInput  = new ObjectInputStream(TCPsocket.getInputStream());
        uploadDone = false;
        this.lock = lock;

//        KeyGenerator kg = KeyGenerator.getInstance("DES");
//		kg.init(new SecureRandom());
//		SecretKey key = kg.generateKey();
//		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
//		Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
//		DESKeySpec ks = (DESKeySpec) skf.getKeySpec(key, spec);
        listenForMsg();

    }

    public String getFileName() {
        return fileName;
    }

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
            //while(pauseUpload == true) {
            //   x = 3+ 3 + 3*3;

            // System.out.println("pause");
            if(pauseUpload) {

                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                    System.out.println("hello");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                /**
                 *
                while(!Thread.currentThread().isInterrupted()) {
                    if(pauseUpload == false) {
                        Thread.currentThread().interrupt();


                    }
                }
                 */
                //Thread.currentThread().sleep(10);

            }

            //}
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
        //System.exit(0);
    }

    public void createConnection() throws InterruptedException, ClassNotFoundException, IOException {

        //event = createFileObj();


        int[] transmission_data = new int[4];
        // transmission_data[FILESIZE] = filebyteArr.length;
        //transmission_data[PACKETSIZE] = packetsize;
        //transmission_data[NUMPACKETS] = numpackets;
        transmission_data[3] = transMissionProtocol;

        try {
            //Connect to the receiver

            /**
             * Send receiver info about the file over TCP
             */


            //Output.writeObject(sourceFilePath);
            sOutput.writeObject(filedata);
            //sOutput.writeObject(this.file.length());


            txWithTCP();




        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }



    public void listenForMsg() throws IOException {
        Thread thread = new Thread() {

            public void run() {
                System.out.println("listening on private port...");
                try {
                    while(true) {

                        String s = (String) sInput.readObject();
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
>>>>>>> branch 'master' of https://Shaun_de_Jager@bitbucket.org/frankdp1993/cs354_p2p.git
                            pauseUpload = false;
                            showpause = false;
                        }
                        System.out.println("Mess rxd" + s);

                    }
                } catch (ClassNotFoundException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }




}
