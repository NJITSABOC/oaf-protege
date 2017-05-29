
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.listeners.ProtegeConceptSelectedListener;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeTANListenerConfiguration extends OWLTANListenerConfiguration {

    private final OWLWorkspace workspace;
    
    public ProtegeTANListenerConfiguration(OWLTANConfiguration config, OWLWorkspace workspace) {
        super(config);
        
        this.workspace = workspace;
    }
    
    @Override
    public EntitySelectionListener<Concept> getGroupConceptListListener() {
        return new ProtegeConceptSelectedListener(workspace);
    }
}
