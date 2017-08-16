package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Region;
import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.nodes.GenericPartitionEntry;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.BaseAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AbNPainter;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.live.gui.node.DiffTaxonomyDashboardPanel;
import edu.njit.cs.saboc.blu.owl.protege.live.gui.node.DiffTaxonomyFloatingDashboardFrame;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeDifTaxonomyExplorationPanel extends JPanel {

    private AbNConfiguration configuration;
    
    private final AbNWarningManager warningManager;
    
    private final AbNDisplayPanel displayPanel = new AbNDisplayPanel();
    
    private final DiffTaxonomyFloatingDashboardFrame dashboardFrame;
    private final DiffTaxonomyDashboardPanel dashboardPanel;

    public ProtegeDifTaxonomyExplorationPanel() {
        
        super(new BorderLayout());
        
        this.warningManager = new AbNWarningManager() {
            
            @Override
            public boolean showAggregationWarning() {
                return false;
            }
        };

        displayPanel.addAbNSelectionListener(new AbNDisplayPanel.AbNEntitySelectionListener() {

            @Override
            public void nodeEntrySelected(SinglyRootedNodeEntry nodeEntry) {
                dashboardPanel.displayDetailsForPArea((PArea)nodeEntry.getNode());
                dashboardFrame.setVisible(true);
            }

            @Override
            public void partitionEntrySelected(GenericPartitionEntry entry) {
                
                Region region = (Region)entry.getNode();
                
                dashboardPanel.displayDetailsForArea(region.getArea());
                dashboardFrame.setVisible(true);
            }

            @Override
            public void noEntriesSelected() {
                dashboardFrame.setVisible(false);
            }
        });
        
        displayPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               
                int x = (displayPanel.getWidth() - dashboardFrame.getWidth()) / 2;
                int y = displayPanel.getHeight() - dashboardFrame.getHeight() - 200;
               
                dashboardFrame.setLocation(x, y);
            }
            
        });
        
        this.dashboardPanel = new DiffTaxonomyDashboardPanel();
        this.dashboardFrame = new DiffTaxonomyFloatingDashboardFrame(dashboardPanel);
        
        this.dashboardFrame.setVisible(false);
        
        this.displayPanel.add(dashboardFrame);
        
        this.add(displayPanel, BorderLayout.CENTER);
    }
    
    public AbNDisplayPanel getDisplayPanel() {
        return displayPanel;
    }
    
    public void showLoading() {
        displayPanel.doLoading();
    }
    
     public void initialize(
            AbstractionNetworkGraph graph, 
            ProtegeDiffPAreaTaxonomyConfiguration config, 
            AbNPainter painter) {
         
         initialize(graph, 
                 config, 
                 painter, 
                 new BaseAbNExplorationPanelInitializer(warningManager));
     }
     
    private boolean firstLoad = true;
    
    public void initialize(
            AbstractionNetworkGraph graph, 
            ProtegeDiffPAreaTaxonomyConfiguration config, 
            AbNPainter painter,
            AbNExplorationPanelGUIInitializer initializer) {
        
        this.configuration = config;
                
        displayPanel.initialize(graph, painter, initializer.getInitialDisplayAction());
        dashboardPanel.initialize(config);

        initializer.initializeAbNDisplayPanel(displayPanel, firstLoad);
        
        this.firstLoad = false;

        displayPanel.resetUpdateables();
        
        config.getUIConfiguration().setDisplayPanel(displayPanel);
    }
}