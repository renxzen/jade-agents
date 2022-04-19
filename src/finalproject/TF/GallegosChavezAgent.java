package finalproject.TF;

import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;

import jade.lang.acl.ACLMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * @author G2: Franco Gallegos & Bill Chavez
 */
public class GallegosChavezAgent extends FieldAgent {
    private Timer t_Ref;
    private Timer t_Rep;
    private boolean dc = false;
    private boolean dm = false;

    private int veces = 0;

    protected void IEat() {
        if (status == STATES.PATROLING) {
            FoodSource source = detectSources();
            if (source != null) {
                status = STATES.EATING;
            }
        }
    }

    protected void IFigth() {
        if (status == STATES.PATROLING) {
            FieldUnity enemy = detectEnemies();
            if (enemy != null) {
                status = STATES.FIGHTING;
            }
        }
    }

    protected void Reinforce() {
        if (t_Ref == null) {
            t_Ref =
                    new Timer(
                            2000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    status = STATES.REINFORCING;
                                }
                            });
        }
    }

    protected void Reproduce() {
        // int veces = 0;
        if (t_Rep == null) {
            t_Rep =
                    new Timer(
                            4000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    status = STATES.REPRODUCING;
                                }
                            });
        }
    }

    @Override
    protected void init() {
        /*if (status == STATES.PATROLING){
        dc = false;
                }*/
        Reproduce();
        if (veces % 20 == 0) {
            dm = true;
        } else {
            dm = false;
        }
        veces = veces + 1;
        if (dm == true) {
            t_Rep.start();
        } else {
            t_Rep.stop();
        }
        Reinforce();
        if (dc == true) {
            t_Ref.start();
        }
        // else{t_Rep.stop();}
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null) sendMessageAllAllies(SOURCE, source);
        FieldUnity enemy = detectEnemies();
        if (enemy != null) sendMessageAllAllies(ENEMY, enemy);
        // sendMessageSomeAllies(ENEMY, enemy, 10);
    }

    @Override
    protected void computeMessage(ACLMessage msg) {
        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < 500) {
                        // Timer timer;
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                        dc = true;

                    } else {
                        dc = false;
                    }
                }
            } else if (msg.getContent().contains(SOURCE + "_")) {
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
                // Reproduce();
            }
        }
    }
}
