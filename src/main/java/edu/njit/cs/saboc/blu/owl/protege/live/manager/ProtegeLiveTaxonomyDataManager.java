
package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.owl.protege.live.manager.LiveDiffTaxonomyManager;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.protege.ProtegeOAFOntologyDataManager;
import org.protege.editor.owl.model.OWLModelManager;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeLiveTaxonomyDataManager extends ProtegeOAFOntologyDataManager {

    private final LiveDiffTaxonomyManager diffTaxonomyManager;
    
    public ProtegeLiveTaxonomyDataManager(
            OWLModelManager modelManager,
            OAFOntologyDataManager dataManager) {
        
        super(modelManager, dataManager);
        
        this.diffTaxonomyManager = new LiveDiffTaxonomyManager(this);
    }
    
    public LiveDiffTaxonomyManager getDiffTaxonomyManager() {
        return diffTaxonomyManager;
    }

    @Override
    public boolean useInferredRels() {
        return false;
    }
    
    public void refreshOntology() {
        this.initialize();
    }
}
