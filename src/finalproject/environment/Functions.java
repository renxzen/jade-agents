package finalproject.environment;

import static finalproject.agents.HostAgent.container;
import static finalproject.frame.EnvironmentPanel.BORDER_X;
import static finalproject.frame.EnvironmentPanel.BORDER_Y;
import static finalproject.frame.EnvironmentPanel.MAX_X;
import static finalproject.frame.EnvironmentPanel.MAX_Y;
import jade.wrapper.AgentController;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Random;

public class Functions {
    
    private static String randomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
    
    public static String randomString() { return randomString(10); }
    
    public static AgentController createAgent(String localname, String type) {
        try {
            AgentController ac = container.createNewAgent(localname, type, null);
            ac.start();
            return ac;
        } catch(Exception e) {
            System.err.println("exception " + e);
            e.printStackTrace();
            return null;
        }
    }
    
    public static Shape shapeStar(double centerX, double centerY,
        double innerRadius, double outerRadius, int numRays,
        double startAngleRad)
    {
        Path2D path = new Path2D.Double();
        double deltaAngleRad = Math.PI / numRays;
        for (int i = 0; i < numRays * 2; i++)
        {
            double angleRad = startAngleRad + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0) {
                relX *= outerRadius;
                relY *= outerRadius;
            } else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0)
                path.moveTo(centerX + relX, centerY + relY);
            else
                path.lineTo(centerX + relX, centerY + relY);
        }
        path.closePath();
        return path;
    }
    
    /*public static void agent_exit_actionPerformed(ActionEvent e, AgenteAnfitrion a) {
        a.addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() { ((AgenteAnfitrion) myAgent).terminarBatalla(); }
        });
    }*/  
    
    public static Position randomPosition() {
        return new Position(BORDER_X + (int) ((MAX_X - 2 * BORDER_X) * Math.random()), 
                            BORDER_Y + (int) ((MAX_Y - 2 * BORDER_Y) * Math.random()));
    }
}