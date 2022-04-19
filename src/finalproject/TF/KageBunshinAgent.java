package finalproject.TF;

import static finalproject.agents.EntityAgent.STATES.EATING;
import static finalproject.agents.EntityAgent.STATES.FIGHTING;
import static finalproject.agents.EntityAgent.STATES.GOINGTOFIGHT;
import static finalproject.agents.EntityAgent.STATES.GOINGTOSOURCE;
import static finalproject.agents.EntityAgent.STATES.PATROLING;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;

import finalproject.agents.FieldAgent;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Position;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.Timer;

/**
 * @author G4: Renzo Damián
 */
public class KageBunshinAgent extends FieldAgent {
    private Timer timerToChangeDirection;
    private Timer timerToReinforce;
    private Timer timerToReproduce;

    private class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    private static enum DIRECTION {
        RANDOM,
        LEADING
    };

    private DIRECTION currentDirection = DIRECTION.LEADING;

    private String leaderId = null;

    private Set<FoodSource> sources = new HashSet<FoodSource>();
    private Set<Tuple<Integer, Integer>> coordinates = new HashSet<Tuple<Integer, Integer>>();

    private Boolean healed = false;
    private Integer cloneTime = 1;

    @Override
    protected void init() {
        getLocal().setGoal(getLocal().position);

        if (timerToChangeDirection == null) {
            timerToChangeDirection =
                    new Timer(
                            2 * 1000,
                            evt -> {
                                if (currentDirection == DIRECTION.LEADING
                                        && getLocal().hasArrived()) {
                                    if (!coordinates.isEmpty()) {
                                        Tuple<Integer, Integer> next =
                                                coordinates.iterator().next();
                                        getLocal().setGoal(new Position(next.x, next.y));
                                        coordinates.remove(next);
                                    } else {
                                        Random randy = new Random(System.currentTimeMillis());
                                        Integer posX = randy.nextInt(MAX_X - BORDER_X);
                                        Integer postY = randy.nextInt(MAX_Y - BORDER_Y);
                                        getLocal().setGoal(new Position(posX, postY));

                                        System.out.println(
                                                "Random position to " + posX + " " + postY);
                                    }
                                }
                            });

            timerToChangeDirection.start();
        }

        if (timerToReproduce == null) {
            timerToReproduce =
                    new Timer(
                            cloneTime * 30 * 1000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    status = STATES.REPRODUCING;
                                    cloneTime++;
                                }
                            });

            timerToReproduce.start();
        }

        if (timerToReinforce == null) {
            timerToReinforce =
                    new Timer(
                            2 * 1000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    if (!healed && getLocal().getPoints() < 3) {
                                        status = STATES.REINFORCING;
                                        healed = true;
                                    }
                                }
                            });

            timerToReinforce.start();
        }
    }

    @Override
    protected void patrol() {
        if (currentDirection == DIRECTION.RANDOM) {
            getLocal().movementRandom();
        }

        if (currentDirection == DIRECTION.LEADING) {
            getLocal().goToGoal();
        }

        FoodSource source = detectSources();
        if (source != null) {
            sources.add(source);
            foodDetected(source);
        }

        FieldUnity enemy = detectEnemies();
        if (enemy != null) enemyDetected(enemy);
    }

    protected void foodDetected(FoodSource foodSource) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        FieldUnity localUnit = getLocal();

        message.setContent(
                SOURCE
                        + "_"
                        + foodSource.getID()
                        + "_"
                        + getLocal().position.getX()
                        + "_"
                        + getLocal().position.getY());

        for (FieldUnity ally : getLocal().species.members.values())
            if (!ally.equals(localUnit))
                message.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));

        status = EATING;
        send(message);
    }

    protected void enemyDetected(FieldUnity enemy) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        FieldUnity localUnit = getLocal();

        message.setContent(
                ENEMY
                        + "_"
                        + enemy.getID()
                        + "_"
                        + getLocal().position.getX()
                        + "_"
                        + getLocal().position.getY());

        for (FieldUnity ally : localUnit.species.members.values())
            if (!ally.equals(localUnit))
                message.addReceiver(new AID(ally.getName(), AID.ISLOCALNAME));

        status = FIGHTING;
        send(message);
    }

    protected void followLeader() {
        FieldUnity leader = getLocal().species.members.get(leaderId);
        if (leader != null) {
            Random randy = new Random(System.currentTimeMillis());
            Integer leaderX = leader.position.getX() + randy.nextInt(500);
            Integer leaderY = leader.position.getY() + randy.nextInt(500);
            Position leaderPosition = new Position(leaderX, leaderY);
            getLocal().setGoal(leaderPosition);
        }
    }

    protected void sendCoordinates() {
        if (sources.size() > 0) {
            for (FoodSource foodSource : sources) {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.setContent(
                        "COORDINATES_"
                                + foodSource.position.getX()
                                + "_"
                                + foodSource.position.getY());
                System.out.println(
                        "coordinates sent: "
                                + foodSource.position.getX()
                                + "_"
                                + foodSource.position.getY());
                send(message);
            }
        }
        // sources.clear();
    }

    @Override
    protected void computeMessage(ACLMessage msg) {
        String[] content = msg.getContent().split("_");

        if (status != PATROLING) return;

        if (content[0].equals(ENEMY.name())) {
            FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + content[1]);

            if (enemy == null) return;
            if (getLocal().position.distance(enemy.position) >= 500) return;

            getLocal().setGoal(enemy.position);
            getLocal().goal_enemy = enemy;

            status = GOINGTOFIGHT;
        }

        if (content[0].equals(SOURCE.name())) {
            FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(content[1] + "_" + content[2]);
            getLocal().setGoal(source.position);

            status = GOINGTOSOURCE;
        }

        if (content[0].equals("COORDINATES") && currentDirection == DIRECTION.LEADING) {
            System.out.println("Adding coordinate:" + content[2] + " " + content[3]);
            coordinates.add(
                    new Tuple<Integer, Integer>(
                            Integer.valueOf(content[2]), Integer.valueOf(content[3])));
        }
    }
}

// final class Tuple<X, Y> {
//     public final X x;
//     public final Y y;

//     public Tuple(X x, Y y) {
//         this.x = x;
//         this.y = y;
//     }
// }
