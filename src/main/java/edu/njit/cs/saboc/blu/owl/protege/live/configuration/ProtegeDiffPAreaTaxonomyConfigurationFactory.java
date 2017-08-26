package edu.njit.cs.saboc.blu.owl.protege.live.configuration;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDiffPAreaTaxonomyConfigurationFactory {
    
    public ProtegeDiffPAreaTaxonomyConfiguration createConfiguration(
            DiffPAreaTaxonomy taxonomy, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener) {
        
        ProtegeDiffPAreaTaxonomyConfiguration pareaTaxonomyConfiguration = new ProtegeDiffPAreaTaxonomyConfiguration(taxonomy);
        pareaTaxonomyConfiguration.setUIConfiguration(
                new ProtegeDiffPAreaTaxonomyUIConfiguration(
                        pareaTaxonomyConfiguration, 
                        workspace, 
                        displayListener));
        
        pareaTaxonomyConfiguration.setTextConfiguration(new ProtegeDiffPAreaTaxonomyTextConfiguration(taxonomy));
        
        return pareaTaxonomyConfiguration;
    }
}
