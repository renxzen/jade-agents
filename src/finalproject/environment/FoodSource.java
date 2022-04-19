package finalproject.environment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.Timer;

public class FoodSource {
    // public static ArrayList<FoodSource> ALL_FOOD_SOURCES = new ArrayList<FoodSource>();
    public static TreeMap<String, FoodSource> ALL_FOOD_SOURCES = new TreeMap<String, FoodSource>();

    public static void paintAllFoodSources(Graphics g) {
        for (FoodSource source : ALL_FOOD_SOURCES.values()) source.paint(g);
    }

    public static FoodSource getSourceFromPosition(Position position) {
        if (position != null) {
            for (FoodSource source : ALL_FOOD_SOURCES.values()) {
                // if (source.position.equals(position))
                if (source.position.isClose(position)) return source;
            }
        }
        return null;
    }

    private String id;
    public Position position;
    private Timer timer;
    private int food;

    public FoodSource() {
        id = "source_" + Functions.randomString();
        position = Functions.randomPosition();
        food = 5;
    }

    public int getFood() {
        return food;
    }

    public String getID() {
        return id;
    }

    public void start() {
        if (timer == null) {
            timer =
                    new Timer(
                            5000,
                            new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    food++;
                                }
                            });
            timer.start();
        }
    }

    @Override
    public String toString() {
        return position.toString() + "(" + food + ")";
    }

    // public int get_nourriture() { return food; }

    /*public void depose(Entity ag) {
        int points = ag.getPoints();
        food += points;
        ag.agent.doDelete();
    }*/

    public int eat() {
        int value;
        if (food > 5) {
            value = 10;
            food = food - 5;
        } else {
            value = food;
            food = 0;
        }
        return value;
    }

    public boolean isEmpty() {
        return food <= 0;
    }

    public void paint(Graphics g) {
        int x = position.getX();
        int y = position.getY();
        g.setColor(Color.BLACK);
        g.fillOval(x - 2, y - 2, 5, 5);
        g.drawOval(x - 12, y - 12, 25, 25);
        g.drawString("" + food, x + 15, y + 15);
        g.setColor(Color.WHITE);
    }
}
