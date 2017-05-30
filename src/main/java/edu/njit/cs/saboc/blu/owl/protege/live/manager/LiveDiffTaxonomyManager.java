package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeLiveTaxonomyDataManager;
import java.util.Optional;

/**
 *
 * @author Chris Ochs
 */
public class LiveDiffTaxonomyManager {
    
    private final DiffTaxonomyManager statedDiffTaxonomyManager;
    
    private Optional<DiffTaxonomyManager> optTnferredDiffTaxonomyManager;
    
    private final ProtegeLiveTaxonomyDataManager dataManager;

    private DerivationSettings currentDerivationSettings = null;
    
    private boolean inferredRelsAvailable = false;
    
    public LiveDiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.dataManager = dataManager;
        
        this.statedDiffTaxonomyManager = new DiffTaxonomyManager(dataManager);
        
        this.optTnferredDiffTaxonomyManager = Optional.empty();
    }
    
    public void setDerivationSettings(DerivationSettings settings) {
        this.currentDerivationSettings = settings;
        
        this.statedDiffTaxonomyManager.setCurrentDerivationSettings(settings);
        
        if(this.optTnferredDiffTaxonomyManager.isPresent()) {
            optTnferredDiffTaxonomyManager.get().setCurrentDerivationSettings(settings);
        }
    }
    
    public void reset() {
        
        this.statedDiffTaxonomyManager.reset();

        if (this.optTnferredDiffTaxonomyManager.isPresent()) {
            optTnferredDiffTaxonomyManager.get().reset();
        }
        
    }
    
    public void initialize() {
        this.statedDiffTaxonomyManager.initialize(dataManager.getOntology().getConceptHierarchy());
    }
    
    public void setInferredRelsAvailable(boolean value) {
        
        this.inferredRelsAvailable = value;
        
        if(value) {
            DiffTaxonomyManager inferredDiffTaxonomyManager = 
                    new DiffTaxonomyManager(dataManager);
            
            inferredDiffTaxonomyManager.setCurrentDerivationSettings(currentDerivationSettings);
            
            inferredDiffTaxonomyManager.initialize(
                    dataManager.createInferredOntology().getConceptHierarchy());
            
            this.optTnferredDiffTaxonomyManager = Optional.of(inferredDiffTaxonomyManager);
        } else {
            this.optTnferredDiffTaxonomyManager = Optional.empty();
        }
    }
    
    public boolean inferredRelsAvailable() {
        return this.inferredRelsAvailable;
    }
    
    public void update() {
        
        this.statedDiffTaxonomyManager.update(
                dataManager.getOntology().getConceptHierarchy());

        if (this.optTnferredDiffTaxonomyManager.isPresent()) {
            optTnferredDiffTaxonomyManager.get().update(
                    dataManager.createInferredOntology().getConceptHierarchy());
        }
    }
    
    public DiffTaxonomyManager getStatedDiffTaxonomyManager() {
        return this.statedDiffTaxonomyManager;
    }
    
    public DiffTaxonomyManager getInferredTaxonomyManager() {
        return this.optTnferredDiffTaxonomyManager.get();
    }
}
