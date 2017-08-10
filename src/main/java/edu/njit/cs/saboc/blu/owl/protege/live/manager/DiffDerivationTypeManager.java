package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class DiffDerivationTypeManager {
    
    private final Logger logger = LoggerFactory.getLogger(DiffDerivationTypeManager.class);

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
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffDerivationTypeManager",
                ""));
    }

    public void addDerivationTypeChangedListener(DerivationTypeChangedListener listener) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "addDerivationTypeChangedListener", 
                ""));
        
        
        derivationTypeListeners.add(listener);
    }

    public void removeDerivationTypeChangedListener(DerivationTypeChangedListener listener) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "removeDerivationTypeChangedListener",
                ""));
        
        derivationTypeListeners.remove(listener);
    }

    public void setDerivationType(DerivationType type) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setDerivationType",
                String.format("type: %s", type.toString())));
        
        this.derivationType = type;

        derivationTypeListeners.forEach((listener) -> {
            listener.derivationTypeChanged(type);
        });
    }

    public void setRelationshipType(RelationshipType relType) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setRelationshipType",
                String.format("relType: %s", relType.toString())));
        
        this.relationshipType = relType;

        derivationTypeListeners.forEach((listener) -> {
            listener.relationshipTypeChanged(relType);
        });
    }

    public void resetFixedPointDerivation() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "resetFixedPointDerivation",
                ""));

        derivationTypeListeners.forEach((listener) -> {
            listener.resetFixedPointStart();
        });
    }

    public void setInferredHierarchyAvailable(boolean value) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setInferredHierarchyAvailable",
                String.format("value: %s", Boolean.toString(value))));
        
        this.inferredHierarchyAvailable = value;

        if(!value && relationshipType == RelationshipType.Inferred) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "setInferredHierarchyAvailable",
                "Reverting to stated relationships"));
            
            setRelationshipType(RelationshipType.Stated);
        }
    }

    public boolean inferredHierarchyAvailable() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "inferredHierarchyAvailable",
                String.format("Returning: %s", Boolean.toString(inferredHierarchyAvailable))));
        
        return this.inferredHierarchyAvailable;
    }

    public DerivationType getDerivationType() {

         logger.debug(LogMessageGenerator.createLiveDiffString(
                "getDerivationType",
                String.format("Returning: %s", derivationType.toString())));
        
        return derivationType;
    }

    public RelationshipType getRelationshipType() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "getRelationshipType",
                String.format("Returning: %s", relationshipType.toString())));

        return relationshipType;
    }
}
