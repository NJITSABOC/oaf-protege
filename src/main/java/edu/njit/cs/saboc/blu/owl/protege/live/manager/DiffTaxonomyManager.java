package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeLiveTaxonomyDataManager;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyManager {

    private OWLPAreaTaxonomy currentTaxonomy = null;
    private OWLPAreaTaxonomy lastUpdatedTaxonomy = null;
    private OWLPAreaTaxonomy lastFixedPointTaxonomy = null;

    private final ProtegeLiveTaxonomyDataManager dataManager;
    
    public DiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public void initialize(OWLPAreaTaxonomy startingTaxonomy) {
        this.currentTaxonomy = startingTaxonomy;
        this.lastUpdatedTaxonomy = startingTaxonomy;
        this.lastFixedPointTaxonomy = startingTaxonomy;
    }
    
    public void reset() {
        this.initialize(currentTaxonomy);
    }
    
    public void setCurrentTaxonomy(OWLPAreaTaxonomy startingTaxonomy) {
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
}
