package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Chris O
 */
public class ProtegeBLUOntologyDataManager extends OAFOntologyDataManager {

    private OWLPAreaTaxonomy currentStatedTaxonomy;
    
    private OWLPAreaTaxonomy lastUpdatedStatedTaxonomy;
    private OWLPAreaTaxonomy lastFixedPointStatedTaxonomy;
    
    
    private Optional<Hierarchy<OWLConcept>> currentInferredHierarchy = Optional.empty();
    
    private Optional<OWLPAreaTaxonomy> currentInferredTaxonomy = Optional.empty();
    
    private Optional<OWLPAreaTaxonomy> lastUpdatedInferredTaxonomy = Optional.empty();
    private Optional<OWLPAreaTaxonomy> lastFixedPointInferredTaxonomy = Optional.empty();
    
    public ProtegeBLUOntologyDataManager(OWLOntologyManager manager, File ontologyFile, String ontologyName, OWLOntology ontology) {
        super(manager, ontologyFile, ontologyName, ontology);
    }

    public void setCurrentStatedTaxonomy(OWLPAreaTaxonomy currentStatedTaxonomy) {
        this.currentStatedTaxonomy = currentStatedTaxonomy;
    }

    public OWLPAreaTaxonomy getCurrentStatedTaxonomy() {
        return currentStatedTaxonomy;
    }

    public void setLastUpdatedStatedTaxonomy(OWLPAreaTaxonomy taxonomy) {
        lastUpdatedStatedTaxonomy = taxonomy;
    }

    public void setLastFixedPointStatedTaxonomy(OWLPAreaTaxonomy taxonomy) {
        lastFixedPointStatedTaxonomy = taxonomy;
    }
    
    public Set<PropertyTypeAndUsage> getCurrentPropertyUsages() {
        return currentStatedTaxonomy.getPropertyTypesAndUsages();
    }

    public OWLPAreaTaxonomy deriveCompleteStatedTaxonomy(Set<PropertyTypeAndUsage> usages) {
        Hierarchy<OWLConcept> currentStatedHierarchy = getOntology().getConceptHierarchy();

        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(this, usages);
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();
        return (OWLPAreaTaxonomy) generator.derivePAreaTaxonomy(factory, currentStatedHierarchy);
    }
    
    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomyProgressive(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastUpdatedStatedTaxonomy, toTaxonomy);
    }

    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomyFixedPoint(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastFixedPointStatedTaxonomy, toTaxonomy);
    }

    public OWLDiffPAreaTaxonomy deriveStatedDiffTaxonomy(OWLPAreaTaxonomy fromTaxonomy, OWLPAreaTaxonomy toTaxonomy) {
        DiffPAreaTaxonomyGenerator diffTaxonomyGenerator = new DiffPAreaTaxonomyGenerator();

        OWLDiffPAreaTaxonomy diffTaxonomy
                = (OWLDiffPAreaTaxonomy) diffTaxonomyGenerator.createDiffPAreaTaxonomy(
                        new OWLDiffPAreaTaxonomyFactory(),
                        getOntology(),
                        fromTaxonomy,
                        getOntology(),
                        toTaxonomy);

        return diffTaxonomy;
    }

    public void refreshOntology() {
        this.reinitialize();
    }

    public void setInferredHierarchy(Hierarchy<OWLConcept> hierarchy) {
        this.currentInferredHierarchy = Optional.of(hierarchy);
    }
    
    public void setCurrentInferredTaxonomy(OWLPAreaTaxonomy currentInferredTaxonomy) {
        this.currentInferredTaxonomy = Optional.of(currentInferredTaxonomy);
        
        if(!lastUpdatedInferredTaxonomy.isPresent()) {
            lastUpdatedInferredTaxonomy = Optional.of(currentInferredTaxonomy);
        }
        
        if(!lastFixedPointInferredTaxonomy.isPresent()) {
            lastFixedPointInferredTaxonomy = Optional.of(currentInferredTaxonomy);
        }
    }
    
    public Optional<OWLPAreaTaxonomy> getCurrentInferredTaxonomy() {
        return this.currentInferredTaxonomy;
    }
    
    public OWLDiffPAreaTaxonomy deriveInferredDiffTaxonomyProgressive(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastUpdatedInferredTaxonomy.get(), toTaxonomy);
    }

    public OWLDiffPAreaTaxonomy deriveInferredDiffTaxonomyFixedPoint(OWLPAreaTaxonomy toTaxonomy) {
        return deriveStatedDiffTaxonomy(lastFixedPointInferredTaxonomy.get(), toTaxonomy);
    }
    
    public OWLPAreaTaxonomy deriveCompleteInferredTaxonomy(Set<PropertyTypeAndUsage> usages) {
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(this, usages);
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();
        
        return (OWLPAreaTaxonomy) generator.derivePAreaTaxonomy(factory, currentInferredHierarchy.get());
    }
    
    public void setLastUpdatedInferredTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastUpdatedInferredTaxonomy = Optional.of(taxonomy);
    }

    public void setLastFixedPointInferredTaxonomy(OWLPAreaTaxonomy taxonomy) {
        this.lastFixedPointInferredTaxonomy = Optional.of(taxonomy);
    }
}
