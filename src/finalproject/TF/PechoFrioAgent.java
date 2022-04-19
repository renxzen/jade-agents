/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package finalproject.TF;

import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Functions;

import jade.lang.acl.ACLMessage;

/**
 * @author G12: Arana & Tarazona
 */
public class PechoFrioAgent extends FieldAgent {
    //    private Timer timer;
    //
    //    @Override
    //    protected void init() {
    //        super.init();
    //        if(timer == null) {
    //            timer = new Timer(4000, new ActionListener() {
    //                @Override
    //                public void actionPerformed(ActionEvent evt) {
    //
    //                    status = STATES.REPRODUCING;
    //
    //                }
    //            });
    //            timer.start();
    //        }
    //    }

    @Override
    public FieldUnity getLocal() {
        return FieldUnity.getLocal(getLocalName());
    }

    @Override
    protected void reproduce() {
        if (getLocal().species.getStock() >= 60) {
            FieldUnity unity = new FieldUnity(getLocal().species);
            // AgentController ac =
            Functions.createAgent(unity.getName(), "TF." + getLocal().species.getFamily());
            // unity.setAgent(ac.);
            getLocal().species.members.put(unity.getID(), unity);
            getLocal().species.empty(10);
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null) sendMessageAllAllies(SOURCE, source);

        FieldUnity enemy = detectEnemies();
        if (enemy != null) {
            status = STATES.REPRODUCING;
            status = STATES.REINFORCING;
            sendMessageAllAllies(ENEMY, enemy);
            // sendMessageSomeAllies(ENEMY, enemy, 10);
        }
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
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
        }
    }
}
