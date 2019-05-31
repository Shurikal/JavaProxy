import java.awt.color.CMMException;
import java.net.ServerSocket;
import java.net.Socket;

public class RobEmulator
{

    private ServerSocket socket;

    private Socket client;

    private CmdInt cmd;

    private ByteFifo tx,rx;

    public RobEmulator() {

        tx = new ByteFifo(2047);
        rx = new ByteFifo(2047);
        cmd = new CmdInt(new SLIP(rx, tx));

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
                    System.out.println(cmd.getInt());
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
