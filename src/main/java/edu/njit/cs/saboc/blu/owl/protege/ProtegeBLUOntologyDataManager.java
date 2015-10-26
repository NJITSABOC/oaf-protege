package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.owl.abn.loader.BLUOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.datastructure.hierarchy.OWLClassHierarchy;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyUsageType;
import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Chris O
 */
public class ProtegeBLUOntologyDataManager extends BLUOntologyDataManager {

    private boolean useInferredVersion = false;
    private Optional<OWLClassHierarchy> currentInferredHierarchy = Optional.empty();
    
    private OWLPAreaTaxonomy currentStatedTaxonomy;
    
    private Optional<OWLPAreaTaxonomy> currentInferredTaxonomy = Optional.empty();
    
    private HashSet<PropertyUsageType> currentPropertyUsages;
            
    public ProtegeBLUOntologyDataManager(OWLOntologyManager manager, File ontologyFile, String ontologyName, OWLOntology ontology) {
        super(manager, ontologyFile, ontologyName, ontology);
    }
    
    public void setCurrentStatedTaxonomy(OWLPAreaTaxonomy currentStatedTaxonomy) {
        this.currentStatedTaxonomy = currentStatedTaxonomy;
        this.currentPropertyUsages = currentStatedTaxonomy.getPropertyTypes();
    }
    
    public OWLPAreaTaxonomy getCurrentStatedTaxonomy() {
        return currentStatedTaxonomy;
    }
    
    public void setCurrentInferredTaxonomy(OWLPAreaTaxonomy currentInferredTaxonomy) {
        this.currentInferredTaxonomy = Optional.ofNullable(currentInferredTaxonomy);
        
        if(currentInferredTaxonomy != null) {
             this.currentPropertyUsages = currentInferredTaxonomy.getPropertyTypes();
        }
    }
    
    public Optional<OWLPAreaTaxonomy> getCurrentInferredTaxonomy() {
        return this.currentInferredTaxonomy;
    }
    
    public HashSet<PropertyUsageType> getCurrentPropertyUsages() {
        return currentPropertyUsages;
    }
    
    public void setUseInferred(boolean useInferred) {
        this.useInferredVersion = useInferred;
    }

    @Override
    protected HashSet<OWLClass> getChildren(OWLClass cls) {
        if(useInferredVersion) {
            if(currentInferredHierarchy.isPresent()) {
                return currentInferredHierarchy.get().getChildren(cls);
            } else {
                return new HashSet<>();
            }
            
        } else {
            return super.getChildren(cls);
        }
    }
    
    public void setInferredHierarchy(OWLClassHierarchy hierarchy) {
        currentInferredHierarchy = Optional.of(hierarchy);
    }
    
    public boolean useInferredVersion() {
        return useInferredVersion;
    }
    
    public OWLPAreaTaxonomy deriveCompleteStatedTaxonomy(HashSet<PropertyUsageType> properties) {
        ProtegeBLUOntologyDataManager dataManager = new ProtegeBLUOntologyDataManager(
                this.getManager(), 
                this.getOntologyFile(), 
                this.getOntologyName(), 
                this.getOntology());
        
        return dataManager.createCompleteOntologyTaxonomy(properties);
    }
    
    public OWLPAreaTaxonomy deriveCompleteInferredTaxonomy(HashSet<PropertyUsageType> properties) {
        ProtegeBLUOntologyDataManager dataManager = new ProtegeBLUOntologyDataManager(
                this.getManager(), 
                this.getOntologyFile(), 
                this.getOntologyName(), 
                this.getOntology());

        if (this.currentInferredHierarchy.isPresent()) {
            dataManager.setUseInferred(true);
            dataManager.setInferredHierarchy(this.currentInferredHierarchy.get());

            return dataManager.createCompleteOntologyTaxonomy(properties);
        } else {
            return null;
        }
    }
}
