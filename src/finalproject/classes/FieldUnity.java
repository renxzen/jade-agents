package finalproject.classes;

import java.awt.Graphics;
import java.util.TreeMap;

public class FieldUnity extends Entity {
    public static TreeMap<String, FieldUnity> ALL_UNITIES = new TreeMap<String, FieldUnity>();
    public static FieldUnity getLocal(String name) { 
        if (!ALL_UNITIES.containsKey(name))
            System.out.println(ALL_UNITIES.keySet().size() + " " + ALL_UNITIES.keySet());
        return ALL_UNITIES.get(name); 
    }
    
    public FieldUnity goal_enemy = null;
    public Species species;
    
    public FieldUnity(Species species) {
        super(10);
        this.species = species;        
        ALL_UNITIES.put(getName(), this);
    }
    
    
    
    @Override
    public void paint(Graphics g) {
        if (!is_alive)
            g.drawString("X", position.getX(), position.getY());
        else {
        int radius = 10 + points / 2 ;
            g.fillOval(position.getX(), position.getY(), radius, radius);        
            //g.fillOval(x - 2, y - 2, 5, 5);
            //g.drawOval(x - 12, y - 12, 25, 25);
            g.drawString("" + points, position.getX() + radius, position.getY() + radius);
        }
    }    

    public void fill(int value) { points += value; }
    
    /*@Override
    public void die() {
        
    }*/
    //protected void reinforce() { points += species.empty(5); }
}