import javax.swing.*;
import java.awt.*;

public class Info extends JDialog
{

    public Info(JFrame fenster){
        super(fenster, "Info", ModalityType.DOCUMENT_MODAL);

        this.add(center(), BorderLayout.CENTER);
        //this.add(new JOptionPane());
        this.setPreferredSize(new Dimension(200,100));
        this.pack();
        this.setVisible(true);
    }

    private JPanel center(){
        JPanel center = new JPanel();

        JTextArea text = new JTextArea("V0.1\r\ncreated by Chris");
        text.setEditable(false);
        center.add(text);
        center.setPreferredSize(new Dimension(0,200));
        return center;
    }
}
