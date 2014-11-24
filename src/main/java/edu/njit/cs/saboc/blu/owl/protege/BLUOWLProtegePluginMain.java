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
import edu.njit.cs.saboc.blu.owl.gui.dialogs.listeners.OWLPAreaDetailsActionAdapter;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.*;
import javax.swing.JPanel;
import org.protege.editor.owl.model.OWLWorkspace;


public class BLUOWLProtegePluginMain extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    public static final Logger log = Logger.getLogger(BLUOWLProtegePluginMain.class);
    
    private class ProtegeOWLTaxonomyPanel extends JPanel {

        public ProtegeOWLTaxonomyPanel(OWLInternalGraphFrame graphFrame) {
            super(new BorderLayout());

            this.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        }
}

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
        
        graphFrame.addDialogActionListener(new OWLPAreaDetailsActionAdapter() {
            public void propertyDoubleClicked(OWLProperty property) {
                OWLWorkspace workspace = getOWLWorkspace();
                
                workspace.getOWLSelectionModel().setSelectedEntity(property);
                
                workspace.displayOWLEntity(property);
            }

            public void classDoubleClicked(OWLClass cls) {
                OWLWorkspace workspace = getOWLWorkspace();

                workspace.getOWLSelectionModel().setSelectedEntity(cls);
                workspace.displayOWLEntity(cls);
            }
        });
        
        this.add(new ProtegeOWLTaxonomyPanel(graphFrame), BorderLayout.CENTER);
        
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