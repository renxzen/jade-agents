package finalproject.classes;

import finalproject.environment.Functions;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class Species {
    public static TreeMap<String, Species> ALL_SPECIES = new TreeMap<String, Species>();
    public static void paintAllSpecies(Graphics g) {
        for(Map.Entry<String, Species> entry : ALL_SPECIES.entrySet())
            entry.getValue().paint(g);
    }
    
    public TreeMap<String, FieldUnity> members;
    private String family;
    public Color color;
    private int stock;
    
    public Species(String family) {
        members = new TreeMap<String, FieldUnity>();
        this.family = family;
        this.stock = 100;
        color =  new Color((int)(Math.random() * 0x1000000));
        ALL_SPECIES.put(family, this);
    }
    
    public void start() {
        for (int i = 0; i < 10; i++) {
            FieldUnity unity = new FieldUnity(this);
            Functions.createAgent(unity.getName(), "finalproject.TF." + family); 
            members.put(unity.getName(), unity);
        }
    }
    
    public int getStock() { return stock; }
    
    public void fill(int val) { stock += val; }
    
    public int empty(int val) {
        if(val > stock) {
            val = stock;
            stock = 0;
        }
        else 
            stock = stock - val;
        return val;
    }
    
    public String getFamily() { return family; }

    private void paint(Graphics g) {
        g.setColor(color);
        for(Map.Entry<String, FieldUnity> entry : members.entrySet())
            entry.getValue().paint(g);
    }
}