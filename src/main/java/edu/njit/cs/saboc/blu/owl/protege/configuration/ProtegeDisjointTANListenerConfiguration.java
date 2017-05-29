package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointtan.configuration.OWLDisjointTANListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.listeners.ProtegeConceptSelectedListener;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointTANListenerConfiguration extends OWLDisjointTANListenerConfiguration {
    
    private final OWLWorkspace workspace;
    
    public ProtegeDisjointTANListenerConfiguration(
            OWLDisjointTANConfiguration config, 
            OWLWorkspace workspace) {
        
        super(config);
        
        this.workspace = workspace;
    }
    
    @Override
    public EntitySelectionListener<Concept> getGroupConceptListListener() {
        return new ProtegeConceptSelectedListener(workspace);
    }
}
