
package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionAdapter;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 *
 * @author Chris O
 */
public abstract class ProtegeDisplayEntityListener<T, ENTITY_T extends OWLEntity> extends EntitySelectionAdapter<T> {


    private final OWLWorkspace workspace;
    
    public ProtegeDisplayEntityListener(OWLWorkspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void entityDoubleClicked(T entity) {
        displayEntity(getEntity(entity));
    }
    
    private void displayEntity(OWLEntity entity) {
        workspace.getOWLSelectionModel().setSelectedEntity(entity);
        workspace.displayOWLEntity(entity);
    }
    
    protected abstract ENTITY_T getEntity(T entry);
}
