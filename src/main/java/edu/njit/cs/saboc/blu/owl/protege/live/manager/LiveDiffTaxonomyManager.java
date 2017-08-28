package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import java.util.ArrayList;
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
    
    private Optional<DiffTaxonomyManager> optInferredDiffTaxonomyManager;
    
    private final ProtegeLiveTaxonomyDataManager dataManager;

    private DerivationSettings currentDerivationSettings = null;
    
    private boolean inferredRelsAvailable = false;
    
    public interface DerivationSettingsChangedListener {
        public void derivationSettingsChanged(DerivationSettings settings);
    }
    
    private final ArrayList<DerivationSettingsChangedListener> derivationChangedListeners = new ArrayList<>();
    
    public LiveDiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "LiveDiffTaxonomyManager", ""));
        
        this.dataManager = dataManager;
        
        this.statedDiffTaxonomyManager = new DiffTaxonomyManager(dataManager);
        
        this.optInferredDiffTaxonomyManager = Optional.empty();
    }
    
    public void addDerivationSettingsChangedListener(DerivationSettingsChangedListener listener) {
        this.derivationChangedListeners.add(listener);
    }
    
    public void removeDerivationSettingsChangedListener(DerivationSettingsChangedListener listener) {
        this.derivationChangedListeners.remove(listener);
    }
    
    public void setDerivationSettings(DerivationSettings settings) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setDerivationSettings", 
                        String.format("settings: %s", settings.toString())));
        
        this.currentDerivationSettings = settings;
        
        this.statedDiffTaxonomyManager.setCurrentDerivationSettings(settings);
        
        if(this.optInferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setDerivationSettings",
                        "Setting inferred taxomonomy derivation settings"));
            
            optInferredDiffTaxonomyManager.get().setCurrentDerivationSettings(settings);
        }
        
        derivationChangedListeners.forEach( (listener) -> {
            listener.derivationSettingsChanged(settings);
        });
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

        if (this.optInferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                    LogMessageGenerator.createLiveDiffString(
                            "reset",
                            "resetting inferred taxonomy manager"));
            
            optInferredDiffTaxonomyManager.get().reset();
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
            
            this.optInferredDiffTaxonomyManager = Optional.of(inferredDiffTaxonomyManager);
        } else {
            this.optInferredDiffTaxonomyManager = Optional.empty();
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

        if (this.optInferredDiffTaxonomyManager.isPresent()) {
            
            logger.debug(
                    LogMessageGenerator.createLiveDiffString(
                            "update",
                            "updating inferred"));
            
            optInferredDiffTaxonomyManager.get().update(dataManager.createInferredOntology());
        }
    }
    
    public DiffTaxonomyManager getStatedDiffTaxonomyManager() {
        return this.statedDiffTaxonomyManager;
    }
    
    public DiffTaxonomyManager getInferredTaxonomyManager() {
        return this.optInferredDiffTaxonomyManager.get();
    }
}
