package finalproject.agents;

import java.util.ArrayList;

public class Simulation {
    private ArrayList<EntityAgent> aValider;
    private HostAgent hostAgent;
    
    public Simulation(HostAgent ag) {
        aValider = new ArrayList<EntityAgent>();
        hostAgent = ag;
        //dimension = 110;
        /*place = new FoodSource[dimension][dimension];
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                int source = (int)(Math.random()*1000);
                place[i][j] = new FoodSource(new Position(i, j), source==1);
            }
        }*/
    }
    
    /*public boolean isOccupied(Position l, EntityAgent ag) {
        Species e = get_espece(ag);
        for(int i=0; i < species.size(); i++) {
            Species test = species.get(i);
            if(test != e) {
                //ArrayList<Entity> agents = test.getMembers();
                for(int j=0; j < test.members.size(); j++) {
                    Position lieu = test.members.get(j).position;//get_actuel();
                    if(lieu==l) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/
    
    public HostAgent getHostAgent() { return hostAgent; }
    
    //public FoodSource[][] getPlaces() { return place; }
    
    //public ArrayList<Species> get_agents() { return especes; }
    
    /*public ArrayList<EntityAgent> get_agents(String famille) {
        for(int i=0; i<species.size(); i++) 
            if(species.get(i).getName().equals(famille)) 
                return (species.get(i));
        return null;
    }*/
    
    //public int get_dimension() { return dimension; }
    
    /*public int get_nombreAgents() {
        int sum = 0;
        for(int i=0; i<species.size(); i++) 
            sum += species.get(i).size();
        sum += aValider.size();
        return sum;
    }*/
    
    /*public Species get_espece(EntityAgent agent) {
        for(int i=0; i < species.size(); i++) {
            Species e = species.get(i);
            //ArrayList<Entity> agents = e.getMembers();
            for(int j=0; j<e.size(); j++) {
                EntityAgent ag = e.get(j);
                if(ag.getAID().equals(agent.getAID())) {
                    return e;
                }
            }
        }
        return null;
    }*/
    
    public void addAgent(EntityAgent ag) { aValider.add(ag); }
    
    /*public void valider(AID agent,String famille) {
        EntityAgent cible = null;
        for(int i=0; i<aValider.size(); i++) if(aValider.get(i).getAID().equals(agent)) cible = aValider.get(i);
        if(cible!=null) {
            ArrayList<EntityAgent> liste = null;
            for(int i=0; i<species.size(); i++) {
                if(species.get(i).getName().equals(famille)) 
                    liste = species.get(i);
            }
            if(liste==null) {
                Species e = new Species(famille);
                liste = e;//.getMembers();
                species.add(e);
            }
            liste.add(cible);
            aValider.remove(cible);
        }
    }*/
    
    /*public void cloner(EntityAgent agent, int points, String famille) {
        int pts;
        Species e = get_espece(agent);
        if(e != null && e.size() < 5) {
            //pts = e.creer(points);
            pts = e.empty(points);
            EntityAgent agNew = null;
            try { agNew = (EntityAgent)agent.getClass().newInstance(); }
            catch (InstantiationException | IllegalAccessException e2) { e2.printStackTrace(); }
            //agNew.give_simulation(this);
            agNew.setStart(agent.position);
            //agNew.setPoints(pts);
            addAgent(agNew);
            AgentController nouveau = null;
            while(nouveau==null) {
                String localName = famille + "_"+get_nombreAgents()+(int)(Math.random()*10);
                try { nouveau = getHostAgent().getContainerController().acceptNewAgent(localName, agNew); }
                catch (Exception e1) { e1.printStackTrace(); }
            }
            try { nouveau.start(); }
            catch (StaleProxyException e1) { e1.printStackTrace(System.err); }
        }
    }*/
    
    /*public int get_stock(EntityAgent agent) {
        if(get_espece(agent)!=null) return get_espece(agent).getStock();
        else return -1;
    }*/
    
    /*public boolean fill_stock(EntityAgent agent, int montant) {
        if(get_espece(agent)!=null) {
            get_espece(agent).fill(montant);
            return true;
        }
        else return false;
    }*/
    
    /*public int prelever(EntityAgent agent, int montant) {
        if(get_espece(agent)!=null) return get_espece(agent).empty(montant);
        else return -1;
    }*/
    
    public void start() {
        /*for(int i=0; i<dimension; i++) {
            for(int j=0; j<dimension; j++)
                place[i][j].start();
        }*/
        /*for (FoodSource source : sources)
            source.start();*/
    }
    
    /*public ArrayList<EntityAgent> get_voisins(EntityAgent agent) {
        ArrayList<EntityAgent> liste = new ArrayList<EntityAgent>();
        for(int i=0; i < species.size(); i++) {
            Species e = species.get(i);
            for(int j=0; j<e.size(); j++) {
                EntityAgent ag = e.get(j);
                //Position caseAg = ag.get_actuel();
                //Position perso = agent.get_actuel();
                if (ag.position.isClose(agent.position, 5))
                //if(proches(caseAg,perso)) 
                    liste.add(ag);
            }
        }
        return liste;
    }*/
    
    /*public ArrayList<FoodSource> get_casesVoisines(FoodSource actuel) {
        ArrayList<FoodSource> liste = new ArrayList<FoodSource>();
        for(int i=0; i<dimension; i++) {
            for(int j=0; j<dimension; j++) {
                FoodSource test = place[i][j];
                if (actuel.position.isClose(test.position, 5))
                //if(proches(actuel,test)) 
                    liste.add(test);
            }
        }
        return liste;
    }*/
    
    //public boolean proches(Lieu l1, Lieu l2) { return distance(l1,l2)<6; }
	
    /*public double distance(Lieu l1, Lieu l2) {
        int x = l1.get_x() - l2.get_x();
        x = x*x;
        int y = l1.get_y() - l2.get_y();
        y = y*y;
        double sum = 1.*(x+y);
        sum = Math.sqrt(sum);
        return sum;
    }*/
    
    /*public void fight(EntityAgent ag) {
        ArrayList<EntityAgent> allies = new ArrayList<EntityAgent>();
        ArrayList<EntityAgent> ennemis = new ArrayList<EntityAgent>();
        int forceAllies = 0;
        int forceEnnemis = 0;
        Species e = get_espece(ag);
        for(int i=0; i<species.size(); i++) {
            Species test = species.get(i);
            //ArrayList<Entity> agents = test.getMembers();
            for(int j=0; j<test.size(); j++) {
                EntityAgent agent = test.get(j);
                if(agent.position.isClose(ag.position, 5)) {
                    int points = (int)((0.125*agent.getPoints())+(0.25*Math.random()*agent.getPoints()));
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
            for(EntityAgent allie:allies) 
                allie.take_damages((int)(Math.random()*degats));
            degats = forceAllies/ennemis.size();
            for(EntityAgent ennemi:ennemis) ennemi.take_damages((int)(Math.random()*degats));
        }
    }*/
    
    /*public EntityAgent getAgent(AID aid) {
        for(int i=0; i<species.size(); i++) {
            ArrayList<EntityAgent> agents = species.get(i);//.getMembers();
            for(int j=0; j<agents.size(); j++) {
                EntityAgent agent = agents.get(j);
                if(agent.getAID().equals(aid)) 
                    return agent;
            }
        }
        return null;
    }*/
    
    /*public void transfererFond(BaseAgent source, BaseAgent cible, int montant) {
        Espece e1 = get_espece(source);
        Espece e2 = get_espece(cible);
        e2.remplir(e1.vider(montant));
    }*/
}