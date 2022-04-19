package finalproject.TF;

import static finalproject.agents.EntityAgent.STATES.EATING;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import javax.swing.Timer;

/**
 * @author G3: Francesco Bassino & Cesar Mosqueira
 */
public class JazzAgent extends FieldAgent {
    private Timer timer;

    @Override
    protected void init() {
        super.init();

        if (timer == null) {
            timer =
                    new Timer(
                            2000,
                            event -> {
                                int count = 0;
                                for (FieldUnity agent : getLocal().species.members.values()) {
                                    if (agent.is_alive) count++;
                                }
                                if (count <= 5 && getLocal().species.getStock() >= 50)
                                    status = STATES.REPRODUCING;
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
            status = STATES.REPRODUCING;
            sendMessageAllAllies(ENEMY, enemy);
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
                if (source != null) {
                    if (getLocal().position.distance(source.position) < 600)
                        getLocal().setGoal(source.position);
                    status = STATES.GOINGTOSOURCE;
                }
            }
        }
    }

    @Override
    public FieldUnity getLocal() {
        return FieldUnity.getLocal(getLocalName());
    }

    @Override
    protected void sendMessageSomeAllies(TYPE_MESSAGES type_message, Object o, int n) {
        ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
        switch (type_message) {
            case ENEMY:
                mensaje.setContent(
                        ENEMY
                                + "_"
                                + ((FieldUnity) o).getName()
                                + "_"
                                + getLocal().position.getX()
                                + "_"
                                + getLocal().position.getY());
                int i = 0;
                for (FieldUnity ally : getLocal().species.members.values()) {
                    if (!ally.equals(getLocal())) {
                        i += 1;
                        if (i < n) mensaje.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));
                        else break;
                    }
                }
                status = STATES.FIGHTING;
                break;
            case SOURCE:
                if (o instanceof FoodSource) {
                    i = 0;
                    mensaje.setContent(
                            SOURCE
                                    + "_"
                                    + ((FoodSource) o).getID()
                                    + "_"
                                    + getLocal().position.getX()
                                    + "_"
                                    + getLocal().position.getY());
                    for (FieldUnity ally : getLocal().species.members.values()) {
                        if (!ally.equals(getLocal())) {
                            i += 1;
                            if (i < n)
                                mensaje.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));
                            else break;
                        }
                    }
                    status = EATING;
                }
                break;
            default:
                break;
        }
        send(mensaje);
    }
}
