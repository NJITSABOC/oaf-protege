
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointNode;
import edu.njit.cs.saboc.blu.core.abn.tan.Cluster;
import edu.njit.cs.saboc.blu.core.abn.tan.ClusterTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointTANConfigurationFactory {
    
    public OWLDisjointTANConfiguration createConfiguration(
            DisjointAbstractionNetwork<DisjointNode<Cluster>, ClusterTribalAbstractionNetwork<Cluster>, Cluster> disjointTAN, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager) {
        
        OWLDisjointTANConfiguration disjointConfiguration = new OWLDisjointTANConfiguration(disjointTAN);
        
        disjointConfiguration.setUIConfiguration(
                new ProtegeDisjointTANUIConfiguration(
                        disjointConfiguration, 
                        workspace, 
                        displayListener, 
                        frameManager));
        
        disjointConfiguration.setTextConfiguration(new OWLDisjointTANTextConfiguration(disjointTAN));
        
        return disjointConfiguration;
    }
}
