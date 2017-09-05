package edu.njit.cs.saboc.blu.owl.protege.live.manager;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.ProtegeOAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyManager {
    
    private final Logger logger = LoggerFactory.getLogger(DiffTaxonomyManager.class);

    private ProtegeOAFOntologyDataManager currentDataManager = null;
    private PAreaTaxonomy currentTaxonomy = null;
    
    private ProtegeOAFOntologyDataManager previousDataManager = null;
    private PAreaTaxonomy lastUpdatedTaxonomy = null;
    
    private ProtegeOAFOntologyDataManager lastFixedPointDataManager = null;
    private PAreaTaxonomy lastFixedPointTaxonomy = null;
    
    private DerivationSettings currentDerivationSettings;

    private final boolean useInferredRels;
    
    public DiffTaxonomyManager(boolean useInferredRels) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyManager",
                ""));
        
        this.useInferredRels = useInferredRels;
    }
        
    public void setCurrentDerivationSettings(DerivationSettings settings) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "setCurrentDerivationSettings",
                String.format("settings: %s", settings)));
        
        this.currentDerivationSettings = settings;
    }

    public final void initialize(ProtegeOAFOntologyDataManager dataManager) throws OWLOntologyCreationException {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "initialize",
                ""));
        
        ProtegeOAFOntologyDataManager manager = deepCloneOAFOntologyManager(dataManager);
        
        if(useInferredRels) {
            manager.setInferredRelsAvailable(true);
        } else {
            manager.initialize();
        }
        
        this.currentDataManager = manager;
        this.previousDataManager = manager;
        this.lastFixedPointDataManager = manager;
        
        PAreaTaxonomy startingTaxonomy = this.createTaxonomyFrom(manager);
        
        this.currentTaxonomy = startingTaxonomy;
        this.lastUpdatedTaxonomy = startingTaxonomy;
        this.lastFixedPointTaxonomy = startingTaxonomy;
    }
    
    // Makes a copy of the ontology into new data manager
    private ProtegeOAFOntologyDataManager deepCloneOAFOntologyManager(ProtegeOAFOntologyDataManager dataManager) throws OWLOntologyCreationException {
        
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        OWLOntology ontologyClone = manager.createOntology(dataManager.getSourceOntology().getAxioms());
        
        OAFOntologyDataManager updatedOntologyManager = new OAFOntologyDataManager(
                dataManager.getOAFStateFileManager(), 
                dataManager.getManager(),
                dataManager.getOntologyFile(),
                dataManager.getOntologyName(),
                ontologyClone
            );
        
        ProtegeOAFOntologyDataManager protegeManager = new ProtegeOAFOntologyDataManager(
                dataManager.getProtegeModelManager(), updatedOntologyManager);
        
        return protegeManager;
    }
    
    public void reset() throws OWLOntologyCreationException {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "reset",
                ""));
        
        this.initialize(currentDataManager);
    }
    
    public void update(ProtegeOAFOntologyDataManager dataManager) throws OWLOntologyCreationException {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "update",
                ""));
        
        this.previousDataManager = this.currentDataManager;
        
        ProtegeOAFOntologyDataManager manager = deepCloneOAFOntologyManager(dataManager);
        
        if(useInferredRels) {
            manager.setInferredRelsAvailable(true);
        } else {
            manager.initialize();
        }
                
        this.currentDataManager = manager;

        this.setCurrentTaxonomy(createTaxonomyFrom(manager));
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
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(new OWLDiffPAreaTaxonomyFactory(lastFixedPointTaxonomy, currentTaxonomy),
                        lastFixedPointDataManager.getOntology(),
                        lastFixedPointTaxonomy,
                        currentDataManager.getOntology(),
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
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(new OWLDiffPAreaTaxonomyFactory(lastUpdatedTaxonomy, currentTaxonomy),
                        previousDataManager.getOntology(),
                        lastUpdatedTaxonomy,
                        currentDataManager.getOntology(),
                        currentTaxonomy);
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "deriveFixedPointDiffTaxonomy",
                String.format("before: %s | after: %s",
                        lastUpdatedTaxonomy.toString(),
                        currentTaxonomy.toString())));

        return diffTaxonomy;
    }
    
    private PAreaTaxonomy createTaxonomyFrom(OAFOntologyDataManager dataManager) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "createTaxonomyFrom",
                ""));
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(
                dataManager, 
                this.currentDerivationSettings.getTypesAndUsages());
        
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();

        Hierarchy<OWLConcept> taxonomySubhierarchy = 
                dataManager.getOntology().getConceptHierarchy().getSubhierarchyRootedAt(
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
