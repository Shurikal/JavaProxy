import java.awt.color.CMMException;
import java.net.ServerSocket;
import java.net.Socket;

public class RobEmulator
{

    private ServerSocket socket;

    private Socket client;

    private CmdInt cmd;

    private ByteFifo tx,rx;

    private boolean buttonA, buttonB,buttonX, buttonY;

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

                     System.out.println(transmit[2]);

                     int r = (i>>4 & 0x0F)-7;
                     int l = (i & 0x0F)-7;
                     System.out.println("R :"+r + " L :"+l);

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
