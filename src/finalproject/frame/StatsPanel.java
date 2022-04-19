package finalproject.frame;

import finalproject.agents.HostAgent;
import finalproject.classes.FieldUnity;
import finalproject.classes.Species;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class StatsPanel extends JPanel {
    private JLabel label_tiempo;
    public static long m_startTime = 0L;
    protected NumberFormat m_avgFormat = NumberFormat.getInstance();
    // private JTextArea label_stocks;
    private JTextPane tPane;

    public StatsPanel() {
        super(new GridLayout(1, 6));
        m_avgFormat.setMaximumFractionDigits(2);
        m_avgFormat.setMinimumFractionDigits(2);
        //
        label_tiempo = new JLabel();
        m_startTime = System.currentTimeMillis();
        JPanel panel_tiempo = new JPanel();
        panel_tiempo.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 3, true), "Time"));
        panel_tiempo.add(label_tiempo);
        add(panel_tiempo);
        //
        JPanel panel_cells = new JPanel(new GridLayout(1, 2));
        panel_cells.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 3, true), "Stocks"));
        /*String s = "";
        for (Species sp : Species.ALL_SPECIES.values())
            s += sp.getFamily() + ": " + sp.getStock() + "\n";
        label_stocks = new JTextArea(s);*/
        // panel_ncells.add(label_stocks);
        tPane = new JTextPane();
        panel_cells.add(tPane);
        /*JPanel panel_ncells = new JPanel();
            panel_ncells.setBorder(new TitledBorder(new LineBorder(Color.BLUE, 3, true), "# Normal"));
            label_ncells = new JLabel("" + (HostAgent.number_of_N_cells - HostAgent.number_of_I_cells));
            panel_ncells.add(label_ncells);
        panel_cells.add(panel_ncells);*/
        /*JPanel panel_icells = new JPanel();
            panel_icells.setBorder(new TitledBorder(new LineBorder(Color.RED, 3, true), "# Infected Cells"));
            label_icells = new JLabel("" + HostAgent.number_of_I_cells);
            panel_icells.add(label_icells);
        panel_cells.add(panel_icells);*/
        add(panel_cells);
        //
        /*label_virus = new JLabel("" + (HostAgent.number_of_Virus - HostAgent.number_of_I_cells));
        JPanel panel_virus = new JPanel();
        panel_virus.setBorder(new TitledBorder(new LineBorder(Color.RED, 3, true), "# Virus particules"));
        panel_virus.add(label_virus);
        add(panel_virus);*/
        //
        // JPanel panel_dcell = new JPanel(new GridLayout(1, 3));
        // panel_dcell.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 3, true), "# Defensive
        // Cells"));
        /*JPanel panel_tcells = new JPanel();
            panel_tcells.setBorder(new TitledBorder(new LineBorder(Color.GREEN, 3, true), "# T"));
            panel_tcells.add(new JLabel("" + HostAgent.number_of_B_cells));
        panel_dcell.add(panel_tcells);*/
        /*JPanel panel_bcells = new JPanel();
            panel_bcells.setBorder(new TitledBorder(new LineBorder(Color.MAGENTA, 3, true), "# B"));
            panel_bcells.add(new JLabel("" + HostAgent.number_of_T_cells));
        panel_dcell.add(panel_bcells);*/
        /*JPanel panel_antibodies = new JPanel();
            panel_antibodies.setBorder(new TitledBorder(new LineBorder(Color.CYAN, 3, true), "# Antibodies"));
            label_antibodies = new JLabel("" + HostAgent.number_of_Antibodies);
            panel_antibodies.add(label_antibodies);
        panel_dcell.add(panel_antibodies);*/
        // add(panel_dcell);
    }

    public double calcularTiempo() {
        return (System.currentTimeMillis() - m_startTime) / 1000.0;
    }

    // public void inicializar() { m_startTime = System.currentTimeMillis(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // removeAll();
        // setLayout(new GridLayout(2, 4));
        /*add(new JLabel("#Normal Cells: " + (HostAgent.number_of_N_cells - HostAgent.number_of_I_cells)));
        add(new JLabel("2"));
        add(new JLabel("3"));
        add(new JLabel("4"));
        add(new JLabel());
        add(new JLabel("6"));
        add(new JLabel("7"));*/
        // label_ncells.setText("" + (HostAgent.number_of_N_cells - HostAgent.number_of_I_cells));
        // label_icells.setText("" + HostAgent.number_of_I_cells);
        // label_virus.setText("" + (HostAgent.number_of_Virus - HostAgent.number_of_I_cells));
        // label_antibodies.setText("" + HostAgent.number_of_Antibodies);
        double tiempo = calcularTiempo();
        if (!HostAgent.STARTED) tiempo = 0;
        label_tiempo.setText(m_avgFormat.format(tiempo) + "s");

        tPane.setText("");
        for (Species sp : Species.ALL_SPECIES.values()) {
            int n_alives = 0;
            for (FieldUnity member : sp.members.values()) {
                if (member.is_alive) n_alives += 1;
            }
            appendToPane(
                    tPane,
                    sp.getFamily()
                            + ": Stock ("
                            + sp.getStock()
                            + ") - #Members("
                            + n_alives
                            + ")"
                            + "\n",
                    sp.color);
        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
}
