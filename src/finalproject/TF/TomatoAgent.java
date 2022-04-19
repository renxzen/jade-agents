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
 * @author G7: Huerta & Rivas
 */
public class TomatoAgent extends FieldAgent {
    private Timer timer;

    @Override
    protected void init() {
        super.init();
        // FieldUnity enemy = detectEnemies();
        // FoodSource source = detectSources();
        if (timer == null) {
            timer =
                    new Timer(
                            4000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {}
                            });
            timer.start();
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        int alive = 0;
        for (FieldUnity allie : getLocal().species.members.values()) {
            if (allie.is_alive) {
                alive = alive + 1;
            }
        }
        if (alive <= 3) {
            status = STATES.REPRODUCING;
        }

        FoodSource source = detectSources();
        if (source != null) {
            sendMessageAllAllies(SOURCE, source);
        }

        FieldUnity enemy = detectEnemies();
        if (enemy != null) {
            if (getLocal().species.getStock() > 50) {
                status = STATES.REINFORCING;
                sendMessageAllAllies(ENEMY, enemy);
            } else if (getLocal().species.getStock() <= 50) {
                sendMessageAllAllies(ENEMY, enemy);
            }
            /*if ( enemy.getPoints() > getLocal().getPoints() ){
                getLocal().setGoal( 10 , 10);
                status = STATES.ESCAPE;
            }*/
        }

        // sendMessageSomeAllies(ENEMY, enemy, 2);
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
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            }
        } else if (msg.getContent().contains(SOURCE + "_")) {

            System.out.println(msg.getContent());
            String[] m = msg.getContent().split("_");
            FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
            getLocal().setGoal(source.position);
            if (source != null) {
                if (getLocal().position.distance(source.position) < 300) {
                    getLocal().setGoal(source.position);
                    status = STATES.GOINGTOSOURCE;
                }
            }
        }
    }
}
