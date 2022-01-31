/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject.TF;

import static finalproject.TF.G5_Costa_Swarm.swarmObjective;
import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import finalproject.environment.Position;
import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;
import jade.lang.acl.ACLMessage;
import static finalproject.frame.MainFrame.panel_stats;
import java.util.Random;
import java.util.*;

/**
 *
 * @author Juan
 */

public class G5_Costa_Swarm extends FieldAgent
{
    public static Position swarmObjective;
    private static List<G5_Costa_Swarm> agents = new ArrayList<G5_Costa_Swarm>();
    public int maxAgents = 50;
    
    public int population = 0;
   
    public double reproduceTimer = 11;
    public static double currentRepTime = 0;
    
    public double newObjectiveTimer = 10;
    public static double currentObjTime = 0;
    
    public boolean arrivedToObjective = false;
    public double maxDistance = 250;
    
    public double wanderForce = 0.2;
    public double xDir = 0;
    public double yDir = 0;
    
    public boolean scout = false;
    public double lateGame = 60;
    
    public int bugYPos = 500;
    public Position scoutPosition = new Position(0,0);
    
    public int GenerateInt(int min,int max){
        Random r = new Random();
        return r.nextInt(max-min) + min;
    }
    
    public Position NextObjective(Position pos){
        pos = new Position(GenerateInt(BORDER_X,MAX_X-BORDER_X),GenerateInt(200,500));
        if (CheckForBug(pos)) {
            pos.setY(300);
        }
        return pos;
    }
    
    public boolean CheckForBug(Position pos){
        return pos.getY() > bugYPos;
    }
    
    private void ExplorePerimeter(Position posReference, double maxDist){
        xDir = (xDir + (-1 + Math.random() * (1-(-1))) * wanderForce);
        yDir = (yDir + (-1 + Math.random() * (1-(-1))) * wanderForce);

        xDir = xDir / (Math.sqrt(Math.pow(xDir, 2) + Math.pow(yDir, 2)));
        yDir = yDir / (Math.sqrt(Math.pow(xDir, 2) + Math.pow(yDir, 2)));
        //Velocity
        
        getLocal().position.add(xDir*getLocal().speed, yDir*getLocal().speed);
        
        //Check if out of bounds
        if (getLocal().position.getX() <= BORDER_X ||
            getLocal().position.getY() <= BORDER_Y ||
            getLocal().position.getX() >= MAX_X ||
            getLocal().position.getY() >= MAX_Y ||
            getLocal().position.distance(posReference) >= maxDist) 
        {
            arrivedToObjective = false;
            getLocal().setGoal(posReference);
            getLocal().goToGoal();
        }
    }
    
    private void SwarmMovement(){
        if (panel_stats.calcularTiempo() - currentRepTime >= reproduceTimer && agents.size() < 20) {
            currentRepTime = panel_stats.calcularTiempo();
            status = STATES.REPRODUCING;
        } else if(panel_stats.calcularTiempo() - currentObjTime >= newObjectiveTimer){
            currentObjTime = panel_stats.calcularTiempo();
            swarmObjective = NextObjective(swarmObjective);
            getLocal().setGoal(swarmObjective);
        } else {
            FoodSource source = detectSources();
            FieldUnity enemy = detectEnemies();
            
            if (enemy != null && enemy.getPoints() > 0 && !CheckForBug(enemy.position)){
                sendMessageAllAllies(ENEMY, enemy);
            }
            else if (source != null && !CheckForBug(source.position)) {
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
            else {
                if (arrivedToObjective) {
                    ExplorePerimeter(swarmObjective,maxDistance);
                } else {
                    if (getLocal().position.isClose(swarmObjective)) {
                        arrivedToObjective = true;
                    }
                    getLocal().setGoal(swarmObjective);
                    getLocal().goToGoal();
                }
            }
        }
    }
    
    private void ScoutMovement(){
        if (panel_stats.calcularTiempo() - currentRepTime >= reproduceTimer && agents.size() < maxAgents) {
            currentRepTime = panel_stats.calcularTiempo();
            status = STATES.REPRODUCING;
        } else if(panel_stats.calcularTiempo() - currentObjTime >= newObjectiveTimer){
            currentObjTime = panel_stats.calcularTiempo();
            scoutPosition = NextObjective(scoutPosition);
            getLocal().setGoal(scoutPosition);
        } else {
            FoodSource source = detectSources();
            FieldUnity enemy = detectEnemies();
            
            if (enemy != null && enemy.getPoints() > 0 && !CheckForBug(enemy.position)){
                if (panel_stats.calcularTiempo() >= lateGame) {
                    getLocal().setGoal(enemy.position);
                    status = STATES.GOINGTOFIGHT;
                    getLocal().goal_enemy = enemy;
                } else {
                    sendMessageAllAllies(ENEMY, enemy);
                }
            }
            else if (source != null && !CheckForBug(source.position)){
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
            else {
                if (arrivedToObjective) {
                    scoutPosition = NextObjective(scoutPosition);
                    arrivedToObjective = false;
                } else {
                    if (getLocal().position.isClose(scoutPosition)) {
                        arrivedToObjective = true;
                    } else {
                        getLocal().setGoal(scoutPosition);
                        getLocal().goToGoal();
                    }
                }
            }
        }
    }
    
    @Override
    protected void patrol() {
        //super.patrol();
        if (panel_stats.calcularTiempo() >= 60) {
            scout = true;
            newObjectiveTimer = 5;
            reproduceTimer = 6;
        }
        
        if (scout) {
            ScoutMovement();
        } else {
            SwarmMovement();
        }
    }

    @Override
    protected void computeMessage(ACLMessage msg) {
        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                //System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < bugYPos) {
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                }
            }
            else if (msg.getContent().contains(SOURCE + "_")){
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        if (swarmObjective == null) {
            swarmObjective = new Position(400,275);
        }
        agents.add(this);
        if (currentRepTime == 0) {
            currentRepTime = panel_stats.calcularTiempo();
        }
        if (currentObjTime == 0) {
            currentObjTime = panel_stats.calcularTiempo();
        }
        
        if (panel_stats.calcularTiempo() >= 5) {
            scout = false;
        } else {
            scoutPosition = NextObjective(scoutPosition);
        }
        
        status = STATES.REPRODUCING;
        getLocal().setGoal(swarmObjective);
    }  
}