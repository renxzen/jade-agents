package finalproject.agents;

import static finalproject.agents.EntityAgent.STATES.EATING;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import static finalproject.classes.FieldUnity.ALL_UNITIES;
import static finalproject.environment.FoodSource.ALL_FOOD_SOURCES;

import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Functions;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class FieldAgent extends EntityAgent {
    @Override
    protected void patrol() {
        getLocal().movementRandom();
    }

    @Override
    protected void reproduce() {
        if (getLocal().species.getStock() >= 10) {
            FieldUnity unity = new FieldUnity(getLocal().species);
            // AgentController ac =
            Functions.createAgent(unity.getName(), "TF." + getLocal().species.getFamily());
            // unity.setAgent(ac.);
            getLocal().species.members.put(unity.getID(), unity);
            getLocal().species.empty(10);
        }
    }

    // @Override
    /*protected void reproduce(int n) {
        if (getLocal().species.getStock() >= n) {
            FieldUnity unity = new FieldUnity(getLocal().species);
            //AgentController ac =
            Functions.createAgent(unity.getName(), "TF." + getLocal().species.getFamily());
            //unity.setAgent(ac.);
            getLocal().species.members.put(unity.getID(), unity);
            getLocal().species.empty(n);
        }
    }*/

    @Override
    protected void fight() {
        if (getLocal().goal_enemy != null && status == STATES.FIGHTING) {
            double p_l =
                    (1.0 * getLocal().getPoints())
                            / (getLocal().getPoints() + getLocal().goal_enemy.getPoints());
            // double p_e = 1.0 * getLocal().goal_enemy.getPoints() / (getLocal().getPoints() +
            // getLocal().goal_enemy.getPoints());
            if (Math.random() < p_l) {
                getLocal().goal_enemy.take_damages(this, 1);
                /*if (getLocal().goal_enemy.getPoints() <= 0)
                getLocal().goal_enemy.agent.status = EntityAgent.STATES.DEAD;*/
            } else status = STATES.PATROLING;
            // else
            // getLocal().take_damages(1);
            // getLocal().goal_enemy.take_damages(1);
        }
    }

    @Override
    protected void init() {}

    public static enum TYPE_MESSAGES {
        ENEMY,
        SOURCE,
        OTHER
    }

    protected FoodSource detectSources() {
        FieldUnity unidad_local = getLocal();
        FoodSource source_found = null;
        for (FoodSource source : ALL_FOOD_SOURCES.values()) {
            if (unidad_local.position.isClose(source.position) && source.getFood() > 10) {
                source_found = source;
                break;
            }
        }
        if (source_found != null) {
            System.out.println(unidad_local.toString() + " " + source_found + " " + status);
            status = EATING;
        }
        return source_found;
    }

    protected FieldUnity detectEnemies() {
        FieldUnity unidad_local = getLocal();
        FieldUnity enemy = null;
        for (FieldUnity unity : ALL_UNITIES.values()) {
            if (unity.is_alive) {
                if (!unidad_local.species.equals(unity.species)) {
                    if (unidad_local.position.isClose(unity.position)) {
                        enemy = unity;
                        break;
                    }
                }
            }
        }
        return enemy;
    }

    protected void sendMessageAllAllies(TYPE_MESSAGES type_message, Object o) {
        ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
        FieldUnity unidad_local = getLocal();
        switch (type_message) {
            case ENEMY:
                mensaje.setContent(
                        ENEMY
                                + "_"
                                + ((FieldUnity) o).getID()
                                + "_"
                                + getLocal().position.getX()
                                + "_"
                                + getLocal().position.getY());
                for (FieldUnity ally : unidad_local.species.members.values()) {
                    if (!ally.equals(unidad_local))
                        mensaje.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));
                }
                break;
            case SOURCE:
                if (o instanceof FoodSource) {
                    mensaje.setContent(
                            SOURCE
                                    + "_"
                                    + ((FoodSource) o).getID()
                                    + "_"
                                    + getLocal().position.getX()
                                    + "_"
                                    + getLocal().position.getY());
                    for (FieldUnity ally : getLocal().species.members.values()) {
                        if (!ally.equals(unidad_local))
                            mensaje.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));
                    }
                    status = EATING;
                }
                break;
            default:
                break;
        }
        send(mensaje);
    }

    protected void sendMessageSomeAllies(TYPE_MESSAGES type_message, Object o, int n) {
        ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
        FieldUnity unidad_local = getLocal();
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
                for (FieldUnity ally : unidad_local.species.members.values()) {
                    if (!ally.equals(unidad_local)) {
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
                        if (!ally.equals(unidad_local)) {
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

    @Override
    protected void computeMessage(ACLMessage msg) {}

    @Override
    public FieldUnity getLocal() {
        return FieldUnity.getLocal(getLocalName());
    }
}
