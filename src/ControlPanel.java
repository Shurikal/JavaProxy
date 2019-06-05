import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ControlPanel extends JFrame implements Runnable {

    private Rob_Connection rob;

    private JLabel lastCMD;

    private int transmit;

    /**
     * Creates a new Control Panel
     * @param rob
     */
    public ControlPanel(Rob_Connection rob) {
        //setPreferredSize(new Dimension(200, 200));
        this.rob = rob;

        setLayout(new BorderLayout());


        this.add(center(), BorderLayout.CENTER);
        this.add(top(), BorderLayout.NORTH);
        this.pack();
        this.setVisible(true);

        new Thread(this).start();
    }

    /**
     *  } catch (Exception e){
            System.out.println(e);
            gamepad =null;
        }
     */
    public void run() {

        while (true) {

            if(!this.isVisible()){
                this.dispose();
                break;
            }
            if(rob!=null){
                rob.cmd.writeCmd(transmit);
            }

            try {
                Thread.sleep(20);
            } catch (Exception e) {
            }
        }
    }

    private JPanel center(){
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(3,1));


        return center;
    }

    private JPanel top(){
        JPanel top = new JPanel();
        lastCMD = new JLabel(decrypt(-1));
        top.add(lastCMD);

        return top;
    }

    private String decrypt(int i){
        String s;

        switch(i){
            case 1:
                s = "Wurfbereit";
                break;
            case 2:
                s = "Fangbereit";
                break;
            default:
                s = "Unbekannter Zustand";
        }
        return "Letzter Zustand : "+ s;
    }
}

