import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

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

    private String myIP;
    private int myport;
    private String destinationPathUDP = "/home/frank/Dropbox/WorkSpace_ONCLOUD/RBUDP/dest/";
    private String destinationPathTCP = "destTCP/";
    static boolean losspacket = false;

    public Receiver(String ip, int port) {
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
        int bytesRead = 0;

        while((bytesRead=is.read(contents))!=-1)
            bos.write(contents, 0, bytesRead);

        bos.flush();
        clientSocket.close();
        serverSocket.close();
        System.out.println("File saved successfully!");




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

            serverSocket = new ServerSocket(8000);
            System.out.println("waiting for files");
            clientSocket = serverSocket.accept();
            System.err.println("Created socket with client");
            sInput  = new ObjectInputStream(clientSocket.getInputStream());
            sOutput = new ObjectOutputStream(clientSocket.getOutputStream());


            sourceFilePath = (String) sInput.readObject();
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

    }

    public static void main(String[] args) throws InterruptedException {
        new Receiver("localhost",4354).run();

    }


}