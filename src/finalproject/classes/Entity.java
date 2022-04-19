package finalproject.classes;

import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;

import finalproject.agents.EntityAgent;
import finalproject.agents.FieldAgent;
import finalproject.environment.Functions;
import finalproject.environment.Position;

import java.awt.Graphics;
import java.util.Random;

public abstract class Entity {
    // public EntityAgent agent;
    protected String entity_type;
    public Position position;
    protected int direction;
    private String id;
    private Position goal;
    protected int points;
    public int speed;
    public boolean is_alive;

    public Entity(int points) {
        position = Functions.randomPosition();
        direction = 1;
        id = Functions.randomString();
        goal = null;
        this.points = points;
        entity_type = "entity";
        speed = 10;
        is_alive = true;
    }

    public void die() {
        is_alive = false;
    }
    // public void fill(int value) { points += value; }

    public Position getGoal() {
        return goal;
    }

    public void setGoal(int x, int y) {
        setGoal(new Position(x, y));
    }

    public void setGoal(Position goal) {
        this.goal = goal;
        goToGoal();
    }

    public void goToGoal() {
        if (!hasArrived()) {
            double deltaX = Math.abs(goal.getX() - position.getX());
            int signX = 1;
            if (goal.getX() < position.getX()) signX = -1;
            double deltaY = Math.abs(goal.getY() - position.getY());
            int signY = 1;
            if (goal.getY() < position.getY()) signY = -1;
            if (deltaX > 0) {
                double angleMovement = Math.atan(deltaY / deltaX);
                double x_tmp = Math.ceil(speed * Math.cos(angleMovement));
                if (signX == -1) x_tmp = Math.floor(signX * speed * Math.cos(angleMovement));
                double y_tmp = Math.ceil(speed * Math.sin(angleMovement));
                if (signY == -1) y_tmp = Math.floor(signY * speed * Math.sin(angleMovement));
                position.add(x_tmp, y_tmp);
            } else position.addY(signY * speed);
        }
        if (position.getX() < BORDER_X) position.setX(BORDER_X);
        else if (position.getX() >= MAX_X - BORDER_X) position.setX(MAX_X - BORDER_X);
        if (position.getY() < BORDER_Y) position.setY(BORDER_Y);
        else if (position.getY() >= MAX_Y - BORDER_Y) position.setY(MAX_Y - BORDER_Y);
    }

    public boolean hasArrived() {
        return position.isClose(goal);
    }

    public String getID() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    // public void setAgent(EntityAgent agent) { this.agent = agent; }

    public abstract void paint(Graphics g);

    public void movementRandom() {
        Random aleatorio = new Random(System.currentTimeMillis());
        if (position.getX() <= BORDER_X) position.addX(speed);
        else if (position.getX() >= MAX_X - BORDER_X) position.addX(-speed);
        else if (aleatorio.nextInt(100) > 50) position.addX(speed);
        else position.addX(-speed);
        if (position.getY() <= BORDER_Y) position.addY(speed);
        else if (position.getY() >= MAX_Y - BORDER_Y) position.addY(-speed);
        else if (aleatorio.nextInt(100) > 50) position.addY(speed);
        else position.addY(-speed);
    }

    public void movementUpDown() {
        Random aleatorio = new Random(System.currentTimeMillis());
        if (position.getX() <= BORDER_X) position.addX(speed);
        else if (position.getX() >= MAX_X - BORDER_X) position.addX(-speed);
        else if (aleatorio.nextInt(100) > 50) position.addX(speed);
        else position.addX(-speed);
        if (position.getY() <= BORDER_Y) direction = 1;
        else if (position.getY() >= MAX_Y - BORDER_Y) direction = -1;
        position.addY(direction * speed);
    }

    public void take_damages(FieldAgent unity, int damages) {
        points = points - damages;
        // System.out.print ("classes.Entity.take_damages() " + points + " " + (points <= 0));
        if (points <= 0) {
            unity.status = EntityAgent.STATES.PATROLING;
            // die();
        }
        // System.out.println("");
    }

    public String getName() {
        return entity_type + "_" + id;
    }

    /*private void die() {
        System.out.println("classes.Entity.die() "+toString());
        //agent.die();
        //agent.status = EntityAgent.STATES.DEAD;
    }*/
}
