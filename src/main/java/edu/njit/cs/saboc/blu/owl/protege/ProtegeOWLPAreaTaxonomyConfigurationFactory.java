package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLDisplayFrameListener;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeOWLPAreaTaxonomyConfigurationFactory extends OWLPAreaTaxonomyConfigurationFactory {
    
    private final OWLWorkspace workspace;
    
    public ProtegeOWLPAreaTaxonomyConfigurationFactory(OWLWorkspace workspace) {
        this.workspace = workspace;
    }
    
    public OWLPAreaTaxonomyConfiguration createConfiguration(OWLPAreaTaxonomy taxonomy, OWLDisplayFrameListener displayListener) {
        
        OWLPAreaTaxonomyConfiguration pareaTaxonomyConfiguration = new OWLPAreaTaxonomyConfiguration(taxonomy);
        pareaTaxonomyConfiguration.setUIConfiguration(new ProtegeOWLPAreaTaxonomyUIConfiguration(pareaTaxonomyConfiguration, displayListener, workspace));
        pareaTaxonomyConfiguration.setTextConfiguration(new OWLPAreaTaxonomyTextConfiguration(taxonomy));
        
        return pareaTaxonomyConfiguration;
    }
}
