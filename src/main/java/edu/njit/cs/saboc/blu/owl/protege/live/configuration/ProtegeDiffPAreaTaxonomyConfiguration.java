package edu.njit.cs.saboc.blu.owl.protege.live.configuration;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyConfiguration;

/**
 *
 * @author Chris O
 */
public class ProtegeDiffPAreaTaxonomyConfiguration extends OWLDiffPAreaTaxonomyConfiguration {

    public ProtegeDiffPAreaTaxonomyConfiguration(DiffPAreaTaxonomy taxonomy) {
        super(taxonomy);
    }
  
    public void setUIConfiguration(ProtegeDiffPAreaTaxonomyUIConfiguration config) {
        super.setUIConfiguration(config);
    }
    
    public void setTextConfiguration(ProtegeDiffPAreaTaxonomyTextConfiguration config) {
        super.setTextConfiguration(config);
    }
    
    @Override
    public ProtegeDiffPAreaTaxonomyUIConfiguration getUIConfiguration() {
        return (ProtegeDiffPAreaTaxonomyUIConfiguration)super.getUIConfiguration();
    }
    
    @Override
    public ProtegeDiffPAreaTaxonomyTextConfiguration getTextConfiguration() {
        return (ProtegeDiffPAreaTaxonomyTextConfiguration)super.getTextConfiguration();
    }
}
