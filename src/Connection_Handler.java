package robControlPanel;

import java.util.ArrayList;

public class Connection_Handler implements Runnable
{

    GUI gui;

    private ArrayList<Rob_Connection> robs;

    public Connection_Handler(GUI gui) {
        robs = new ArrayList<>();
    this.gui = gui;
    }

    public void addRob(Rob_Connection rob){
        robs.add(rob);
    }

    public Rob_Connection getRob(int i){
        return robs.get(i);
    }

    @Override
    public void run() {
        while(true){

            for(Rob_Connection rob : robs){
                if(rob!= null){
                    while(rob.cmd.readCmd() == CmdInt.Type.Cmd){
                        int i = rob.cmd.getInt();
                        gui.addText(i + " <- " +rob.getName());

                        for(Rob_Connection rob1 : robs){
                            if(rob1 != rob){
                                rob1.cmd.writeCmd(i);
                            }
                        }
                    }
                }
            }

            try{
                Thread.sleep(5);
            }catch (Exception e){}
        }
    }
}
