package finalproject.frame;

import finalproject.environment.FoodSource;
import finalproject.agents.HostAgent;
import finalproject.classes.Species;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class EnvironmentPanel extends JPanel {
    public static int MAX_X = 800, MAX_Y = 700, BORDER_X = 50, BORDER_Y = 150;
    
    public EnvironmentPanel() {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(MAX_X, MAX_Y));
    }
      
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        g.setColor(Color.WHITE);
        Dimension d = getSize();
        MAX_X = d.width;
        MAX_Y = d.height;
        g.fillRect(0, 0, d.width, d.height);
        if(HostAgent.ENABLED) {
            /*Cell.paintAllCells(g);
            for (Map.Entry<String, Virus> entry : Virus.getActiveVirus().entrySet())
                entry.getValue().paint(g);
            Antibody.paintAllAntibodies(g);*/
            /*for (FoodSource source : sources) {
                g.setColor(Color.BLACK);
                g.fillOval(source.position.getX(), source.position.getY(), (int)(source.food), (int)(source.food));
                g.setColor(Color.WHITE);
                //System.out.println(source.position.getX()+" "+source.position.getY()+" "+source.food);
            }*/
            FoodSource.paintAllFoodSources(g);
            Species.paintAllSpecies(g);
        }
    }
}