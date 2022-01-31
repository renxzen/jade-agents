package finalproject.TF;

import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Functions;
import finalproject.environment.Position;
import jade.lang.acl.ACLMessage;
import jdk.net.SocketFlow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static finalproject.environment.FoodSource.ALL_FOOD_SOURCES;
import static java.lang.System.console;
import javax.swing.Timer;

public class G9_Peralta_Chavez extends FieldAgent {
    private Timer timerRein1;
    private Timer timerRein2;
    private Timer timerFood;
    private Timer timerRep;
    private int cantidad = 0;
    private Position position1;

    @Override
    protected void init() {
        super.init();
        if(timerRein1 == null) {
            timerRein1 = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (getLocal().getPoints() < 10) {
                        status = STATES.REINFORCING;
                    }
                }
            });
            timerRein1.start();
        }
        if(timerRein2 == null) {
            timerRein2 = new Timer(10000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if ((int) (Math.random() * (10 - 0)) > 5){
                        status = STATES.REINFORCING;
                    }
                }
            });
            timerRein2.start();
        }
        if(timerRep == null) {
            timerRep = new Timer(15000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    cantidad = 0;
                    for(FieldUnity ally : getLocal().species.members.values()) {
                        if(!ally.equals(getLocal()))
                            cantidad += 1;
                    }
                    if (cantidad < 6){
                        status = STATES.REPRODUCING;
                    }
                }
            });
            timerRep.start();
        }
        if(timerFood ==null){
            timerFood =  new Timer(20000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    System.out.println(position1);
                    if(position1!=null) {
                           if (getLocal().position.distance(position1) < 100 && ((int) (Math.random() * (10 - 0)) <= 2)
                                && (status != STATES.FIGHTING || status != STATES.GOINGTOFIGHT)) {
                            getLocal().setGoal(position1);
                            status = STATES.GOINGTOSOURCE;
                        }
                    }
                }
            });
            timerFood.start();
        }
        //if ((int) (Math.random() * (10 - 0)) > 7){
        //    status = STATES.REPRODUCING;
       // }
    }

    @Override
    protected void reproduce() {
        if (getLocal().species.getStock() > 60) {
            FieldUnity unity = new FieldUnity(getLocal().species);
            //AgentController ac =
            Functions.createAgent(unity.getName(), "TF." + getLocal().species.getFamily());
            //unity.setAgent(ac.);
            getLocal().species.members.put(unity.getID(), unity);
            if(position1==null) {
                unity.position = new Position(getLocal().position.getX() + 50, getLocal().position.getY() + 50);
            }else {
                unity.position = position1;
            }
            getLocal().species.empty(10);
        }
    }

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null){
            sendMessageAllAllies(SOURCE, source);
            status= STATES.PATROLING;
        }
        FieldUnity enemy = detectEnemies();
        if (enemy != null) {
            sendMessageAllAllies(ENEMY, enemy);
            //sendMessageSomeAllies(ENEMY, enemy, 10);
            getLocal().goal_enemy=enemy;
            status = STATES.FIGHTING;
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
                    if (getLocal().position.distance(enemy.position) < 500 ) {
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            }
            else if (msg.getContent().contains(SOURCE + "_")){
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                if (source != null) {
                    if (getLocal().position.distance(source.position) < 500) {
                        getLocal().setGoal(source.position);
                        if(position1==null) {
                            position1 = source.position;
                        }
                        status = STATES.GOINGTOSOURCE;
                    }
                    //getLocal().setGoal(source.position);
                    //status = STATES.GOINGTOSOURCE;
                }
            }
        }
    }
}
