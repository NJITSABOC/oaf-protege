
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.listeners.ProtegeConceptSelectedListener;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegePAreaTaxonomyListenerConfiguration extends OWLPAreaTaxonomyListenerConfiguration {
    
    private final OWLWorkspace workspace;

    public ProtegePAreaTaxonomyListenerConfiguration(
            OWLPAreaTaxonomyConfiguration config, 
            OWLWorkspace workspace) {
        
        super(config);
        
        this.workspace = workspace;
    }

    @Override
    public EntitySelectionListener<Concept> getGroupConceptListListener() {
        return new ProtegeConceptSelectedListener(workspace);
    }
}
