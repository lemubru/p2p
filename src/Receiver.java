import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
<<<<<<< HEAD

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
    private int fileLength;
    private FileObj filedata;
    private int myport;
    private String destinationPathUDP = "/home/frank/Dropbox/WorkSpace_ONCLOUD/RBUDP/dest/";
    private String destinationPathTCP = "destTCP/";
    static boolean losspacket = false;

    public Receiver(String ip, int port) {
        myport = port;
        bytesRead = 0;
        fileDownloadDone = false;
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

    public void rxWithTCP() throws IOException {
        int TCPpacketsize = 524288;
        System.out.println("TCP: protocol is TCP");

        byte[] contents = new byte[TCPpacketsize];

        //Initialize the FileOutputStream to the output file's full path.
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());

        FileOutputStream fos = new FileOutputStream(destinationPathTCP+fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = clientSocket.getInputStream();

        //No of bytes read in one read() call
        bytesRead = 0;

        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead);

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

    public int getFileLength() {
        return fileLength;
    }


    public boolean getfileDownloadDOne() {
        return fileDownloadDone;
    }



    public int getPs() {
        return ps;
    }


    public void informSender(String msg) {

        try {

            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            //   textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }

    }

    public void rxWithUDP() throws InterruptedException, IOException, ClassNotFoundException {

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
            System.out.println(fileLength);
            //  Stopwatch timer2 = new Stopwatch();
            rxWithTCP();
        }


        //System.exit(0);
    }




    public String getGetFileName() {
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
    }

    public void run() {
        try {
            rxWithUDP();
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

=======
import java.math.*;

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
    private int current = 0;

    public Receiver(String ip, int port) {
        myport = port;
        bytesRead = 0;
        fileDownloadDone = false;
    }

    public int getCurrent() {
        return current;
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




    public String getFileName() {
        return fileName;
    }

    public void rxWithTCP2() throws IOException {
        int TCPpacketsize = 999999999;
        System.out.println("Receiving file...");

        byte[] contents = new byte[TCPpacketsize];

        //Initialize the FileOutputStream to the output file's full path.
        fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());

        FileOutputStream fos = new FileOutputStream(destinationPathTCP+fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = clientSocket.getInputStream();

        //No of bytes read in one read() call
        byte [] byteArray  = new byte [999999999]; //SET THE BYTEARRAY 100 BYTES EXTRA
        bytesRead = is.read(byteArray,0,byteArray.length);
        current = bytesRead;
        do {
            bytesRead = is.read(byteArray, current, (byteArray.length-current));
            if(bytesRead >= 0) current += bytesRead;
            //progressBar.setValue(current);
        } while(bytesRead > -1);

        bos.write(byteArray, 0 , current);
        bos.flush();
        //System.out.println(current);
        bos.close();
        fos.close();
        System.out.println("File saved successfully!");
        fileDownloadDone  =true;


    }







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


    public void informSender(String msg) {

        try {

            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            //   textArea.insert("Server has closed" + "\n", textArea.getText().length());
            System.out.println("Exception writing to server: " + e);
        }

    }

    public void rxWithUDP() throws InterruptedException, IOException, ClassNotFoundException {

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
            // BigInteger in = new BigInteger(Integer.toString(fileLength));
            //System.out.println(in);
            //  Stopwatch timer2 = new Stopwatch();
            fileDownloadDone = false;
            rxWithTCP();
        }


        //System.exit(0);
    }




    public String getGetFileName() {
        return sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
    }

    public void run() {
        try {
            rxWithUDP();
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
>>>>>>> branch 'master' of https://Shaun_de_Jager@bitbucket.org/frankdp1993/cs354_p2p.git
    }

    public static void main(String[] args) throws InterruptedException {
        new Receiver("localhost",4354).run();

    }


}