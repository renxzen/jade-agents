package finalproject.environment;

@SuppressWarnings("EqualsAndHashcode")
public class Position {
    private int x;
    private int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    
    public int getY() { return y; }
    
    public void setX(int x) { this.x = x; }
    
    public void setY(int y) { this.y = y; }
    
    public void addX(int dx) { x += dx; }
    
    public void addY(int dy) { y += dy; }
    
    public void add(double dx, double dy) { 
        x += dx;
        y += dy;
    }
    
    public double distance(Position position) { return Math.sqrt((this.x - position.getX()) * (this.x - position.getX()) + (this.y - position.getY()) * (this.y - position.getY())); }
    
    public boolean isClose(Position position, int max_distance) { 
        double distance = this.distance(position);
        return distance < max_distance / 3;
    }
    
    public boolean isClose(Position position) { return isClose(position, 50); }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            return (((Position)o).getX() == x) && (((Position)o).getY() == y);
        }
        else
            return false;
    }
    
    @Override
    public String toString() { return "("+ x + "," + y + ")"; }
}