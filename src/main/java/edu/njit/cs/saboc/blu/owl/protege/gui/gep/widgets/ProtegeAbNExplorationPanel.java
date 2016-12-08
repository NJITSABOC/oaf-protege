
package edu.njit.cs.saboc.blu.owl.protege.gui.gep.widgets;

import edu.njit.cs.saboc.blu.core.abn.PartitionedAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.node.PartitionedNode;
import edu.njit.cs.saboc.blu.core.abn.node.SinglyRootedNode;
import edu.njit.cs.saboc.blu.core.graph.BluGraph;
import edu.njit.cs.saboc.blu.core.graph.nodes.GenericPartitionEntry;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.BaseAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AbNPainter;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author Chris O
 */
public class ProtegeAbNExplorationPanel extends JPanel {
    
    public static final Logger log = Logger.getLogger(ProtegeAbNExplorationPanel.class);
        
    private AbNConfiguration configuration;
    
    private final AbNDisplayPanel displayPanel = new AbNDisplayPanel();

    public ProtegeAbNExplorationPanel() {
        super(new BorderLayout());

        displayPanel.addAbNSelectionListener(new AbNDisplayPanel.AbNEntitySelectionListener() {

            @Override
            public void nodeEntrySelected(SinglyRootedNodeEntry nodeEntry) {
                
            }

            @Override
            public void partitionEntrySelected(GenericPartitionEntry entry) {
                PartitionedAbstractionNetwork partitionedAbN = (PartitionedAbstractionNetwork)configuration.getAbstractionNetwork();

                PartitionedNode node = partitionedAbN.getPartitionNodeFor((SinglyRootedNode)entry.getNode().getInternalNodes().iterator().next());

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
            BluGraph graph, 
            AbNConfiguration config, 
            AbNPainter painter) {
         
         initialize(graph, config, painter, new BaseAbNExplorationPanelInitializer());
     }
    
    public void initialize(
            BluGraph graph, 
            AbNConfiguration config, 
            AbNPainter painter,
            AbNExplorationPanelGUIInitializer initializer) {
        
        this.configuration = config;
                
        displayPanel.initialize(graph, painter, initializer.getInitialDisplayAction());

        // Add display-specific widgets and wizbangs
        initializer.initializeAbNDisplayPanel(displayPanel);

        displayPanel.resetUpdateables();
        
        config.getUIConfiguration().setDisplayPanel(displayPanel);
    }
}
