
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointNode;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointPAreaTaxonomyConfigurationFactory {
    
    public OWLDisjointPAreaTaxonomyConfiguration createConfiguration(
            DisjointAbstractionNetwork<DisjointNode<PArea>, PAreaTaxonomy<PArea>, PArea> disjointTaxonomy, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager) {
        
        OWLDisjointPAreaTaxonomyConfiguration disjointConfiguration = new OWLDisjointPAreaTaxonomyConfiguration(disjointTaxonomy);
        
        disjointConfiguration.setUIConfiguration(
                new ProtegeDisjointPAreaTaxonomyUIConfiguration(
                        disjointConfiguration,
                        workspace, 
                        displayListener, 
                        frameManager));
        
        disjointConfiguration.setTextConfiguration(new OWLDisjointPAreaTaxonomyTextConfiguration(disjointTaxonomy));
        
        return disjointConfiguration;
    }
}
