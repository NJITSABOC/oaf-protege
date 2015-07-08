
package edu.njit.cs.saboc.blu.owl.protege.nat;

import edu.njit.cs.saboc.blu.owl.gui.nat.OWLBrowserDataSource;
import edu.njit.cs.saboc.nat.generic.FocusConcept;
import javax.swing.SwingUtilities;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Chris O
 */
public class UpdatingOWLBrowserDataSource extends OWLBrowserDataSource {

    public UpdatingOWLBrowserDataSource(OWLOntologyManager manager, OWLOntology ontology) {
        super(manager, ontology);
    }
    
    public void update(FocusConcept<OWLClass> focusConcept) {
        focusConcept.setNATEnabled(false);
        
        Thread updateThread = new Thread(new Runnable() {
           public void run() {
               initializeDataSource();
               
               SwingUtilities.invokeLater(new Runnable() {
                   public void run() {
                       focusConcept.setNATEnabled(true);
                   }
               });
           } 
        });
        
        updateThread.start();
        
        
    }
}
