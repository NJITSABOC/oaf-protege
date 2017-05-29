package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.listeners.ProtegeConceptSelectedListener;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointPAreaTaxonomyListenerConfiguration extends OWLDisjointPAreaTaxonomyListenerConfiguration {
    
    private final OWLWorkspace workspace;
    
    public ProtegeDisjointPAreaTaxonomyListenerConfiguration(
            OWLDisjointPAreaTaxonomyConfiguration config,
            OWLWorkspace workspace) {
        
        super(config);
        
        this.workspace = workspace;
    }
    
    @Override
    public EntitySelectionListener<Concept> getGroupConceptListListener() {
        return new ProtegeConceptSelectedListener(workspace);
    }
}
