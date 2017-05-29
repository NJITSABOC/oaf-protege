package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointNode;
import edu.njit.cs.saboc.blu.core.abn.tan.Cluster;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANUIConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons.ProtegeDisjointClusterOptionsPanel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointTANUIConfiguration extends OWLDisjointTANUIConfiguration {
    
    public ProtegeDisjointTANUIConfiguration(
            OWLDisjointTANConfiguration config,
            OWLWorkspace workspace, 
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager) {
        
        super(config, 
                new ProtegeDisjointTANListenerConfiguration(config, workspace), 
                displayListener, 
                frameManager);
    }
    
    @Override
    public NodeOptionsPanel<DisjointNode<Cluster>> getNodeOptionsPanel() {
        return new ProtegeDisjointClusterOptionsPanel(getConfiguration(), getConfiguration().getAbstractionNetwork().isAggregated());
    }

}
