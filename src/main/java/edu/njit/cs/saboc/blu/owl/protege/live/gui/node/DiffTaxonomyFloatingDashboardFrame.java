package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyFloatingDashboardFrame extends JInternalFrame {

    private final DiffTaxonomyDashboardPanel dashboardPanel;

    public DiffTaxonomyFloatingDashboardFrame(DiffTaxonomyDashboardPanel dashboardPanel) {

        super("", false, false, false, false);

        this.dashboardPanel = dashboardPanel;

        JPanel internalPanel = new JPanel(new BorderLayout());

        internalPanel.add(dashboardPanel, BorderLayout.CENTER);

        this.add(internalPanel);

        this.setSize(500, 100);

        JComponent titlePanel = (BasicInternalFrameTitlePane) ((BasicInternalFrameUI) getUI()).getNorthPane();
        titlePanel.setPreferredSize(new Dimension(-1, 10));
        titlePanel.setBackground(new Color(100, 100, 255));

        this.setFrameIcon(null);

        this.setVisible(true);
    }
}
