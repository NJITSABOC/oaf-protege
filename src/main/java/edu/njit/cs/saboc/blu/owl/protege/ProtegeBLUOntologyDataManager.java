package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
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

    private boolean useInferredVersion = false;
    private Optional<Hierarchy<OWLConcept>> currentInferredHierarchy = Optional.empty();
    
    private OWLPAreaTaxonomy currentStatedTaxonomy;
    
    private Optional<OWLPAreaTaxonomy> currentInferredTaxonomy = Optional.empty();
    
    private Set<PropertyTypeAndUsage> currentPropertyUsages;
            
    public ProtegeBLUOntologyDataManager(OWLOntologyManager manager, File ontologyFile, String ontologyName, OWLOntology ontology) {
        super(manager, ontologyFile, ontologyName, ontology);
    }
    
    public void setCurrentStatedTaxonomy(OWLPAreaTaxonomy currentStatedTaxonomy) {
        this.currentStatedTaxonomy = currentStatedTaxonomy;
        this.currentPropertyUsages = currentStatedTaxonomy.getPropertyTypesAndUsages();
    }
    
    public OWLPAreaTaxonomy getCurrentStatedTaxonomy() {
        return currentStatedTaxonomy;
    }
    
    public void setCurrentInferredTaxonomy(OWLPAreaTaxonomy currentInferredTaxonomy) {
        this.currentInferredTaxonomy = Optional.ofNullable(currentInferredTaxonomy);
        
        if(currentInferredTaxonomy != null) {
             this.currentPropertyUsages = currentInferredTaxonomy.getPropertyTypesAndUsages();
        }
    }
    
    public Optional<OWLPAreaTaxonomy> getCurrentInferredTaxonomy() {
        return this.currentInferredTaxonomy;
    }
    
    public Set<PropertyTypeAndUsage> getCurrentPropertyUsages() {
        return currentPropertyUsages;
    }
    
    public void setUseInferred(boolean useInferred) {
        this.useInferredVersion = useInferred;
    }

    public boolean useInferredVersion() {
        return useInferredVersion;
    }

    public OWLPAreaTaxonomy deriveCompleteStatedTaxonomy(Set<PropertyTypeAndUsage> usages) {
        
        Hierarchy<OWLConcept> currentStatedHierarchy = getOntology().getConceptHierarchy();
           
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(this, usages);
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();
        return (OWLPAreaTaxonomy) generator.derivePAreaTaxonomy(factory, currentStatedHierarchy);
    }

    public OWLPAreaTaxonomy deriveCompleteInferredTaxonomy(Set<PropertyTypeAndUsage> usages) {
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(this, usages);
        PAreaTaxonomyGenerator generator = new PAreaTaxonomyGenerator();
        return (OWLPAreaTaxonomy) generator.derivePAreaTaxonomy(factory, currentInferredHierarchy.get());
    }
  
    public void refreshOntology(){
        this.reinitialize();
    }

    public void setInferredHierarchy(Hierarchy<OWLConcept> hierarchy) {
        this.currentInferredHierarchy = Optional.of(hierarchy);
    }
}
