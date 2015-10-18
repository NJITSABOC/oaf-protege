package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.listeners.EntitySelectionListener;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyListenerConfiguration;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyDetails;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 *
 * @author Chris O
 */
public class ProtegeOWLPAreaTaxonomyListenerConfiguration extends OWLPAreaTaxonomyListenerConfiguration {
    
    private final OWLWorkspace workspace;
    
    public ProtegeOWLPAreaTaxonomyListenerConfiguration(OWLPAreaTaxonomyConfiguration config, OWLWorkspace workspace) {
        super(config);
        
        this.workspace = workspace;
    }
    
    @Override
    public EntitySelectionListener<PropertyDetails> getContainerRelationshipSelectedListener() {
        return new ProtegeDisplayEntityListener<PropertyDetails, OWLProperty>(workspace) {

            @Override
            protected OWLProperty getEntity(PropertyDetails entry) {
                return entry.getProperty();
            }
        };
    }

    @Override
    public EntitySelectionListener<PropertyDetails> getGroupRelationshipSelectedListener() {
        return new ProtegeDisplayEntityListener<PropertyDetails, OWLProperty>(workspace) {

            @Override
            protected OWLProperty getEntity(PropertyDetails entry) {
                return entry.getProperty();
            }
        };
    }

    @Override
    public EntitySelectionListener<OWLClass> getGroupConceptListListener() {
        return new ProtegeDisplayEntityListener<OWLClass, OWLClass>(workspace) {

            @Override
            protected OWLClass getEntity(OWLClass entry) {
                return entry;
            }
        };
    }

    
    
}
