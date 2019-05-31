import java.io.DataOutputStream;
import java.net.Socket;

public class Rob_Sender implements Runnable {
    private DataOutputStream out;
    private Socket socket;

    private static final byte OPEN = (byte)0xd5;
    private static final byte CLOSE = (byte)0xd4;
    private static final byte HEARTBEAT = (byte)0xd3;
    private static final byte CMD = '$';
    private static final byte ESCAPE = (byte)0xf1;
    private static final byte ESCAPED_ESCAPE = (byte)0xf3;
    private static final byte ESCAPED_OPEN = (byte)0xf2;
    private static final byte ESCAPED_CLOSE = (byte)0xf5;
    private static final byte ESCAPED_HEARTBEAT = (byte)0xe6;
    private static final byte ESCAPED_CMD = (byte)0xf0;

    private boolean isServer;

    private ByteFifo tx;

    private CmdInt cmd;

    public Rob_Sender(Socket socket, ByteFifo tx, CmdInt cmd) {
        this.socket = socket;
        this.tx = tx;
        this.cmd = cmd;
    }

    public Rob_Sender(Socket socket,ByteFifo tx, CmdInt cmd,Boolean isServer) {
        this.socket = socket;
        this.tx = tx;
        this.cmd = cmd;
        this.isServer = isServer;
    }

    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Output Stream erstellt");
        } catch (Exception e) {
            System.out.println("Could not create Input");
        }
        long lastHeartBeat = System.currentTimeMillis();
        while (socket !=null && !socket.isClosed()) {

            if(System.currentTimeMillis() > lastHeartBeat + 999) {
                sendHeartbeat();
                lastHeartBeat = System.currentTimeMillis();
                if(isServer){
                    cmd.writeCmd(22);
                }
            }

            writeToOutput();

            long oldSysTime = System.currentTimeMillis();
            while (System.currentTimeMillis() < oldSysTime + 20) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.out.println("Could not sleep...");
                }
            }
        }
        System.out.println("Socket closed -> Output Thread terminated");
    }

    private void sendHeartbeat(){
        try {
            out.write(HEARTBEAT);
        } catch (Exception e){}
    }

    public synchronized void sendByte(Byte b){
        tx.enqueue(b);
    }

    private synchronized void writeToOutput(){
        if (tx.availToRead() > 0) {
            while (tx.availToRead() > 0) {
                byte b = 0;
                try {
                    b = (byte)tx.dequeue();
                }
                catch (Exception e) {
                    break;
                }
                try{
                if (b == OPEN) {
                    out.write(ESCAPE);
                    out.write(ESCAPED_OPEN);
                }
                else if (b == CLOSE) {
                    out.write(ESCAPE);
                    out.write(ESCAPED_CLOSE);
                }
                else if (b == HEARTBEAT) {
                    out.write(ESCAPE);
                    out.write(ESCAPED_HEARTBEAT);
                }
                else if (b == CMD) {
                    out.write(ESCAPE);
                    out.write(ESCAPED_CMD);
                }
                else if (b == ESCAPE) {
                    out.write(ESCAPE);
                    out.write(ESCAPED_ESCAPE);
                }
                else {
                    out.write(b);
                }
                }catch (Exception e){}

            }
        }
    }
}
