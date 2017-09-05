package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOWLOntology;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeOAFOntologyDataManager extends OAFOntologyDataManager {
    
    private final OWLModelManager protegeModelManager;
    
    private boolean inferredRelsAvailable = false;
    
    public ProtegeOAFOntologyDataManager(
            OWLModelManager protegeModelManager,
            OAFOntologyDataManager sourceManager) {
        
        super(sourceManager.getOAFStateFileManager(),
                sourceManager.getManager(), 
                sourceManager.getOntologyFile(), 
                sourceManager.getOntologyName(), 
                sourceManager.getSourceOntology());
        
        this.protegeModelManager = protegeModelManager;
    }
    
    public OWLModelManager getProtegeModelManager() {
        return protegeModelManager;
    }
    
    public void setInferredRelsAvailable(boolean value) {
        this.inferredRelsAvailable = value;
        
        initialize();
    } 
    
    public boolean inferredRelsAvailable() {
        return this.inferredRelsAvailable;
    }
    
    public boolean useInferredRels() {
        return inferredRelsAvailable();
    }

    @Override
    protected OAFOWLOntology createOAFOntology() {
        
        if(useInferredRels()) {
            return createInferredOntology();
        } else {
            return super.createOAFOntology();
        }
    }
    
    public OAFOWLOntology createInferredOntology() {

        OWLObjectHierarchyProvider<OWLClass> protegeInferredHierarchyProvider = 
                protegeModelManager.getOWLHierarchyManager().getInferredOWLClassHierarchyProvider();
  
        OWLConcept root = getOntology().getOWLConceptFor(protegeModelManager.getOWLDataFactory().getOWLThing());

        Hierarchy<OWLConcept> oafInferredHierarchy = new Hierarchy<>(root);

        Set<OWLConcept> processed = new HashSet<>();
        Set<OWLConcept> inQueue = new HashSet<>();

        Queue<OWLConcept> queue = new ArrayDeque<>();

        queue.add(root);
        inQueue.add(root);
        
        // BFS inferred hierarchy to copy hierarchy into OAF hierarchy
        while (!queue.isEmpty()) {
            OWLConcept cls = queue.remove();

            processed.add(cls);
            inQueue.remove(cls);

            Set<OWLClass> children = protegeInferredHierarchyProvider.getChildren(cls.getCls());

            Set<OWLConcept> childrenConcepts = children.stream().map((childCls) -> {
                return getOntology().getOWLConceptFor(childCls);
            }).collect(Collectors.toSet());

            childrenConcepts.forEach( (child) -> {
                oafInferredHierarchy.addEdge(child, cls);

                if (!inQueue.contains(child) && !processed.contains(child)) {
                    queue.add(child);
                    inQueue.add(child);
                }
            });
        }
        
        return new OAFOWLOntology(oafInferredHierarchy, this);
    }
}
