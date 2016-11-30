package edu.njit.cs.saboc.blu.owl.protege;

import java.util.ArrayList;

/**
 *
 * @author Chris O
 */
public class DiffDerivationTypeManager {
    
    public static enum DerivationType {
        FixedPoint,
        Progressive
    }
    
    public interface DerivationTypeChangedListener {
        public void derivationTypeChange(DerivationType newDerivationType);
        
        public void resetFixedPointStart();
    }
    
    private final ArrayList<DerivationTypeChangedListener> derivationTypeListeners = new ArrayList<>();
    
    private DerivationType derivationType = DerivationType.FixedPoint;
    
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
            listener.derivationTypeChange(type);
        });
    }
    
    public void resetFixedPointDerivation() {
        derivationTypeListeners.forEach( (listener) -> {
            listener.resetFixedPointStart();
        });
    }
    
    public DerivationType getDerivationType() {
        return derivationType;
    }
}
