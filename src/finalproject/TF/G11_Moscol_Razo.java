package finalproject.TF;

import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.agents.HostAgent;
import finalproject.classes.FieldUnity;
import static finalproject.classes.FieldUnity.ALL_UNITIES;
import finalproject.environment.FoodSource;
import static finalproject.frame.MainFrame.panel_stats;
import jade.lang.acl.ACLMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.util.*;


public class G11_Moscol_Razo extends FieldAgent {
    private Timer timer;
    private int totalLista;
    private String state;

    private static List<FieldUnity> Allies = new ArrayList<FieldUnity>();
    private static List<FieldUnity> Enemies = new ArrayList<FieldUnity>();

    private static List<FoodSource> Food = new ArrayList<FoodSource>();
    
    @Override
    protected void patrol() {
        
        FieldUnity dAllies = detectAllies();
        FoodSource source = detectSources();
        FieldUnity enemy = detectEnemies();
        List<FieldUnity> myAllies = CheckListAllies();
        if(state=="search"){
            super.patrol();
        }
        
        FindFood();
        
        for(int i = 0; i < myAllies.size(); i++){
            if(myAllies.get(i).getPoints()<=16){
                status = STATES.REINFORCING; 
            }
            if(getLocal().species.getStock()>=50){
                if(myAllies.get(i).getPoints()<=18){
                    status = STATES.REINFORCING;
                }
            }
        }
        
        if (enemy != null) {
            if (getLocal().position.distance(enemy.position) < 500) {
                getLocal().setGoal(enemy.position);
                status = STATES.GOINGTOFIGHT;
                getLocal().goal_enemy = enemy;
            }
        }
    }
        private void Search(){
        state = "search";
    }
    
    protected FieldUnity detectAllies() {
        FieldUnity unidad_local = getLocal();
        FieldUnity ally = null;
        for(FieldUnity unity : ALL_UNITIES.values()) {
            if(unity.is_alive){
                if(unidad_local.species.equals(unity.species)) {
                    if(unidad_local.position.isClose(unity.position)) {
                        ally = unity;
                        break;
                    }
                }
            }
        }        
        return ally;
    }
    
    protected void FindFood(){
        FoodSource source = detectSources();
        if(source != null){
            boolean newSource = true;
            for (int i = 0; i < Food.size(); i++) {
                if (source.position.equals(Food.get(i).position)) {
                    newSource = false;
                }
            }
            if(newSource){
                status = STATES.GOINGTOSOURCE;
                Food.add(source);
                nearestSource();
            }
        }
    }
    
    private void nearestSource(){
        double distance = 9999;
        double betterFood = 0;
        int pos = 0;
        for (int i = 0; i < Food.size(); i++) {
            if(Food.get(i).getFood() >= 1){
                double newDistance = getLocal().position.distance(Food.get(i).position);
                double wayBetterFood = Food.get(i).getFood();
                double moreFood = wayBetterFood - betterFood;
                if(newDistance < distance){
                    distance = newDistance;
                    betterFood = wayBetterFood;
                    pos = i;
                }
            }
        }
        if(distance != 9999){
            getLocal().setGoal(Food.get(pos).position);
            status = STATES.GOINGTOSOURCE;
        }
    }
    
    protected List<FieldUnity> CheckListAllies(){
        FieldUnity dAllies = detectAllies();
        if(dAllies != null){
            boolean newAlly = true;
            for (int i = 0; i < Allies.size(); i++) {
                if (dAllies.position.equals(Allies.get(i).position)) {
                    newAlly = false;
                }
            }
            if(newAlly){
                Allies.add(dAllies);
            }
        }
        return Allies;
    }
    @Override
    protected void init() {
        super.init();
        
        Search();
    }
    @Override
    protected void computeMessage(ACLMessage msg){
         
    }
}