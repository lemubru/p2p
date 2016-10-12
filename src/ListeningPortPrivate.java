import java.io.*;
import java.net.*;
import java.util.Enumeration;

import javax.sound.sampled.*;

public class ListeningPortPrivate {


    private ByteArrayOutputStream byteOutputStream;
    private AudioFormat adFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream InputStream;
    private SourceDataLine sourceLine;
    private MulticastSocket msocket;
    private String groups[];
    private int port;
    private DatagramSocket psocket;
    private boolean listening;
    public ListeningPortPrivate(int port) throws IOException {

        this.port = port;
        psocket = new DatagramSocket(port);
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }


    public int getPort() {
        return this.port;
    }


    public void setPort(int port) {
        this.port = port;

    }

    public void joinNewGroup(InetAddress address) throws IOException {
        msocket.joinGroup(address);

    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public boolean getListeningOnPP() {
        return this.listening;
    }
    public void runVOIP() {
        try {
            // System.out.println("listening on UDP socket... transmitting any audio");
            //DatagramSocket serverSocket = new DatagramSocket(4523); //(9786);
            // msocket = new MulticastSocket(port);
            // InetAddress address = InetAddress.getByName("230.0.0.1");
            // msocket.joinGroup(address);
            // psocket.setLoopbackMode(true);
            listening = true;
            byte[] receiveData = new byte[10000];
            while (listening) {
                // serverSocket.receive(receivePacket);
                //System.out.println("RECEIVED: " + receivePacket.getAddress().getHostAddress() + " " + receivePacket.getPort());

                /*
                 */

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                System.out.println("Listening for rx");
                psocket.receive(receivePacket);
                //System.out.println("RECEIVED: " + receivePacket.getAddress().getHostAddress() + " " + receivePacket.getPort());
                try {

                    byte audioData[] = receivePacket.getData();
                    InputStream byteInputStream = new ByteArrayInputStream(audioData);
                    AudioFormat adFormat = getAudioFormat();
                    InputStream = new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize());
                    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
                    sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceLine.open(adFormat);
                    sourceLine.start();
                    Thread playThread = new Thread(new PlayThread());
                    playThread.start();


                } catch (Exception e) {
                    System.out.println(e);
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int cnt;
                while ((cnt = InputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceLine.write(tempBuffer, 0, cnt);
                    }
                }
                //  sourceLine.drain();
                // sourceLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}