package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegePAreaTaxonomyConfigurationFactory {
    
    public OWLPAreaTaxonomyConfiguration createConfiguration(
            PAreaTaxonomy taxonomy, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager,
            boolean showingAreaTaxonomy) {
        
        OWLPAreaTaxonomyConfiguration pareaTaxonomyConfiguration = new OWLPAreaTaxonomyConfiguration(taxonomy);
        
        pareaTaxonomyConfiguration.setUIConfiguration(
                new ProtegePAreaTaxonomyUIConfiguration(
                        pareaTaxonomyConfiguration, 
                        workspace,
                        displayListener, 
                        frameManager, 
                        showingAreaTaxonomy));
        
        pareaTaxonomyConfiguration.setTextConfiguration(new OWLPAreaTaxonomyTextConfiguration(taxonomy));
        
        return pareaTaxonomyConfiguration;
    }
}
