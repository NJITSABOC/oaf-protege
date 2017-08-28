package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOWLOntology;
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

    private OAFOWLOntology currentOntology = null;
    private PAreaTaxonomy currentTaxonomy = null;
    
    private OAFOWLOntology previousOntology = null;
    private PAreaTaxonomy lastUpdatedTaxonomy = null;
    
    private OAFOWLOntology lastFixedPointOntology = null;
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
    
    public final void initialize(OAFOWLOntology startingOntology) {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "initialize",
                String.format("startingHierarchy: %s (%d)", 
                        startingOntology.getConceptHierarchy().getRoot().getName(), 
                        startingOntology.getConceptHierarchy().size())));
        
        this.currentOntology = startingOntology;
        this.previousOntology = startingOntology;
        this.lastFixedPointOntology = startingOntology;
        
        PAreaTaxonomy startingTaxonomy = this.createTaxonomyFrom(startingOntology);
        
        this.currentTaxonomy = startingTaxonomy;
        this.lastUpdatedTaxonomy = startingTaxonomy;
        this.lastFixedPointTaxonomy = startingTaxonomy;
    }
    
    public void reset() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "reset",
                ""));
        
        this.initialize(currentOntology);
    }
    
    public void update(OAFOWLOntology ontology) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "update",
                String.format("hierarchy: %s (%d)", 
                        ontology.getConceptHierarchy().getRoot().getName(), 
                        ontology.getConceptHierarchy().size())));
        
        this.previousOntology = this.currentOntology;
        
        this.currentOntology = ontology;

        this.setCurrentTaxonomy(createTaxonomyFrom(ontology));
    }
    
    private void setCurrentTaxonomy(PAreaTaxonomy startingTaxonomy) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setCurrentTaxonomy",
                String.format("beforeHierarchy: %d | afterHierarchy: %d", 
                        this.currentTaxonomy.getSourceHierarchy().size(), 
                       startingTaxonomy.getSourceHierarchy().size())));
        
        this.lastUpdatedTaxonomy = this.currentTaxonomy;
        
        this.currentTaxonomy = startingTaxonomy;
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setCurrentTaxonomy",
                ""));
    }
    
    public OWLDiffPAreaTaxonomy deriveFixedPointDiffTaxonomy() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveFixedPointDiffTaxonomy",
                ""));

        DiffPAreaTaxonomyGenerator diffTaxonomyGenerator = new DiffPAreaTaxonomyGenerator();

        OWLDiffPAreaTaxonomy diffTaxonomy
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(
                        new OWLDiffPAreaTaxonomyFactory(lastFixedPointTaxonomy, currentTaxonomy),
                        lastFixedPointOntology,
                        lastFixedPointTaxonomy,
                        currentOntology,
                        currentTaxonomy);
                
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveFixedPointDiffTaxonomy",
                String.format("before: %s | after: %s", 
                        lastFixedPointTaxonomy.toString(), 
                        currentTaxonomy.toString())));

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
                        previousOntology,
                        lastUpdatedTaxonomy,
                        currentOntology,
                        currentTaxonomy);
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveFixedPointDiffTaxonomy",
                String.format("before: %s | after: %s",
                        lastUpdatedTaxonomy.toString(),
                        currentTaxonomy.toString())));

        return diffTaxonomy;
    }
    
    private PAreaTaxonomy createTaxonomyFrom(OAFOWLOntology ontology) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "createTaxonomyFrom",
                String.format("hierarchy: %s (%d)",
                        ontology.getConceptHierarchy().getRoot().getName(),
                        ontology.getConceptHierarchy().size())));
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(
                dataManager, 
                this.currentDerivationSettings.getTypesAndUsages());
        
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

        Hierarchy<OWLConcept> taxonomySubhierarchy = 
                ontology.getConceptHierarchy().getSubhierarchyRootedAt(
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
