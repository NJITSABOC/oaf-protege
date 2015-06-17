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
import edu.njit.cs.saboc.blu.owl.gui.conceptbrowser.OWLBrowserDataSource;
import edu.njit.cs.saboc.blu.owl.gui.conceptbrowser.OWLInternalConceptBrowserFrame;
import edu.njit.cs.saboc.blu.owl.gui.conceptbrowser.OWLNATLayout;
import edu.njit.cs.saboc.blu.owl.gui.dialogs.listeners.OWLPAreaDetailsActionAdapter;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.*;
import edu.njit.cs.saboc.nat.generic.gui.panels.FocusConceptPanel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

public class BLUOWLProtegePluginMain extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    public static final Logger log = Logger.getLogger(BLUOWLProtegePluginMain.class);

    private class ProtegeOWLTaxonomyPanel extends JPanel {

        public ProtegeOWLTaxonomyPanel(OWLInternalGraphFrame graphFrame) {
            super(new BorderLayout());

            this.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        }
    }

    private JTabbedPane tabbedPane = new JTabbedPane();

    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        OWLModelManager modelManager = getOWLModelManager();

        OWLModelManagerListener modelListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                switch (event.getType()) {
                    case ACTIVE_ONTOLOGY_CHANGED:
                        initializeBLUOWL();
                        break;
                }
            }
        };

        modelManager.addListener(modelListener);

        this.add(tabbedPane, BorderLayout.CENTER);
        
        initializeBLUOWL();

        log.info("BLUOWL STATUS: Initialized");
    }

    private void initializeBLUOWL() {
        tabbedPane.removeAll();

        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();

        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        OWLPAreaTaxonomyLoader loader = new OWLPAreaTaxonomyLoader(ontologyManager, ontology.getOntologyID().toString(), ontology);

        log.info(loader.getOntologyRoots());

        HashSet<PropertyUsageType> usages = new HashSet<PropertyUsageType>();

        usages.add(PropertyUsageType.OPExplicitDomain);

        OWLPAreaTaxonomy taxonomy = loader.createCompleteOntologyTaxonomy(usages);

        log.info("BLUOWL STATUS: Created initial taxonomy");

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

        tabbedPane.add(new ProtegeOWLTaxonomyPanel(graphFrame), "Partial-area Taxonomies");

        OWLBrowserDataSource dataSource = new OWLBrowserDataSource(ontologyManager, ontology);
        
        OWLInternalConceptBrowserFrame browserFrame = new OWLInternalConceptBrowserFrame(getMyFrame(), dataSource);
        
        final FocusConceptPanel<OWLClass> fcPanel = ((OWLNATLayout)browserFrame.getBrowser().getNATLayout()).getFocusConceptPanel();
        
        JButton editButton = new JButton("Edit");
        
        editButton.addActionListener(e -> {
            OWLClass cls = (OWLClass)browserFrame.getBrowser().getFocusConcept().getConcept();
            
            OWLWorkspace workspace = getOWLWorkspace();

            workspace.getOWLSelectionModel().setSelectedEntity(cls);
            workspace.displayOWLEntity(cls);
        });
        
        fcPanel.addOptionButton(editButton);

        tabbedPane.add(browserFrame.getContentPane(), "OWL NAT");
        
        

    }

    private JFrame getMyFrame() {

        Container cont = this.getParent();
        if (cont == null) {
            return null;
        }

        while (cont != null && !(cont instanceof JFrame)) {
            cont = cont.getParent();
        }

        JFrame f = (JFrame) cont;

        return f;
    }

    protected void disposeOWLView() {

    }

}
