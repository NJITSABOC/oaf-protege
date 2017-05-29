package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.nodes.GenericPartitionEntry;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.BaseAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AbNPainter;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeAbNExplorationPanel extends JPanel {

    private AbNConfiguration configuration;
    
    private final AbNWarningManager warningManager;
    
    private final AbNDisplayPanel displayPanel = new AbNDisplayPanel();

    public ProtegeAbNExplorationPanel() {
        
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
                
            }

            @Override
            public void partitionEntrySelected(GenericPartitionEntry entry) {
                
            }

            @Override
            public void noEntriesSelected() {
                
            }
        });

        
        
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
            AbNConfiguration config, 
            AbNPainter painter) {
         
         initialize(graph, 
                 config, 
                 painter, 
                 new BaseAbNExplorationPanelInitializer(warningManager));
     }
     
    private boolean firstLoad = true;
    
    public void initialize(
            AbstractionNetworkGraph graph, 
            AbNConfiguration config, 
            AbNPainter painter,
            AbNExplorationPanelGUIInitializer initializer) {
        
        this.configuration = config;
                
        displayPanel.initialize(graph, painter, initializer.getInitialDisplayAction());

        initializer.initializeAbNDisplayPanel(displayPanel, firstLoad);
        
        this.firstLoad = false;

        displayPanel.resetUpdateables();
        
        config.getUIConfiguration().setDisplayPanel(displayPanel);
    }
}