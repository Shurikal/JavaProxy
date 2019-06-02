import java.awt.color.CMMException;
import java.net.ServerSocket;
import java.net.Socket;

public class RobEmulator
{

    private ServerSocket socket;

    private Socket client;

    private CmdInt cmd;

    private ByteFifo tx,rx;

    private byte[] transmit;

    public RobEmulator() {

        tx = new ByteFifo(2047);
        rx = new ByteFifo(2047);
        cmd = new CmdInt(new SLIP(rx, tx));
        transmit = new byte[4];

        try {
            socket = new ServerSocket(5555);
        }catch(Exception e) { }

        while(true){

            try {
                System.out.println("Warte auf Rob");
                client = socket.accept();
                System.out.println("Rob verbunden");

            }catch (Exception e){}

            if(!client.isClosed()){
                new Thread(new Rob_Receiver(client,rx,cmd)).start();
                new Thread(new Rob_Sender(client,tx,cmd,true)).start();
            }

            while(!client.isClosed()){
                 if (cmd.readCmd() == CmdInt.Type.Cmd) {

                     int i = cmd.getInt();

                     transmit[0] = (byte) ((i >> 24) & 0xff);
                     transmit[1] = (byte) ((i >> 16) & 0xff);
                     transmit[2] = (byte) ((i >> 8) & 0xff);
                     transmit[3] = (byte) ((i >> 0) & 0xff);

                     int r = (transmit[3]>>4 & 0x0F)-7;
                     int l = (transmit[3] & 0x0F)-7;

                     boolean buttonA = ((transmit[2]&0x01) !=0);
                     boolean buttonB = ((transmit[2]&0x02) !=0);
                     boolean buttonX = ((transmit[2]&0x04) !=0);
                     boolean buttonY = ((transmit[2]&0x08) !=0);
                     boolean buttonRZ= ((transmit[2]&0x10) !=0);

                     int zylValue = transmit[1]&0x0F;

                     //System.out.println("A: "+buttonA+" B: "+buttonB+" Y: "+buttonY+" X: "+buttonX);
                     System.out.println("R :"+r + " L :"+l);
                     //System.out.println(zylValue);
                     //System.out.println(buttonRZ);

                     //System.out.println((int)(zylValue*(87.0/15.0)+8));
                 }
                try{
                    Thread.sleep(10);
                }catch(Exception e){ }
            }
            System.out.println("Connection closed");
        }
    }

    public static void main(String[] args) {
        new RobEmulator();
    }

    public void stop(){
        try{
            if (socket != null)
            socket.close();
        }catch(Exception e){}
    }
}
