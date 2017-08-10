package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyManager {
    
    private final Logger logger = LoggerFactory.getLogger(DiffTaxonomyManager.class);

    private Hierarchy<OWLConcept> currentHierarchy;
    
    private PAreaTaxonomy currentTaxonomy = null;
    
    private PAreaTaxonomy lastUpdatedTaxonomy = null;
    private PAreaTaxonomy lastFixedPointTaxonomy = null;
    
    private DerivationSettings currentDerivationSettings;

    private final ProtegeLiveTaxonomyDataManager dataManager;
    
    public DiffTaxonomyManager(ProtegeLiveTaxonomyDataManager dataManager) {
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyManager",
                ""));

        this.dataManager = dataManager;
    }
    
    public void setCurrentDerivationSettings(DerivationSettings settings) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setCurrentDerivationSettings",
                String.format("settings: %s", settings)));
        
        this.currentDerivationSettings = settings;
    }
    
    public final void initialize(Hierarchy<OWLConcept> startingHierarchy) {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "initialize",
                String.format("startingHierarchy: %s (%d)", 
                        startingHierarchy.getRoot().getName(), 
                        startingHierarchy.size())));
        
        this.currentHierarchy = startingHierarchy;
        
        PAreaTaxonomy startingTaxonomy = this.createTaxonomyFrom(startingHierarchy);
        
        this.currentTaxonomy = startingTaxonomy;
        this.lastUpdatedTaxonomy = startingTaxonomy;
        this.lastFixedPointTaxonomy = startingTaxonomy;
    }
    
    public void reset() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "reset",
                ""));
        
        this.initialize(currentHierarchy);
    }
    
    public void update(Hierarchy<OWLConcept> hierarchy) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "update",
                String.format("hierarchy: %s (%d)", 
                        hierarchy.getRoot().getName(), 
                        hierarchy.size())));
        
        this.currentHierarchy = hierarchy;
        
        this.setCurrentTaxonomy(createTaxonomyFrom(hierarchy));
    }
    
    private void setCurrentTaxonomy(PAreaTaxonomy startingTaxonomy) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setCurrentTaxonomy",
                ""));
        
        this.lastUpdatedTaxonomy = this.currentTaxonomy;
        this.currentTaxonomy = startingTaxonomy;
    }
    
    public OWLDiffPAreaTaxonomy deriveFixedPointDiffTaxonomy() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveFixedPointDiffTaxonomy",
                ""));

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

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveProgressiveDiffTaxonomy",
                ""));

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
    
    private PAreaTaxonomy createTaxonomyFrom(Hierarchy<OWLConcept> hierarchy) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "createTaxonomyFrom",
                String.format("hierarchy: %s (%d)",
                        hierarchy.getRoot().getName(),
                        hierarchy.size())));
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(
                dataManager, 
                this.currentDerivationSettings.getTypesAndUsages());
        
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

        Hierarchy<OWLConcept> taxonomySubhierarchy = hierarchy.getSubhierarchyRootedAt(
                this.currentDerivationSettings.getRoot());

        PAreaTaxonomy taxonomy = generator.derivePAreaTaxonomy(factory, taxonomySubhierarchy);
        
        if(!this.currentDerivationSettings.getSelectedProperties().equals(
                this.currentDerivationSettings.getAvailableProperties())) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "createTaxonomyFrom",
                    String.format("Creating relationship subtaxonomy: %s", 
                            this.currentDerivationSettings.getSelectedProperties().toString())));

            
            taxonomy = taxonomy.getRelationshipSubtaxonomy(this.currentDerivationSettings.getSelectedProperties());
        }
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "createTaxonomyFrom",
                String.format("taxonomy: areas (%d), pareas (%d)",
                        taxonomy.getAreas().size(),
                        taxonomy.getPAreas().size())));
        
        return taxonomy;
    }
}
