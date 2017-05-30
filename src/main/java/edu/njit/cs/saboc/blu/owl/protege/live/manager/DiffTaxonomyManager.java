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

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyManager {

    private Hierarchy<OWLConcept> currentHierarchy;
    
    private OWLPAreaTaxonomy currentTaxonomy = null;
    private OWLPAreaTaxonomy lastUpdatedTaxonomy = null;
    private OWLPAreaTaxonomy lastFixedPointTaxonomy = null;
    
    private DerivationSettings currentDerivationSettings;

    private final ProtegeLiveTaxonomyDataManager dataManager;
    
    public DiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public void setCurrentDerivationSettings(DerivationSettings settings) {
        this.currentDerivationSettings = settings;
    }
    
    public final void initialize(Hierarchy<OWLConcept> startingHierarchy) {
        this.currentHierarchy = startingHierarchy;
        
        OWLPAreaTaxonomy startingTaxonomy = this.createTaxonomyFrom(startingHierarchy);
        
        this.currentTaxonomy = startingTaxonomy;
        this.lastUpdatedTaxonomy = startingTaxonomy;
        this.lastFixedPointTaxonomy = startingTaxonomy;
    }
    
    public void reset() {
        this.initialize(currentHierarchy);
    }
    
    public void update(Hierarchy<OWLConcept> hierarchy) {
        this.currentHierarchy = hierarchy;
        this.setCurrentTaxonomy(createTaxonomyFrom(hierarchy));
    }
    
    private void setCurrentTaxonomy(OWLPAreaTaxonomy startingTaxonomy) {
        this.lastUpdatedTaxonomy = this.currentTaxonomy;
        this.currentTaxonomy = startingTaxonomy;
    }
    
    public OWLDiffPAreaTaxonomy deriveFixedPointDiffTaxonomy() {

        DiffPAreaTaxonomyGenerator diffTaxonomyGenerator = new DiffPAreaTaxonomyGenerator();

        OWLDiffPAreaTaxonomy diffTaxonomy
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(
                        new OWLDiffPAreaTaxonomyFactory(lastFixedPointTaxonomy, currentTaxonomy),
                        dataManager.getOntology(),
                        lastFixedPointTaxonomy,
                        dataManager.getOntology(),
                        currentTaxonomy);

        return diffTaxonomy;
    }
    
    public OWLDiffPAreaTaxonomy deriveProgressiveDiffTaxonomy() {

        DiffPAreaTaxonomyGenerator diffTaxonomyGenerator = new DiffPAreaTaxonomyGenerator();

        OWLDiffPAreaTaxonomy diffTaxonomy
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(
                        new OWLDiffPAreaTaxonomyFactory(lastUpdatedTaxonomy, currentTaxonomy),
                        dataManager.getOntology(),
                        lastUpdatedTaxonomy,
                        dataManager.getOntology(),
                        currentTaxonomy);

        return diffTaxonomy;
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
