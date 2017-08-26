package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

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

        this.setSize(500, 300);

        this.setFrameIcon(null);

        this.setVisible(true);
    }
}
