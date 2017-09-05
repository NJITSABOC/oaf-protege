package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.diff.DiffTaxonomySubsetOptions;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import java.util.ArrayList;
import java.util.Optional;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
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
    
    private DiffTaxonomySubsetOptions currentDisplaySettings = null;
    
    private boolean inferredRelsAvailable = false;
    
    public interface DerivationSettingsChangedListener {
        public void derivationSettingsChanged(DerivationSettings settings);
        public void displaySettingsChanged(DiffTaxonomySubsetOptions displaySettings);
    }
    
    private final ArrayList<DerivationSettingsChangedListener> derivationChangedListeners = new ArrayList<>();
    
    public LiveDiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "LiveDiffTaxonomyManager", ""));
        
        this.dataManager = dataManager;
        
        this.statedDiffTaxonomyManager = new DiffTaxonomyManager(false);
        
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
    
    public void setDisplaySettings(DiffTaxonomySubsetOptions currentDisplaySettings) {
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setDisplaySettings", 
                        ""));
        
        this.currentDisplaySettings = currentDisplaySettings;
        
        derivationChangedListeners.forEach((listener) -> {
            listener.displaySettingsChanged(currentDisplaySettings);
        });
    }
    
    public Optional<DerivationSettings> getDerivationSettings() {
        return Optional.of(this.currentDerivationSettings);
    }
    
    public Optional<DiffTaxonomySubsetOptions> getDisplaySettings() {
        return Optional.of(this.currentDisplaySettings);
    }
    
    public void reset() {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "reset",
                        ""));
        
        try {
            this.statedDiffTaxonomyManager.reset();

            if (this.optInferredDiffTaxonomyManager.isPresent()) {

                logger.debug(
                        LogMessageGenerator.createLiveDiffString(
                                "reset",
                                "resetting inferred taxonomy manager"));

                optInferredDiffTaxonomyManager.get().reset();
            }
        } catch (OWLOntologyCreationException ooce) {
            ooce.printStackTrace();
        }
    }
    
    public void initialize() {

        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "initialize",
                        ""));
        
        try {
            
            this.statedDiffTaxonomyManager.initialize(dataManager);
            
        } catch(OWLOntologyCreationException ooce) {
            
            ooce.printStackTrace();
            
        }
    }
    
    public void setInferredRelsAvailable(boolean value) {
        
        logger.debug(
                LogMessageGenerator.createLiveDiffString(
                        "setInferredRelsAvailable",
                        String.format("value: %s", Boolean.toString(value))));
        
        this.inferredRelsAvailable = value;
        
        try {
            if (value) {
                DiffTaxonomyManager inferredDiffTaxonomyManager = new DiffTaxonomyManager(true);

                inferredDiffTaxonomyManager.setCurrentDerivationSettings(currentDerivationSettings);

                inferredDiffTaxonomyManager.initialize(dataManager);

                this.optInferredDiffTaxonomyManager = Optional.of(inferredDiffTaxonomyManager);
            } else {
                this.optInferredDiffTaxonomyManager = Optional.empty();
            }
        } catch (OWLOntologyCreationException ooce) {

            ooce.printStackTrace();

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
        
        try {

            this.statedDiffTaxonomyManager.update(dataManager);

            if (this.optInferredDiffTaxonomyManager.isPresent()) {

                logger.debug(
                        LogMessageGenerator.createLiveDiffString(
                                "update",
                                "updating inferred"));
                
                

                optInferredDiffTaxonomyManager.get().update(dataManager);
            }
            
        } catch (OWLOntologyCreationException exception) {

        }
    }
    
    public DiffTaxonomyManager getStatedDiffTaxonomyManager() {
        return this.statedDiffTaxonomyManager;
    }
    
    public DiffTaxonomyManager getInferredTaxonomyManager() {
        return this.optInferredDiffTaxonomyManager.get();
    }
}
