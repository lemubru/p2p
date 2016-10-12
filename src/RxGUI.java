import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;


public class RxGUI extends JPanel {

    JProgressBar pbar;
    private Receiver rxThread;
    static final int MAXIMUM = 100;
    static final int MINIMUM = 0;
    private int progress = 0 ;
    private JLabel throughput = new JLabel("Average throughput");
    private JLabel FileLabel = new JLabel("File:");
    private JLabel metric = new JLabel("MB/s");
    private JButton pauseBtn;
    private JButton resumeBtn;
    private JTextField tp = new JTextField();
    private JTextField file = new JTextField();
    private boolean pauseDL = false;


    public RxGUI() {
        // initialize Progress Bar
        pbar = new JProgressBar();
        pbar.setMinimum(MINIMUM);
        pbar.setMaximum(MAXIMUM);
        // add to JPanel

        resumeBtn = new JButton("resume");
        resumeBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                //rxThread.setPauseDL(false);
                rxThread.informSender("resume");
            }
        });
        pauseBtn = new JButton("pause");
        pauseBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                //rxThread.setPauseDL(true);

                // rxThread.informSender("pause");


            }
        });


        add(resumeBtn);
        add(pauseBtn);
        add(pbar, BorderLayout.WEST);
        add(FileLabel);
        file.setPreferredSize(new Dimension(100,20));
        add(file);
        setPreferredSize(new Dimension(700,50));

        add(throughput);
        tp.setText("");
        tp.setPreferredSize(new Dimension(50,20));
        add(tp, BorderLayout.SOUTH);
        add(metric);
    }

    public void setPauseDL(boolean pauseDL) {
        this.pauseDL = pauseDL;
    }

    public void startRxThread() {
        rxThread = new Receiver("localhost", 5654);
        rxThread.start();
    }

    public JTextField getFile() {
        return file;
    }
    public void updateBar(int newValue) {
        pbar.setValue(newValue);
    }
    public JTextField getTp() {
        return tp;
    }

    public Thread getRxThread() {
        return rxThread;
    }
    public Receiver getRxObject() {
        return rxThread;
    }
    public static double rounding(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String args[]) {
        final RxGUI it = new RxGUI();
        JFrame frame = new JFrame("File download progress");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(it);
        frame.pack();
        frame.setVisible(true);
        it.startRxThread();
        int rxp = it.getRxObject().getRxPackets();
        while(it.getRxThread().isAlive()) {
            if(rxp != it.getRxObject().getRxPackets()) {
                //if received packets changes get total packets and calculate the percentage completed
                int TP = it.getRxObject().getTotalPackets();
                if(TP != 0) {
                    float perc = (float) rxp / (float) TP;
                    int percentage = (int)Math.round(perc*100);
                    //System.out.println(percentage + "percentage");
                    it.updateBar(percentage);
                }
                it.getFile().setText(it.getRxObject().getGetFileName());
                //  it.getTp().setText(""+ rounding( (((it.getRxObject().getPs()*rxp)/it.getRxObject().getTimerUDP().elapsedTime()) / 1000000), 2 ));
                rxp = it.getRxObject().getRxPackets();
            }
        }
    }
}