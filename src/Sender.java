import java.awt.FontFormatException;
import java.awt.PageAttributes;
import java.io.*;
import java.net.*;
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
    public Sender(File file, int port) throws IOException {
        this.file = file;
        sendport = port;
        sourceFilePath = this.file.getAbsolutePath();

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
            sOutput.writeObject(this.file.getAbsolutePath());


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
