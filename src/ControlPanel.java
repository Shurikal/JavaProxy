import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControlPanel extends JFrame implements KeyListener, Runnable {

    private boolean vorwaerts,rueckwaerts,uz,guz,spanne90,spanne40,spanne08,werfe, loese_wand;
    private Rob_Connection rob;

    int VORWAERTS				= 4200;
    int RUECKWAERTS				= 4201;
    int DREHE_UZ				= 4202;
    int DREHE_GUZ				= 4203;

    int KURVEV_UZ				= 4204;
    int KURVEV_GUZ				= 4205;

    int KURVER_UZ				= 4206;
    int KURVER_GUZ				= 4207;

    int SPANNE90                = 4208;
    int SPANNE40                = 4209;
    int SPANNE08                = 4210;
    int WERFE                   = 4211;
    int LOESE_WAND              = 4212;


    public ControlPanel(Rob_Connection rob) {
        setPreferredSize(new Dimension(200, 200));
        this.rob = rob;

        addKeyListener(this);
        this.setFocusable(true);

        setLayout(null);

        this.pack();
        this.setVisible(true);

        new Thread(this).start();
    }


    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

        //System.out.println(e);
        switch (e.getKeyChar()) {
            case '1':
                spanne90 = true;
                break;
            case '2':
                spanne40 = true;
                break;
            case '3':
                spanne08 = true;
                break;
            case 'f':
                werfe = true;
                break;
            case 'a':
                guz = true;
                break;
            case 'd':
                uz = true;
                break;
            case 'r':
                loese_wand = true;
                break;
            case 'w':
                vorwaerts = true;
                break;
            case 's':
                rueckwaerts = true;
                break;
            default:


        }

    }


    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {

            case 'w':
                vorwaerts = false;
                break;
            case 's':
                rueckwaerts = false;
                break;
            case '1':
                spanne90 = false;
                break;
            case '2':
                spanne40 = false;
                break;
            case '3':
                spanne08 = false;
                break;
            case 'a':
                guz = false;
                break;
            case 'd':
                uz = false;
                break;
            case 'f':
                werfe = false;
                break;
            case 'r':
                loese_wand = false;
                break;
            default:
        }

    }

    public void run() {
        while (true) {
            //this.requestFocus();

            if(!this.isVisible()){
                this.dispose();
                break;
            }
            if(rob!=null){
                if(vorwaerts && !(rueckwaerts || guz || uz)){
                    rob.cmd.writeCmd(VORWAERTS);
                }else if(rueckwaerts && !(vorwaerts || guz || uz)){
                    rob.cmd.writeCmd(RUECKWAERTS);
                }else if(uz && !(vorwaerts || rueckwaerts || guz)){
                    rob.cmd.writeCmd(DREHE_UZ);
                }else if(guz && !(vorwaerts || rueckwaerts || uz)){
                    rob.cmd.writeCmd(DREHE_GUZ);
                }else if(vorwaerts && uz && !(rueckwaerts || guz)){
                    rob.cmd.writeCmd(KURVEV_UZ);
                }else if(vorwaerts && guz && !(rueckwaerts || uz)){
                    rob.cmd.writeCmd(KURVEV_GUZ);
                }else if(rueckwaerts && guz && !(vorwaerts || uz)){
                    rob.cmd.writeCmd(KURVER_GUZ);
                }else if(rueckwaerts && uz && !(vorwaerts || guz)) {
                    rob.cmd.writeCmd(KURVER_UZ);
                }else if(spanne90){
                    rob.cmd.writeCmd(SPANNE90);
                }else if(spanne40){
                    rob.cmd.writeCmd(SPANNE40);
                }else if(spanne08){
                    rob.cmd.writeCmd(SPANNE08);
                }else if(werfe){
                    rob.cmd.writeCmd(WERFE);
                }else if(loese_wand) {
                    rob.cmd.writeCmd(LOESE_WAND);
                }else{
                    rob.cmd.writeCmd(0);
                }

            }

            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }
}

