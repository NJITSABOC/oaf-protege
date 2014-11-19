package edu.njit.cs.saboc.blu.owl.protege;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.awt.Container;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import org.semanticweb.owlapi.model.*;

import edu.njit.cs.saboc.blu.owl.abn.loader.*;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.*;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.*;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.*;




public class BLUOWLProtegePluginMain extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    public static final Logger log = Logger.getLogger(BLUOWLProtegePluginMain.class);

    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        
        log.info("BLUOWL STATUS: Initializing");
        
        OWLOntologyManager manager = getOWLModelManager().getOWLOntologyManager();
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        
        OWLPAreaTaxonomyLoader loader = new OWLPAreaTaxonomyLoader(manager, ontology.getOntologyID().toString(), ontology);

        log.info(loader.getOntologyRoots());
        
        HashSet<PropertyUsageType> usages = new HashSet<PropertyUsageType>();
        
        usages.add(PropertyUsageType.OPExplicitDomain);
        
        OWLPAreaTaxonomy taxonomy = loader.createCompleteOntologyTaxonomy(usages);
        
        log.info("BLUOWL STATUS: " + taxonomy.getPAreaCount());
               
        OWLInternalGraphFrame graphFrame = new OWLInternalGraphFrame(getMyFrame(), taxonomy, true, false, ontology);
        
        this.add(graphFrame, BorderLayout.CENTER);
        
        log.info("BLUOWL STATUS: Initialized");
    }
    
    private JFrame getMyFrame() {
        
        Container cont = this.getParent();
        if (cont == null) { 
            return null;
        }

        while(cont != null && !(cont instanceof JFrame)) {
             cont = cont.getParent();
        }
        
        JFrame f = (JFrame)cont;
        
        return f;
    }

    protected void disposeOWLView() {
        
    }
    
}