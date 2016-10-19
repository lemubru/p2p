import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import java.math.*;


/**
 * 
 * @author frank
 * This class handles all downloads, security on the receiving side, and pausing/resuming of downloads.
 */
public class Receiver extends Thread {
    private static final long serialVersionUID = 1L;
    private DatagramSocket socket = null;
    private ObjectInputStream sInput;
    private int ps;
    private int transMissionProtocol = 0;
    private int receivedPackets = 0;
    private int totalPackets = 0;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean pauseDL = false;
    private ObjectOutputStream sOutput;
    private String sourceFilePath;
    private int bytesRead;
    private boolean fileDownloadDone;
    private String myIP;
    private long fileLength;
    private String fileName;
    private FileObj filedata;
    private int myport;
    private String destinationPathUDP = "/home/frank/Dropbox/WorkSpace_ONCLOUD/RBUDP/dest/";
    private String destinationPathTCP = "/home/frank/destTCP/";
    static boolean losspacket = false;
    private long current = 0;
    private String skey;
    public Receiver(String ip, int port) {
        myport = port;
        bytesRead = 0;
        fileDownloadDone = false;
    }

    public long getCurrent() {
        return current;
    }

    public void setKey(String key) {
        this.skey = key;
    }


    public void setPauseDL(boolean pauseDL) {
        System.out.println("pausing DL");
        this.pauseDL = pauseDL;
    }
    public int getTotalPackets() {
        return totalPackets;
    }
    public int getRxPackets() {
        return receivedPackets;
    }

    public void setDLPath(String path) {
        destinationPathTCP = path+"/";
        System.out.println("new dl dir" + destinationPathTCP);

    }


    public String getFileName() {
        return fileName;
    }

/**
 * This function rx with TCP.
 * @throws IOException
 */
    public void rxWithTCP() throws IOException {
        int TCPpacketsize = 524288;
        System.out.println("Receiving file...");

        byte[] contents = new byte[TCPpacketsize];

        //Initialize the FileOutputStream to the output file's full path.
        fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());

        FileOutputStream fos = new FileOutputStream(destinationPathTCP+fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = clientSocket.getInputStream();

        current = 0;
        //No of bytes read in one read() call
        bytesRead = 0;

        while((bytesRead=is.read(contents))!=-1) {
            bos.write(contents, 0, bytesRead);
            if(bytesRead >= 0) current += bytesRead;
            //System.out.println(bytesRead);
        }


        bos.flush();
        clientSocket.close();
        serverSocket.close();
        System.out.println(bytesRead);
        System.out.println("File saved successfully!");
        fileDownloadDone  =true;


    }

    public int getBytesRead() {
        return bytesRead;
    }

    public long getFileLength() {
        return fileLength;
    }


    public boolean getfileDownloadDOne() {
        return fileDownloadDone;
    }



    public int getPs() {
        return ps;
    }

/**
 * This function sends a message to the tx'er telling him to stop or resume.
 * @param msg
 */
    public void informSender(String msg) {

        try {

            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            //   textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }

    }
    /**
     * This function creates a connection between tx and rx peers.
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */

    public void createConnectionNow() throws InterruptedException, IOException, ClassNotFoundException {

        int fileSize = 0;
        int packetSize = 0;
        int numPackets = 0;

        while(true) {
            serverSocket = new ServerSocket(myport);
            System.out.println("waiting for files");
            clientSocket = serverSocket.accept();
            System.err.println("Created socket with client");
            sInput  = new ObjectInputStream(clientSocket.getInputStream());
            sOutput = new ObjectOutputStream(clientSocket.getOutputStream());

            filedata = (FileObj) sInput.readObject();



            sourceFilePath = filedata.getFilePath();
            fileLength = filedata.getFileLength();

            String key = filedata.getKey();
            if(key.equals(skey)) {

                sOutput.writeObject(new ChatMessage(ChatMessage.CORRECTKEY, "correct"));
                // BigInteger in = new BigInteger(Integer.toString(fileLength));
                //System.out.println(in);
                //  Stopwatch timer2 = new Stopwatch();
                fileDownloadDone = false;
                rxWithTCP();
            } else {
                sOutput.writeObject(new ChatMessage(ChatMessage.UNKNOWNKEY, "incorrect"));
            }
        }


        //System.exit(0);
    }



/**
 * Get the filename thats being downloaded.
 * @return
 */
    public String getGetFileName() {
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
    }

    public void run() {
        try {
            createConnectionNow();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new Receiver("localhost",4354).run();

    }


}