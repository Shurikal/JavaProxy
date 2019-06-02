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

    private byte buttonA,buttonB,buttonX,buttonY, buttonRZ;

    private int transmit;

    private boolean buttonLT3;

    private float ryBuffer=8.0f, oldRY;

    private byte[] tx;

    /**
     * Creates a new Control Panel
     * @param rob
     */
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

    /**
     *
     */
    public void run() {
        List<Controller> gamepads = Arrays
                .stream(ControllerEnvironment.getDefaultEnvironment().getControllers()).filter(controller ->
                        controller.getType().equals(Controller.Type.GAMEPAD)).collect(Collectors.toList());
        Controller gamepad = gamepads.get(0);

        Component component;
        float value;

        Event event;
        while (true) {
            gamepad.poll();

            EventQueue eq = gamepad.getEventQueue();
            event = new Event();

            while (eq.getNextEvent(event)) {
                component = event.getComponent();
                value = event.getValue();

                if(component.getIdentifier() == Component.Identifier.Axis.X){
                    x = value;
                }else if(component.getIdentifier() == Component.Identifier.Axis.Y){
                    y = value;
                }else if(component.getIdentifier() == Component.Identifier.Button.A){
                    buttonA = (value ==1)? (byte)0x01:0x00;
                }else if(component.getIdentifier() == Component.Identifier.Button.B){
                    buttonB = (value ==1)? (byte)0x02:0x00;
                }else if(component.getIdentifier() == Component.Identifier.Button.X){
                    buttonX = (value ==1)? (byte)0x04:0x00;
                }else if(component.getIdentifier() == Component.Identifier.Button.Y){
                    buttonY = (value ==1)? (byte)0x08:0x00;
                }else if(component.getIdentifier() == Component.Identifier.Axis.RZ){
                    buttonRZ = (value ==1)? (byte)0x10:0x00;
                }else if(component.getIdentifier() == Component.Identifier.Axis.RY){
                    oldRY = -1*value;
                }else if(component.getIdentifier() == Component.Identifier.Button.LEFT_THUMB3){
                    buttonLT3 = (value==1);
                }else{
                    System.out.println(component.getIdentifier());
                }

            }

            if(oldRY>0.15 || oldRY<-0.15){
                ryBuffer += oldRY;
                ryBuffer = (ryBuffer >15)? 15: ryBuffer;
                ryBuffer = (ryBuffer <0)? 0: ryBuffer;
            }

            System.out.println(ryBuffer);

            double alpha = -Math.PI/4;


            float r,l;
            float range = 0.2f;
            if(y == -1 && x<range && x>-range && !buttonLT3 ){
                l =1;
                r = 1;
            }else if(y == 1 && x<range && x>-range && !buttonLT3){
                l = -1;
                r = -1;
            }else if(x == 1 && y<range && y>-range && !buttonLT3){
                l = 0.7f;
                r = -0.7f;
            }else if(x == -1 && y<range && y>-range && !buttonLT3){
                l = -0.7f;
                r = 0.7f;
            }else if(!buttonLT3){
                r = -1 * (float) (x * Math.cos(alpha) - y * Math.sin(alpha));
                l = -1 * (float) (x * Math.sin(alpha) + y * Math.cos(alpha));
            }else if(buttonLT3 && (x>0.2|| x<-0.2)){
                float z;
                z = (x>0)? -0.2f:0.2f;
                l = (x+z)/.8f;
                r = (-x-z)/.8f;
            }else{
                l =0;
                r = 0;
            }
            tx[3]=getByte(scale(r),scale(l));
            tx[2]= (byte)(buttonA|buttonB|buttonY|buttonX|buttonRZ);
            tx[1]=(byte)ryBuffer;

            transmit = ((int)tx[0]) << 24;
            transmit |= (((int)tx[1]) & 0xff) << 16;
            transmit |= (((int)tx[2]) & 0xff) << 8;
            transmit |= (((int)tx[3]) & 0xff);

            //System.out.println("X: "+ x + " Y: "+y);
            //System.out.println("A: "+buttonA+" B: "+buttonB+" Y: "+buttonY+" X: "+buttonX);
            //System.out.println("XY: "+ getByte(scale(r),scale(l)));
            //System.out.println(buttonRZ);

            if(!this.isVisible()){
                this.dispose();
                break;
            }
            if(rob!=null){
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
        if(f < 0.15 && f>-0.15){f =0;}
        return (byte)Math.round(f*7)+7;
    }

    private byte getByte(int i, int j){
        return (byte)((byte)(i<<4)+(byte)(j));
    }
}

