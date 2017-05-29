package edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons;

import edu.njit.cs.saboc.blu.core.abn.tan.Cluster;
import edu.njit.cs.saboc.blu.core.abn.tan.aggregate.AggregateCluster;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.CreateDisjointAbNFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.CreateTANFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.ExportSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.tan.buttons.CreateAncestorTANButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.tan.buttons.CreateRootTANButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.tan.buttons.ExpandAggregateClusterButton;
import edu.njit.cs.saboc.blu.owl.abn.OWLAbstractionNetwork;
import edu.njit.cs.saboc.blu.owl.abn.tan.OWLTANFactory;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.listeners.DisplayTANAction;

/**
 *
 * @author Chris O
 */
public class ProtegeClusterOptionsPanel extends NodeOptionsPanel {

    public ProtegeClusterOptionsPanel(OWLTANConfiguration config, boolean forAggregate) {
        
        OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork)config.getAbstractionNetwork();
        
        
        if(forAggregate) {
            
        } else {
            CreateDisjointAbNFromSinglyRootedNodeButton<Cluster> createDisjointBtn = new CreateDisjointAbNFromSinglyRootedNodeButton<>(
                    config,
                    (disjointTAN) -> {
                        config.getUIConfiguration().getAbNDisplayManager().displayDisjointTribalAbstractionNetwork(disjointTAN);
                    });

            super.addOptionButton(createDisjointBtn);
        }
        
        CreateRootTANButton rootTANBtn = new CreateRootTANButton(config,
            new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(rootTANBtn);
        
        
        CreateAncestorTANButton ancestorTANBtn = new CreateAncestorTANButton(config,
                new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));

        super.addOptionButton(ancestorTANBtn);
        
        
        if(forAggregate) {
            ExpandAggregateClusterButton<AggregateCluster> expandAggregateBtn = new ExpandAggregateClusterButton<>(config, 
                (tan) -> {
                    config.getUIConfiguration().getAbNDisplayManager().displayTribalAbstractionNetwork(tan);
                });
            
            super.addOptionButton(expandAggregateBtn);
        }

        CreateTANFromSinglyRootedNodeButton tanBtn = new CreateTANFromSinglyRootedNodeButton(
                new OWLTANFactory(owlAbN.getDataManager()),
                config,
                new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));

        super.addOptionButton(tanBtn);
        
        
        
        ExportSinglyRootedNodeButton exportBtn = new ExportSinglyRootedNodeButton(config);

        super.addOptionButton(exportBtn);

        
        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("cluster", () -> {
            Cluster cluster = (Cluster)super.getCurrentNode().get();
            
            NodeDashboardPanel anp = config.getUIConfiguration().createNodeDetailsPanel();
            anp.setContents(cluster);

            return anp;
        });

        super.addOptionButton(popoutBtn);
        
        
        NodeHelpButton helpBtn = new NodeHelpButton(config);

        super.addOptionButton(helpBtn);
    }
}
