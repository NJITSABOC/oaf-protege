
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyUIConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons.ProtegePAreaOptionsPanel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegePAreaTaxonomyUIConfiguration extends OWLPAreaTaxonomyUIConfiguration {
    
    public ProtegePAreaTaxonomyUIConfiguration(
            OWLPAreaTaxonomyConfiguration config, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager,
            boolean showingAreaTaxonomy) {
        
        super(config, 
                new ProtegePAreaTaxonomyListenerConfiguration(config, workspace), 
                displayListener, 
                frameManager, 
                showingAreaTaxonomy);
    }
    
    @Override
    public NodeOptionsPanel getNodeOptionsPanel() {
        OWLPAreaTaxonomyConfiguration owlConfig = getConfiguration();
        
        return new ProtegePAreaOptionsPanel(owlConfig, owlConfig.getPAreaTaxonomy().isAggregated());
    }
}
