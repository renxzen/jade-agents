package finalproject.TF;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

public class G6_Contreras_Baptista extends FieldAgent {

    private Timer timer;
    private int aux_repro = 0;
    private FoodSource food_source_goal = null;

    @Override
    protected void init() {
        super.init();
        FieldUnity unidad_local = getLocal();
        if(timer == null) {
            timer = new Timer(500, evt -> {
                aux_repro += 1;
                if (aux_repro == 4) {
                    aux_repro = 0;
                    int cnt = 0;
                    for (FieldUnity ally : unidad_local.species.members.values()) {
                        if (ally.is_alive)
                            cnt++;
                    }
                    System.out.println("la cnt es " + cnt);
                    if (cnt <= 20)
                        status = STATES.REPRODUCING;
                }

                switch (status)
                {
                    case GOINGTOFIGHT:
                        if(getLocal().goal_enemy != null && !getLocal().goal_enemy.is_alive)
                            status = STATES.PATROLING;
                        break;
                    case GOINGTOSOURCE:
                        FoodSource source = food_source_goal;
                        if(source != null && source.getFood() <= 9) {
                            status = STATES.PATROLING;
                        }
                        break;
                    default:
                        break;
                }
            });
            timer.start();
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null) {
            sendMessageAllAllies(SOURCE, source);
        }
        FieldUnity enemy = detectEnemies();
        if (enemy != null) {
            sendMessageAllAllies(ENEMY, enemy);
        }
    }


    @Override
    protected void computeMessage(ACLMessage msg) {
        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < 500) {
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            }
            else if (msg.getContent().contains(SOURCE + "_")){
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);

                if (source != null && getLocal().position.distance(source.position) < 1000) {
                    getLocal().setGoal(source.position);
                    status = STATES.GOINGTOSOURCE;
                    //food_source_goal_id = m[1] + "_" + m[2];
                    food_source_goal = source;
                }
            }
        }
    }

}
