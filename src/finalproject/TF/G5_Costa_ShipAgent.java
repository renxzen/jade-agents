/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject.TF;


import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;
import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.MainFrame.panel_stats;
import finalproject.environment.Functions;

import java.util.*;
import finalproject.environment.Position;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Juan Costa
 */


public class G5_Costa_ShipAgent extends FieldAgent {
    // Explore
    // Return
    // Stay
    // Emergency
    private String currentState;
    
    
    private Position home = new Position(0,0);
    
    private static List<G5_Costa_ShipAgent> agents = new ArrayList<G5_Costa_ShipAgent>();
    private static List<FoodSource> foundFood = new ArrayList<FoodSource>();
   
    private float wanderForce = 0.25f;
    private double xDir;
    private double yDir;
    
    private double waitTime = 25;
    
    //private int currentIndex = 0;
    
    @Override
    protected void patrol(){
        //super.patrol();
        
        if (panel_stats.calcularTiempo() >= waitTime) {
            CheckFoodSource();

            if (currentState == "Explore") {
                ExploreMovement();
            } else if (currentState == "Return"){
                if (getLocal().hasArrived()) {
                    currentState = "Explore";
                }
                getLocal().goToGoal();
            } else if (currentState == "Stay"){
                FieldUnity enemy = detectEnemies();
                if (enemy != null){
                    status = STATES.FIGHTING;
                    getLocal().goal_enemy = enemy;
                }
                status = STATES.GOINGTOSOURCE;
            } 
        } else {
            CheekyMovement();
            FieldUnity enemy = detectEnemies();
            if (enemy != null){
                status = STATES.FIGHTING;
                getLocal().goal_enemy = enemy;
            } else {
                status = STATES.REPRODUCING;
            }
            //if (getLocal().getPoints() <= 100) {
            //        status = STATES.REINFORCING;
            //}
        }
        
        /*
        int deads = 0;
        for (G5_Costa_ShipAgent ally : agents) {
            if (ally.getLocal().getPoints() <= 0) {
                deads++;
            }
        }
        System.out.println(deads);
        if (deads>=9) {
            currentState = "Emergency";
        }
        */
    }
    
    private void ReturnHome(){
        currentState = "Return";
        getLocal().setGoal(home);
    }
    
    private int cheekyDir = 1;
    private void CheekyMovement(){
        if (getLocal().position.getX() <= BORDER_X || getLocal().position.getX() >= MAX_X) {
            cheekyDir = cheekyDir * -1;
        }
        
        if(getLocal().position.getY() < MAX_Y - 100){
            getLocal().position.addY(getLocal().speed);
            getLocal().position.addX(cheekyDir * getLocal().speed);
        }
    }
    
    private void ExploreMovement(){
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
            getLocal().position.getY() >= MAX_Y) 
        {
            RandomizeHome();
            ReturnHome();
        }
    }
    
    private void RandomizeHome(){
        home = Functions.randomPosition();
        if (home.getX() >= MAX_Y) {
            home.setX(MAX_Y-10);
        }
    }
    
    private void CheckFoodSource(){
        FoodSource source = detectSources();
        
        if (source != null && source.position.getY() <= MAX_Y) {
            
            //Check if source is already on the list
            boolean isNew = true;
            for (int i = 0; i < foundFood.size(); i++) {
                if (source.position.equals(foundFood.get(i).position)) {
                    isNew = false;
                }
            }
            
            if (isNew) {
                currentState = "Stay";
                foundFood.add(source);
                GoToCloseSource();
                home = source.position;
                //System.out.println(foundFood +"| COMIDA: "+source.position + " | " + panel_stats.calcularTiempo());
            }
        }
    }
    
    private void GoToCloseSource(){
        double dist = 9999;
        int foodIndex = 0;
        for (int i = 0; i < foundFood.size(); i++) {
            if(foundFood.get(i).getFood() >= 5){
                double newDist = getLocal().position.distance(foundFood.get(i).position);
            
                if(newDist < dist){
                    dist = newDist;
                    foodIndex = i;
                }
            }
        }
        
        if (dist != 9999) {
            getLocal().setGoal(foundFood.get(foodIndex).position);
            status = STATES.GOINGTOSOURCE;
        }
    }
        
    @Override
    protected void reproduce() {
        super.reproduce();
    }
    
    @Override
    protected void fight() {
        double p_l = (1.0 * getLocal().getPoints()) / (getLocal().getPoints() + getLocal().goal_enemy.getPoints());
        if (Math.random() < p_l) {
            getLocal().goal_enemy.take_damages(this, 1);
        } 
        else{
            status = STATES.PATROLING;
        }
    }
    
    @Override
    protected void init() {
        super.init();
        agents.add(this);
        
        
        RandomizeHome();
        ReturnHome();
    }  
    
    @Override
    protected void computeMessage(ACLMessage msg){
         
    }
}
