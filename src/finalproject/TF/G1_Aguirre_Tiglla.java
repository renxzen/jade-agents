/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.TF;

import static finalproject.agents.EntityAgent.STATES.EATING;
import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import static finalproject.environment.FoodSource.ALL_FOOD_SOURCES;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Usuario
 */
public class G1_Aguirre_Tiglla extends FieldAgent {

    private Timer timer;
    int val = 10;

    @Override
    protected void init() {
        super.init(); // To change body of generated methods, choose Tools | Templates.

        if (timer == null) {
            timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (val != 0) {
                        if (getLocal().species.getStock() > 10) {
                            status = STATES.REINFORCING;
                            val = val - 1;
                        }
                    }
                }
            });
            timer.start();
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null)
            sendMessageAllAllies(SOURCE, source);
        // sendMessageSomeAllies(SOURCE, source, 2);

        FieldUnity enemy = detectEnemies();
        if (enemy != null)
            sendMessageAllAllies(ENEMY, enemy);
        // sendMessageSomeAllies(ENEMY, enemy, 200);
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
            } else if (msg.getContent().contains(SOURCE + "_")) {

                System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                getLocal().setGoal(source.position);
                if (source != null) {
                    if (getLocal().position.distance(source.position) < 500) {
                        getLocal().setGoal(source.position);
                        status = STATES.GOINGTOSOURCE;
                    }
                }
            }
        }
    }
}
