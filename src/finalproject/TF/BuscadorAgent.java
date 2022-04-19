package finalproject.TF;

import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;

import jade.lang.acl.ACLMessage;

public class BuscadorAgent extends FieldAgent {
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
