package edu.njit.cs.saboc.blu.owl.protege.configuration.listeners;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionAdapter;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeConceptSelectedListener extends EntitySelectionAdapter<Concept> {
    
    private final OWLWorkspace workspace;
    
    public ProtegeConceptSelectedListener(OWLWorkspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void entityDoubleClicked(Concept entity) {
        
        OWLConcept concept = (OWLConcept)entity;
        
        workspace.getOWLSelectionModel().setSelectedEntity(concept.getCls());
        workspace.displayOWLEntity(concept.getCls());
    }
}
