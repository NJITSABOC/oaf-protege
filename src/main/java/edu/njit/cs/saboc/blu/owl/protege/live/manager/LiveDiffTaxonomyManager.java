package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class LiveDiffTaxonomyManager {
    
    private final Logger logger = LoggerFactory.getLogger(DiffDerivationTypeManager.class);
    
    private final DiffTaxonomyManager statedDiffTaxonomyManager;
    
    private Optional<DiffTaxonomyManager> optTnferredDiffTaxonomyManager;
    
    private final ProtegeLiveTaxonomyDataManager dataManager;

    private DerivationSettings currentDerivationSettings = null;
    
    private boolean inferredRelsAvailable = false;
    
    public LiveDiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "LiveDiffTaxonomyManager", ""));
        
        this.dataManager = dataManager;
        
        this.statedDiffTaxonomyManager = new DiffTaxonomyManager(dataManager);
        
        this.optTnferredDiffTaxonomyManager = Optional.empty();
    }
    
    public void setDerivationSettings(DerivationSettings settings) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setDerivationSettings", 
                        String.format("settings: %s", settings.toString())));
        
        this.currentDerivationSettings = settings;
        
        this.statedDiffTaxonomyManager.setCurrentDerivationSettings(settings);
        
        if(this.optTnferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setDerivationSettings",
                        "Setting inferred taxomonomy derivation settings"));
            
            optTnferredDiffTaxonomyManager.get().setCurrentDerivationSettings(settings);
        }
    }
    
    public Optional<DerivationSettings> getDerivationSettings() {
        return Optional.of(this.currentDerivationSettings);
    }
    
    public void reset() {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "reset",
                        ""));
        
        this.statedDiffTaxonomyManager.reset();

        if (this.optTnferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                    LogMessageGenerator.createLiveDiffString(
                            "reset",
                            "resetting inferred taxonomy manager"));
            
            optTnferredDiffTaxonomyManager.get().reset();
        }
    }
    
    public void initialize() {

        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "initialize",
                        ""));
        
        this.statedDiffTaxonomyManager.initialize(dataManager.getOntology());
    }
    
    public void setInferredRelsAvailable(boolean value) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setInferredRelsAvailable",
                        String.format("value: %s", Boolean.toString(value))));
        
        this.inferredRelsAvailable = value;
        
        if(value) {
            DiffTaxonomyManager inferredDiffTaxonomyManager = 
                    new DiffTaxonomyManager(dataManager);
            
            inferredDiffTaxonomyManager.setCurrentDerivationSettings(currentDerivationSettings);
            
            inferredDiffTaxonomyManager.initialize(dataManager.createInferredOntology());
            
            this.optTnferredDiffTaxonomyManager = Optional.of(inferredDiffTaxonomyManager);
        } else {
            this.optTnferredDiffTaxonomyManager = Optional.empty();
        }
    }
    
    public boolean inferredRelsAvailable() {
        return this.inferredRelsAvailable;
    }
    
    public void update() {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "update",
                        ""));
        
        this.statedDiffTaxonomyManager.update(dataManager.getOntology());

        if (this.optTnferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                    LogMessageGenerator.createLiveDiffString(
                            "update",
                            "updating inferred"));
            
            optTnferredDiffTaxonomyManager.get().update(dataManager.createInferredOntology());
        }
    }
    
    public DiffTaxonomyManager getStatedDiffTaxonomyManager() {
        return this.statedDiffTaxonomyManager;
    }
    
    public DiffTaxonomyManager getInferredTaxonomyManager() {
        return this.optTnferredDiffTaxonomyManager.get();
    }
}
