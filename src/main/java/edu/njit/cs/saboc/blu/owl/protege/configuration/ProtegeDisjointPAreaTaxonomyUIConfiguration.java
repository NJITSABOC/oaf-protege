package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyUIConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons.ProtegeDisjointPAreaOptionsPanel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointPAreaTaxonomyUIConfiguration extends OWLDisjointPAreaTaxonomyUIConfiguration {

    public ProtegeDisjointPAreaTaxonomyUIConfiguration(
            OWLDisjointPAreaTaxonomyConfiguration config, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager) {
        
        super(config, 
                new ProtegeDisjointPAreaTaxonomyListenerConfiguration(config, workspace), 
                displayListener, 
                frameManager);
    }

    @Override
    public NodeOptionsPanel getNodeOptionsPanel() {
        return new ProtegeDisjointPAreaOptionsPanel(getConfiguration());
    }
}
