package finalproject.TF;
import finalproject.agents.EntityAgent;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.ENEMY;
import static finalproject.agents.FieldAgent.TYPE_MESSAGES.SOURCE;
import finalproject.classes.FieldUnity;
import finalproject.environment.FoodSource;
import jade.lang.acl.ACLMessage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;


import finalproject.agents.FieldAgent;

/**
 *
 * @author Administrator
 */
public class G8_Contreras_E extends FieldAgent {
  private Timer timer;
    int atacante=0;
             //Autor Coomment: Si encuentra una comida que automaticamente utilice el recurso recien creado para aumentar en +1,
            //                la poblacion,en son de mantener un equilibrio de natalidad/recursos con una curva con funcion positiva

    @Override
    protected void patrol() {
        super.patrol();
        FoodSource source = detectSources();
        if (source != null)
            sendMessageAllAllies(SOURCE, source);
            if (source != null){timer = new Timer(4000, (ActionEvent evt) -> {
                status = STATES.REPRODUCING;
            });
            timer.start();
            }
        FieldUnity enemy = detectEnemies();
        if (enemy != null)
            //Autor Coomment: Si encuentra un enemigo y el stock es mayor a 30 se hace un reinforcment,
            //                Esto con la idea de que si entra en combate pueda ganar usando el reinforcement como un escudo
            //                  *Seria una mejor idea en ves de usar un numero estatico (30) sacar el stock que el mod 
            //                  pone al iniciar el juego y para implementar alguna logica de tipo de threshold
            sendMessageAllAllies(ENEMY, enemy);
            if(getLocal().species.getStock() > 30 &&  enemy !=null){ timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) { 
                    status = EntityAgent.STATES.REINFORCING; 
                }
            });
            timer.start();
            }
            //sendMessageSomeAllies(ENEMY, enemy, 10);
    }
    //int atacante=0;
    @Override
    protected void computeMessage(ACLMessage msg) {

        //System.out.println(atacante);
        

        if (status == STATES.PATROLING) {
            if (msg.getContent().contains(ENEMY + "_")) {
                //System.out.println(msg.getContent());
                String[] m = msg.getContent().split("_");
                FieldUnity enemy = FieldUnity.ALL_UNITIES.get("entity_" + m[1]);
                            //Autor Coomment: Si hay un enemigo y la posicion es menor a 500, y el HP + HP de refuerzos venidero
            //                (sumatoria acumulatoria de los que andan  en State GoingtoFigtht x su [cantidad de puntos]
            //                  *se esta usando un numero estatico (10) pero seria una mejor idea tener el getPoits de cada agente y acumularlos
            //                  se usa el operador "o" para evitar que gracias ala fuerte sea menos frecuente la funcion de ataque
                if (enemy != null ) {                                      
                    if (getLocal().getPoints() > enemy.getPoints()*2){
                        //Como idea interesante, estaria genial aprovechar la funcion de los procesadores de dividirse (funcion de los hilos para cierto task)
                        //para que de esta manera puedan comer mas rapido pues dos bocas comen mas rapido que 1
                        //sin embargo solo existen funciones de reproduccion y repotencia, sin embargo ninguna que lo haga en sentido contrario
                        //posiblemente se pueda usar un factor como el (-1) e invertir la funcion pues tiene sentido matematicamente
                        
                        //cabe rezaltar que la funcion de actibvacion seria si esque el objetivo de cierto agente tiene mas del doble de puntos que el objetivo
                    }
                    if (getLocal().position.distance(enemy.position) < 500 && getLocal().getPoints()  >= enemy.getPoints() || atacante*10 >= enemy.getPoints()) {
                        //int atacante = 0;
                       // System.out.println(FieldUnity.ALL_UNITIES);
                        //System.out.ptintln(enemy.position);
                        //System.out.println(getLocal().getPoints());
                        //getLocal().getPoints()
                        System.out.println("Enemy: "+ enemy.getID()+" " +enemy.getPoints());
                        getLocal().setGoal(enemy.position);
                        status = STATES.GOINGTOFIGHT;
                        getLocal().goal_enemy = enemy;
                    }
                    else
                        //Autor Comment: Funcion Escape no implemtnada aun pero la idea es que se aleje
                        //del enemigo en caso enemigo sea null
                        getLocal().position.addX(-1);
                        getLocal().position.addY(-1);
                }
               
            }
            else if (msg.getContent().contains(SOURCE + "_")){
                String[] m = msg.getContent().split("_");
                FoodSource source = FoodSource.ALL_FOOD_SOURCES.get(m[1] + "_" + m[2]);
                getLocal().setGoal(source.position);
                status = STATES.GOINGTOSOURCE;
            }
        }
        if (status == STATES.GOINGTOFIGHT){atacante=atacante+1;System.out.println(atacante);}
    }
}