package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.util.Set;

/**
 *
 * @author Chris Ochs
 */
public class DerivationSettings {
    
    private final OWLConcept root;
    
    private final Set<PropertyTypeAndUsage> typesAndUsages;
    
    private final Set<InheritableProperty> selectedProperties;
    private final Set<InheritableProperty> availableProperties;
    
    public DerivationSettings(
            OWLConcept root, 
            Set<PropertyTypeAndUsage> typesAndUsages, 
            Set<InheritableProperty> selectedProperties,
            Set<InheritableProperty> availableProperties) {
        
        this.root = root;
        
        this.typesAndUsages = typesAndUsages;
        this.selectedProperties = selectedProperties;
        this.availableProperties = availableProperties;
    }

    public OWLConcept getRoot() {
        return root;
    }

    public Set<PropertyTypeAndUsage> getTypesAndUsages() {
        return typesAndUsages;
    }

    public Set<InheritableProperty> getSelectedProperties() {
        return selectedProperties;
    }

    public Set<InheritableProperty> getAvailableProperties() {
        return availableProperties;
    }
    
    @Override
    public String toString() {
        
        String result = "{root: %s, "
                + "typesAndUsages: %s, "
                + "selectedProperties: %s, "
                + "availableProperties: %s}";
        
        result = String.format(
                result, 
                root.getName(), 
                typesAndUsages.toString(),
                selectedProperties.toString(), 
                availableProperties.toString());
        
        return result;
    }
}
