
package edu.njit.cs.saboc.blu.owl.protege.live.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.listeners.ProtegeConceptSelectedListener;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDiffPAreaTaxonomyListenerConfiguration extends OWLDiffPAreaTaxonomyListenerConfiguration {

    private final OWLWorkspace workspace;
    
    public ProtegeDiffPAreaTaxonomyListenerConfiguration(
            ProtegeDiffPAreaTaxonomyConfiguration config,
            OWLWorkspace workspace) {
        
        super(config);
        
        this.workspace = workspace;
    }
    
    @Override
    public EntitySelectionListener<Concept> getGroupConceptListListener() {
        return new ProtegeConceptSelectedListener(workspace);
    }
}
