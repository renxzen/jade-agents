package finalproject.agents;

import static finalproject.environment.FoodSource.ALL_FOOD_SOURCES;

import finalproject.classes.Species;
import finalproject.environment.FoodSource;
import finalproject.frame.MainFrame;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.PlatformController;

public class HostAgent extends Agent {
    // public static ArrayList<Species> species = new ArrayList<Species>();
    private int number_of_sources = 10;
    public static MainFrame frame = null;
    public static boolean ENABLED = false;
    public static boolean STARTED = false;
    public static PlatformController container;

    @Override
    public void setup() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            DFService.register(this, dfd);
            frame = new MainFrame(this);
            frame.setVisible(true);
            container = getContainerController();

            // Species s = new Species("Example");
            // s.start();

            // Agregar especies
            new Species("G1_Aguirre_Tiglla").start();
            new Species("G2_Gallegos_Chavez").start();
            new Species("G3_Bassino_Mosqueira").start();
            new Species("G4_Damian").start();
            new Species("G4_Mondragon").start();

            // new Species("G5_Costa_ShipAgent").start();
            // new Species("G5_Costa_Swarm").start();
            // new Species("G6_Contreras_Baptista").start();
            // new Species("G7_Huerta_Rivas").start();
            // new Species("G8_Contreras_E").start();

            // new Species("G9_Peralta_Chavez").start();

            //
            for (int i = 0; i < number_of_sources; i++) {
                FoodSource source = new FoodSource();
                ALL_FOOD_SOURCES.put(source.getID(), source);
            }
            ParallelBehaviour parallel = new ParallelBehaviour();
            parallel.addSubBehaviour(
                    new CyclicBehaviour(this) {
                        @Override
                        public void action() {
                            MainFrame.panel_principal.repaint();
                            MainFrame.panel_stats.repaint();
                        }
                    });
            parallel.addSubBehaviour(
                    new TickerBehaviour(this, 500) {
                        @Override
                        public void onTick() {
                            /*NCell c = NCell.getNormalCells().firstEntry().getValue();
                            for (Map.Entry<String, NCell> entry : NCell.getNormalCells().entrySet()) {
                                if (entry.getValue().status == INFECTED) {
                                    c = entry.getValue();
                                    break;
                                }
                            }
                            //NCell c = NCell.getNormalCells().firstEntry().getValue();
                            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                            msg.setContent("die");
                            msg.addReceiver(new AID(c.getName(), AID.ISLOCALNAME));
                            send(msg);
                            //System.out.println(c.getName());*/
                        }
                    });
            addBehaviour(parallel);
            ENABLED = true;
        } catch (Exception e) {
            System.err.println("exception " + e);
            e.printStackTrace();
        }
    }

    /*public static void fight(BaseAgent ag) {
        ArrayList<BaseAgent> allies = new ArrayList<BaseAgent>();
        ArrayList<BaseAgent> ennemis = new ArrayList<BaseAgent>();
        int forceAllies = 0;
        int forceEnnemis = 0;
        Species e = get_espece(ag);
        for(int i=0; i<especes.size(); i++) {
            Species test = especes.get(i);
            ArrayList<BaseAgent> agents = test.get_members();
            for(int j=0; j<agents.size(); j++) {
                BaseAgent agent = agents.get(j);
                if(agent.get_actuel()==ag.get_actuel()) {
                    int points = (int)((0.125*agent.get_points())+(0.25*Math.random()*agent.get_points()));
                    if(test==e) {
                        forceAllies += points;
                        allies.add(agent);
                    }
                    else {
                        forceEnnemis += points;
                        ennemis.add(agent);
                    }
                }
            }
        }
        if(ennemis.size()>0) {
            int degats = forceEnnemis/allies.size();
            for(BaseAgent allie:allies)
                allie.take_damages((int)(Math.random()*degats));
            degats = forceAllies/ennemis.size();
            for(BaseAgent ennemi:ennemis) ennemi.take_damages((int)(Math.random()*degats));
        }
    }*/

    public static boolean fill_stock(FieldAgent agent, int value) {
        if (agent.getLocal().species != null) {
            agent.getLocal().species.fill(value);
            return true;
        } else return false;
    }

    public static boolean transfer(FieldAgent agent, int value) {
        if (agent.getLocal().species != null) {
            // agent.getLocal().species.fill(value);
            agent.getLocal().fill(agent.getLocal().species.empty(value));
            return true;
        } else return false;
    }
}
