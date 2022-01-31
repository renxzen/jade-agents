package finalproject.frame;

import static finalproject.environment.FoodSource.ALL_FOOD_SOURCES;
import finalproject.environment.FoodSource;
import finalproject.agents.HostAgent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
    public static EnvironmentPanel panel_principal;
    public static StatsPanel panel_stats;
    protected HostAgent anfitrion;
    
    public MainFrame(HostAgent anfitrion) {
        this.anfitrion = anfitrion;      
        try { inicializar(); }
        catch(Exception e) { e.printStackTrace(); }
    }
    
    private void inicializar() throws Exception {
        //maximizar el frame
        final GraphicsConfiguration config = getGraphicsConfiguration();
        final int left = Toolkit.getDefaultToolkit().getScreenInsets(config).left;
        final int right = Toolkit.getDefaultToolkit().getScreenInsets(config).right;
        final int top = Toolkit.getDefaultToolkit().getScreenInsets(config).top;
        final int bottom = Toolkit.getDefaultToolkit().getScreenInsets(config).bottom;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = screenSize.width - left - right;
        final int height = screenSize.height - top - bottom;
        setResizable(false);
        setSize(width, height);
        EnvironmentPanel.MAX_X = width;
        EnvironmentPanel.MAX_Y = height;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //
        setLayout(new BorderLayout());
            JButton button_start = new JButton("Start");
            button_start.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    StatsPanel.m_startTime = System.currentTimeMillis();
                    HostAgent.STARTED = true;
                    for (FoodSource source : ALL_FOOD_SOURCES.values())
                        source.start();
                }
            });                    
        add(button_start, BorderLayout.NORTH);
            panel_stats = new StatsPanel();
        add(panel_stats, BorderLayout.SOUTH);
            panel_principal = new EnvironmentPanel();
            panel_principal.setBackground(Color.BLUE);
        add(panel_principal, BorderLayout.CENTER);
    }
}