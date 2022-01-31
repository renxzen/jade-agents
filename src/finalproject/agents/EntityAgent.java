package finalproject.agents;

import static finalproject.agents.EntityAgent.STATES.PATROLING;
import finalproject.classes.Entity;
import finalproject.environment.FoodSource;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public abstract class EntityAgent extends Agent {
    public static enum STATES { PATROLING, REINFORCING, REPRODUCING, GOINGTOFIGHT, GOINGTOSOURCE, FIGHTING, EATING, ESCAPE, DEAD}
    protected ParallelBehaviour parallel;
    public STATES status;
    
    @Override
    protected void setup() {        
        try {
            status = PATROLING;
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            DFService.register(this, dfd);
            init();
            parallel = new ParallelBehaviour();
            //Comportamiento movimiento
            parallel.addSubBehaviour(new TickerBehaviour(this, 100){
		@Override
                protected void onTick() { 
                    if (status != PATROLING)  {
                        
                    }
                }
            });
            parallel.addSubBehaviour(new TickerBehaviour(this, 100){
		@Override
                protected void onTick() { computeStatus(); }
            });
            parallel.addSubBehaviour(new CyclicBehaviour(this) {
                @Override
                public void action() {
                    // listen if a greetings message arrives
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    if (msg != null)
                        computeMessage(msg);
                    else
                        block();
                }
            });
            addBehaviour(parallel);
        }
        catch (Exception e) {
            System.out.println("Saw exception in GuestAgent: " + e);
            e.printStackTrace();
        }
    }
    
    public void die() {
        try {
            DFService.deregister(this);
            doDelete();
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    protected abstract Entity getLocal();
    protected abstract void init();
    protected abstract void patrol();
    
    protected abstract void reproduce();
    protected abstract void fight();
    
    protected abstract void computeMessage(ACLMessage msg);
    
    private void computeStatus() {
        if (HostAgent.STARTED) {
            if (getLocal().getPoints() <= 0) {
                status = EntityAgent.STATES.DEAD;
                getLocal().die();
            }
            else {
            //REPRODUCING, FIGHTING, EATING, ESCAPE
            switch(status) {
                case PATROLING:  patrol(); break;
                case REINFORCING:  
                    HostAgent.transfer((FieldAgent) this, 2);
                    status = STATES.PATROLING;
                    break;
                case REPRODUCING:  
                    reproduce();
                    status = STATES.PATROLING;
                    break;
                case GOINGTOFIGHT: 
                    getLocal().goToGoal();
                    if(getLocal().hasArrived())
                        status = STATES.FIGHTING;
                    break;
                case GOINGTOSOURCE:
                    getLocal().goToGoal();
                    if(getLocal().hasArrived())
                        status = STATES.EATING;
                    break;
                case FIGHTING:
                    fight();
                    break;
                case EATING:
                    FoodSource source = FoodSource.getSourceFromPosition(getLocal().getGoal());
                    //System.out.println(getLocal().getName()+ " " + getLocal().position + " " + getLocal().getGoal() + " " + source);
                    if (source != null) { 
                        //getLocal().fill(source.eat()); 
                        HostAgent.fill_stock((FieldAgent) this, source.eat());
                        if (source.isEmpty())
                            status = PATROLING;
                    }
                    else
                        status = PATROLING;
                    break;
                case ESCAPE:
                    break;
                case DEAD:
                    break;
            }
            }
        }
    }
}