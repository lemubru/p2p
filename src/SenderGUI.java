import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

public class SenderGUI extends JFrame implements ActionListener {

    String tcpString = "TCP";
    String udpString = "UDP";
    private File file;
    private JButton chooseFileBtn;
    private JButton sendBtn;
    private boolean tcp = false, udp = false;
    private int transMissionProtocol = Sender.TCP;
    private int ps;
    private Sender sender;
    private int bs;
    private int plp;
    public SenderGUI(int ps, int bs, int plp) {
        this.plp = plp;
        this.file = null;
        this.ps  = ps;
        this.bs = bs;

        JRadioButton tcpButton = new JRadioButton(tcpString);
        tcpButton.setEnabled(false);
        tcpButton.setMnemonic(KeyEvent.VK_B);
        tcpButton.setActionCommand(tcpString);
        tcpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                System.out.println("tcp selected");
                tcp = true;
                udp = false;
                transMissionProtocol = Sender.TCP;
                try {
                    sender = new Sender(file, transMissionProtocol,bs, ps, plp);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        tcpButton.setSelected(true);


        JRadioButton udpButton = new JRadioButton(udpString);
        udpButton.setEnabled(false);
        udpButton.setMnemonic(KeyEvent.VK_C);
        udpButton.setActionCommand(udpString);
        udpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                System.out.println("udp selected");
                udp = true;
                tcp = false;
                transMissionProtocol = Sender.UDP;
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(tcpButton);
        group.add(udpButton);

        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(tcpButton);
        radioPanel.add(udpButton);




        addWindowListener(
        new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                System.exit(1);
//                    try                   { socket.close();        }
//                    catch (Exception ioe) { ioe.printStackTrace(); }
            }
        }
        );
        sendBtn  = new JButton("Send");
        sendBtn.setEnabled(false);
        sendBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    System.out.println("sending with TCP");
                    sender.start();
                }
            }
        });
        chooseFileBtn = new JButton("choose file");
        chooseFileBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    showChooser();
                    if(file != null) {
                        sendBtn.setEnabled(true);
                        tcpButton.setEnabled(true);
                        udpButton.setEnabled(true);
                    }
                }
            }
        });
        Container content = getContentPane();
        content.setSize(800,400);
        content.add(new JLabel("Server running"));
        content.add(chooseFileBtn,BorderLayout.CENTER);
        content.add(sendBtn, BorderLayout.EAST);
        content.add(radioPanel, BorderLayout.LINE_START);
        pack();
        setVisible(true);
    }
    public static void main(String[] args) {
        System.out.println(args[0]);
        SenderGUI gui = new SenderGUI(Integer.parseInt(args[0]),Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

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
            this.file = chooser.getSelectedFile();
            System.out.println(file.getAbsolutePath());

        }
    }

}
