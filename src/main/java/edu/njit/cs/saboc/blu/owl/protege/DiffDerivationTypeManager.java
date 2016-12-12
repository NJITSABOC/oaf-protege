package edu.njit.cs.saboc.blu.owl.protege;

import java.util.ArrayList;

/**
 *
 * @author Chris O
 */
public class DiffDerivationTypeManager {
    
    public static enum RelationshipType {
        Stated,
        Inferred
    }
    
    public static enum DerivationType {
        FixedPoint,
        Progressive
    }
    
    public interface DerivationTypeChangedListener {
        public void derivationTypeChanged(DerivationType newDerivationType);
        
        public void relationshipTypeChanged(RelationshipType version);
        
        public void resetFixedPointStart();
    }
    
    private final ArrayList<DerivationTypeChangedListener> derivationTypeListeners = new ArrayList<>();
    
    private DerivationType derivationType = DerivationType.FixedPoint;
    private RelationshipType relationshipType = RelationshipType.Stated;
    
    private boolean inferredHierarchyAvailable = false;
    
    public DiffDerivationTypeManager() {
        
    }
    
    public void addDerivationTypeChangedListener(DerivationTypeChangedListener listener) {
        derivationTypeListeners.add(listener);
    }
    
    public void removeDerivationTypeChangedListener(DerivationTypeChangedListener listener) {
        derivationTypeListeners.remove(listener);
    }
    
    public void setDerivationType(DerivationType type) {
        this.derivationType = type;
        
        derivationTypeListeners.forEach( (listener) -> {
            listener.derivationTypeChanged(type);
        });
    }
    
    public void setRelationshipType(RelationshipType relType) {
        this.relationshipType = relType;

        derivationTypeListeners.forEach((listener) -> {
            listener.relationshipTypeChanged(relType);
        });
    }
    
    public void resetFixedPointDerivation() {
        derivationTypeListeners.forEach( (listener) -> {
            listener.resetFixedPointStart();
        });
    }
    
    public void setInferredHierarchyAvailable(boolean value) {
        this.inferredHierarchyAvailable = value;
    }
    
    public boolean inferredHierarchyAvailable() {
        return this.inferredHierarchyAvailable;
    }
    
    public DerivationType getDerivationType() {
        return derivationType;
    }
    
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }
}
