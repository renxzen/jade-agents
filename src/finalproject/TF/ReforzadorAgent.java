package finalproject.TF;

import finalproject.agents.FieldAgent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ReforzadorAgent extends FieldAgent {
    private Timer timer;
    
    @Override
    protected void init() {
        super.init();
        if(timer == null) {
            timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) { 
                    status = STATES.REINFORCING; 
                }
            });
            timer.start();
        }
    }
    
    /*@Override
    protected void patrol() {
        super.patrol();        
    }*/
}