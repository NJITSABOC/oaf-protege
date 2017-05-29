package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeLiveTaxonomyDataManager;
import java.util.Optional;

/**
 *
 * @author Chris Ochs
 */
public class LiveDiffTaxonomyManager {
    
    private Hierarchy<OWLConcept> currentStatedHierarchy;
    
    private OWLPAreaTaxonomy currentStatedTaxonomy;
    
    private OWLPAreaTaxonomy lastUpdatedStatedTaxonomy;
    
    private OWLPAreaTaxonomy lastFixedPointStatedTaxonomy;

    private Optional<Hierarchy<OWLConcept>> currentInferredHierarchy = Optional.empty();

    private Optional<OWLPAreaTaxonomy> currentInferredTaxonomy = Optional.empty();
    
    private Optional<OWLPAreaTaxonomy> lastUpdatedInferredTaxonomy = Optional.empty();
    private Optional<OWLPAreaTaxonomy> lastFixedPointInferredTaxonomy = Optional.empty();
    
    
    private final ProtegeLiveTaxonomyDataManager dataManager;
    
    private DerivationSettings currentDerivationSettings = null;

    public LiveDiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public void setDerivationSettings(DerivationSettings settings) {
        this.currentDerivationSettings = settings;
    }

    public void setCurrentStatedHierarchy(Hierarchy<OWLConcept> currentStatedHierarchy) {
        this.currentStatedHierarchy = currentStatedHierarchy;
    }
    
    public void setCurrentInferredHierarchy(Hierarchy<OWLConcept> currentInferredHierarchy) {
        this.currentInferredHierarchy = Optional.of(currentInferredHierarchy);
    }

    public void setCurrentStatedTaxonomy(OWLPAreaTaxonomy currentStatedTaxonomy) {
        this.currentStatedTaxonomy = currentStatedTaxonomy;
    }

    public OWLPAreaTaxonomy getCurrentStatedTaxonomy() {
        return currentStatedTaxonomy;
    }

    public void setLastUpdatedStatedTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastUpdatedStatedTaxonomy = taxonomy;
    }

    public void setLastFixedPointStatedTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastFixedPointStatedTaxonomy = taxonomy;
    }

    public void setLastUpdatedInferredTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastUpdatedInferredTaxonomy = Optional.of(taxonomy);
    }

    public void setLastFixedPointInferredTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastFixedPointInferredTaxonomy = Optional.of(taxonomy);
    }

    public Optional<OWLPAreaTaxonomy> getCurrentInferredTaxonomy() {
        return this.currentInferredTaxonomy;
    }

    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomyProgressive(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastUpdatedStatedTaxonomy, toTaxonomy);
    }

    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomyFixedPoint(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastFixedPointStatedTaxonomy, toTaxonomy);
    }
    
    public OWLPAreaTaxonomy deriveCurrentStatedTaxonomy() {
        return createTaxonomyFrom(this.currentStatedHierarchy);
    }

    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomy(OWLPAreaTaxonomy fromTaxonomy, OWLPAreaTaxonomy toTaxonomy) {

        DiffPAreaTaxonomyGenerator diffTaxonomyGenerator = new DiffPAreaTaxonomyGenerator();

        OWLDiffPAreaTaxonomy diffTaxonomy
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(
                        new OWLDiffPAreaTaxonomyFactory(fromTaxonomy, toTaxonomy),
                        dataManager.getOntology(),
                        fromTaxonomy,
                        dataManager.getOntology(),
                        toTaxonomy);

        return diffTaxonomy;
    }

    public OWLDiffPAreaTaxonomy deriveInferredDiffTaxonomyProgressive(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastUpdatedInferredTaxonomy.get(), toTaxonomy);
    }

    public OWLDiffPAreaTaxonomy deriveInferredDiffTaxonomyFixedPoint(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastFixedPointInferredTaxonomy.get(), toTaxonomy);
    }

    public void setCurrentInferredTaxonomy(OWLPAreaTaxonomy currentInferredTaxonomy) {

        this.currentInferredTaxonomy = Optional.of(currentInferredTaxonomy);

        if (!lastUpdatedInferredTaxonomy.isPresent()) {
            lastUpdatedInferredTaxonomy = Optional.of(currentInferredTaxonomy);
        }

        if (!lastFixedPointInferredTaxonomy.isPresent()) {
            lastFixedPointInferredTaxonomy = Optional.of(currentInferredTaxonomy);
        }
    }

    public OWLPAreaTaxonomy deriveCompleteInferredTaxonomy() {
        return createTaxonomyFrom(this.currentInferredHierarchy.get());
    }
    
    private OWLPAreaTaxonomy createTaxonomyFrom(Hierarchy<OWLConcept> hierarchy) {
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(
                dataManager, 
                this.currentDerivationSettings.getTypesAndUsages());
        
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

        Hierarchy<OWLConcept> taxonomySubhierarchy = hierarchy.getSubhierarchyRootedAt(
                this.currentDerivationSettings.getRoot());

        OWLPAreaTaxonomy taxonomy = (OWLPAreaTaxonomy) generator.derivePAreaTaxonomy(factory, taxonomySubhierarchy);
        
        if(!this.currentDerivationSettings.getSelectedProperties().equals(this.currentDerivationSettings.getAvailableProperties())) {
            taxonomy = (OWLPAreaTaxonomy)taxonomy.getRelationshipSubtaxonomy(this.currentDerivationSettings.getSelectedProperties());
        }
        
        return taxonomy;
    }

}
