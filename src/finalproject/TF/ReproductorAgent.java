package finalproject.TF;

import finalproject.agents.FieldAgent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class ReproductorAgent extends FieldAgent {
    private Timer timer;

    @Override
    protected void init() {
        super.init();
        if (timer == null) {
            timer =
                    new Timer(
                            4000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    status = STATES.REPRODUCING;
                                }
                            });
            timer.start();
        }
    }
}
