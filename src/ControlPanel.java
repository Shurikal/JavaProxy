import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JFrame{

    private Rob_Connection rob;

    private JLabel lastCMD;

    /**
     * Creates a new Control Panel
     * @param rob
     */
    public ControlPanel(Rob_Connection rob) {
        this.rob = rob;
        setLayout(new BorderLayout());
        this.add(center(), BorderLayout.CENTER);
        this.add(top(), BorderLayout.NORTH);
        this.pack();
        this.setVisible(true);
    }


    private JPanel center(){
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(4,1));

        JButton button = new JButton("Sende Start");
        button.addActionListener(e-> send(100));
        center.add(button);

        button = new JButton("Sende Fangbereit");
        button.addActionListener(e-> send(100));
        center.add(button);

        button = new JButton("Sende Wurfbereit");
        button.addActionListener(e-> send(100));
        center.add(button);

        button = new JButton("Sende Fahren");
        button.addActionListener(e-> send(100));
        center.add(button);

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

    private void send(int i){
        if(rob != null){
            rob.cmd.writeCmd(i);
        }
    }
}

