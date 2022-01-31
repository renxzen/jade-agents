/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject.TF;

import finalproject.agents.FieldAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.classes.FieldUnity;
import finalproject.classes.Species;
import finalproject.environment.FoodSource;
import finalproject.environment.Position;
import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;
import static finalproject.frame.MainFrame.panel_stats;
import jade.lang.acl.ACLMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

public class G13_Porta_Morales_Tilin extends FieldAgent
{
    private Timer timer;
    double xdir=1.0;
    double ydir=1.0;

    //private Timer timer2;

    @Override
    protected void init() {
        //super.init();
        if(timer == null) {
            //System.out.println(getLocal().species.members.size());
            timer = new Timer(20000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) { 
                    status = STATES.REPRODUCING; 
                }
            });
            timer.start();
        }
        /*if(timer2 == null) 
        {
            timer2 = new Timer(40000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) { 
                    getLocal().setGoal((int)MAX_X/2,(int)MAX_Y/2);
                    getLocal().goToGoal();
                }
            });
            timer2.start();
        }*/
    }

    
    @Override
    protected void patrol()
    {
        //x azar y y al azar
        getLocal().position.addX((int)xdir*getLocal().speed);
        getLocal().position.addY((int)ydir*getLocal().speed);
        
        if (getLocal().position.getX() < BORDER_X || getLocal().position.getX() > MAX_X)
            xdir=xdir*-1;
        if (getLocal().position.getY() < BORDER_Y || getLocal().position.getY() > MAX_Y)
            ydir=ydir*-1;
        
        FoodSource source=detectSources();
        if(source != null)
            sendMessageAllAllies(SOURCE,source);
            //sendMessageSomeAllies(SOURCE,source,3);
        FieldUnity enemy= detectEnemies();
        if(enemy !=null)
            //sendMessageSomeAllies(ENEMY,enemy,3);
            sendMessageAllAllies(ENEMY,enemy);
    }
    @Override
    protected void computeMessage(ACLMessage msg) {
        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                //System.out.println(Species.ALL_SPECIES.get("G13_Porta_Morales_Tilin"));
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                if (enemy != null) {
                    if (getLocal().position.distance(enemy.position) < 300 ) 
                    {
                        //System.out.println(Species.ALL_SPECIES.get(enemy.species.getFamily()).getStock());
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                    else if  (getLocal().getPoints() < enemy.getPoints() &&getLocal().position.distance(enemy.position) < 20 )
                    {  
                        //getLocal().setGoal((int)MAX_X/2,(int)MAX_Y/2);
                        //getLocal().goToGoal();
                        //getLocal().fill(getLocal().species.empty(5));
                        status = STATES.REINFORCING;
                        
                    }
                }
            }
           
            else if (msg.getContent().contains(SOURCE + "_")){
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                //System.out.println("source:"+source.position);
                if(getLocal().position.distance(source.position)<250)
                {
                    getLocal().setGoal(source.position);
                    status = STATES.GOINGTOSOURCE;
                }
            }
        }
    }
}

