import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import net.java.games.input.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JFrame implements Runnable {

    private Rob_Connection rob;

    private float x,y;

    private byte buttonA,buttonB,buttonX,buttonY;

    private int transmit;

    private byte[] tx;


    public ControlPanel(Rob_Connection rob) {
        setPreferredSize(new Dimension(200, 200));
        this.rob = rob;
        tx = new byte[4];

        this.setFocusable(true);

        setLayout(null);

        this.pack();
        this.setVisible(true);

        new Thread(this).start();
    }

    public void run() {
        List<Controller> gamepads = Arrays
                .stream(ControllerEnvironment.getDefaultEnvironment().getControllers()).filter(controller ->
                        controller.getType().equals(Controller.Type.GAMEPAD)).collect(Collectors.toList());
        Controller gamepad = gamepads.get(0);
        long old = System.currentTimeMillis();
        Component component;
        float value;

        Event event;
        while (true) {
            //System.out.println(System.currentTimeMillis()-old);
            old = System.currentTimeMillis();
            //this.requestFocus();

            gamepad.poll();

            EventQueue eq = gamepad.getEventQueue();
            event = new Event();

            while (eq.getNextEvent(event)) {
                component = event.getComponent();
                value = event.getValue();

                if(component.getIdentifier() == Component.Identifier.Axis.X){
                    x = value;
                }
                if(component.getIdentifier() == Component.Identifier.Axis.Y){
                    y = value;
                }
                if(component.getIdentifier() == Component.Identifier.Button.A){
                    buttonA = (value ==1)? (byte)0x01:0x00;
                }
                if(component.getIdentifier() == Component.Identifier.Button.B){
                    buttonB = (value ==1)? (byte)0x02:0x00;
                }
                if(component.getIdentifier() == Component.Identifier.Button.X){
                    buttonX = (value ==1)? (byte)0x04:0x00;
                }
                if(component.getIdentifier() == Component.Identifier.Button.Y){
                    buttonY = (value ==1)? (byte)0x08:0x00;
                }
            }

            double alpha = -Math.PI/4;
            float r = -1*(float) (x*Math.cos(alpha)-y*Math.sin(alpha));
            float l = -1*(float) (x*Math.sin(alpha)+y*Math.cos(alpha));


            //System.out.println("A: "+buttonA+" B: "+buttonB+" Y: "+buttonY+" X: "+buttonX);
            System.out.println("XY: "+ getByte(scale(r),scale(l)));

            tx[3]=getByte(scale(r),scale(l));
            tx[2]= (byte)(buttonA|buttonB|buttonY|buttonX);

            transmit = ((int)tx[0]) << 24;
            transmit |= (((int)tx[1]) & 0xff) << 16;
            transmit |= (((int)tx[2]) & 0xff) << 8;
            transmit |= (((int)tx[3]) & 0xff);

            if(!this.isVisible()){
                this.dispose();
                break;
            }
            if(rob!=null){
                System.out.println("Test");
                rob.cmd.writeCmd(transmit);
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    private int scale(float f){
        if(f > 1){f = 1;}
        if(f <-1){f = -1;}
        return (byte)(f*7)+7;
    }

    private byte getByte(int i, int j){
        return (byte)((byte)(i<<4)+(byte)(j));
    }
}

