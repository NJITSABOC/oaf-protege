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
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLDisplayFrameListener;

import edu.njit.cs.saboc.blu.owl.gui.dialogs.listeners.OWLPAreaDetailsActionAdapter;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.*;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLInternalConceptBrowserFrame;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLNATAdjustableLayout;
import edu.njit.cs.saboc.blu.owl.protege.nat.UpdatingOWLBrowserDataSource;
import edu.njit.cs.saboc.nat.generic.gui.panels.FocusConceptPanel;
import java.awt.GraphicsEnvironment;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.change.AddAxiomData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeRecord;

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
                        
                    case ONTOLOGY_VISIBILITY_CHANGED:
                    case ENTITY_RENDERER_CHANGED:
                    case ENTITY_RENDERING_CHANGED:
                    case REASONER_CHANGED:
                    case ABOUT_TO_CLASSIFY:
                    case ONTOLOGY_CLASSIFIED:
                    case ONTOLOGY_CREATED:
                    case ONTOLOGY_LOADED:
                    case ONTOLOGY_RELOADED:
                    case ONTOLOGY_SAVED:
                        
                    default:
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

        BLUOntologyDataManager loader = new BLUOntologyDataManager(ontologyManager, null, ontology.getOntologyID().toString(), ontology);

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
        
        UpdatingOWLBrowserDataSource dataSource = new UpdatingOWLBrowserDataSource(ontologyManager, ontology);

        OWLInternalConceptBrowserFrame browserFrame = new OWLInternalConceptBrowserFrame(getMyFrame(), dataSource,
                new OWLDisplayFrameListener(getMyFrame()) {
                    public void displayFrame(final JInternalFrame internalFrame) {
                        Container contentPane = internalFrame.getContentPane();
                        
                        JFrame contentFrame = new JFrame();

                        contentFrame.setTitle("Biomedical Layout Utility for OWL (BLUOWL) by SABOC");

                        contentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                        contentFrame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
                        
                        contentFrame.add(contentPane);
                        
                        contentFrame.setVisible(true);
                    }
                }
        );

        final FocusConceptPanel<OWLClass> fcPanel = ((OWLNATAdjustableLayout) browserFrame.getBrowser().getNATLayout()).getFocusConceptPanel();

        JButton editButton = new JButton("Edit Class");

        editButton.addActionListener(e -> {
            OWLClass cls = (OWLClass) browserFrame.getBrowser().getFocusConcept().getConcept();

            OWLWorkspace workspace = getOWLWorkspace();

            workspace.getOWLSelectionModel().setSelectedEntity(cls);
            workspace.displayOWLEntity(cls);
        });
        
        OWLOntologyChangeListener changeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                
                changes.forEach((OWLOntologyChange change) -> {
                    OWLOntologyChangeData changeData = change.getChangeData();
                    OWLOntologyChangeRecord changeRecord = change.getChangeRecord();
                                       
                    if(change.isAddAxiom()) {
                        AddAxiom addAxiom = (AddAxiom)change;
                        
                        AddAxiomData addAxiomData = addAxiom.getChangeData();
                        
                        
                    } else if(change.isAxiomChange()) {
                        OWLAxiomChange changeAxiom = (OWLAxiomChange)change;
                        
                        
                    } else if(change.isImportChange()) {
                        ImportChange importChange = (ImportChange)change;
                        
                        
                    } else if(change.isRemoveAxiom()) {
                        RemoveAxiom removeAxiom = (RemoveAxiom)change;
                        
                    } else {
                        
                    }
                });
                
                dataSource.update(browserFrame.getBrowser().getFocusConcept());
            }
        };
        
        OWLModelManager modelManager = getOWLModelManager();
        
        modelManager.addOntologyChangeListener(changeListener);

        fcPanel.addOptionButton(editButton);

        tabbedPane.add(browserFrame.getContentPane(), "OWL Neighborhood Auditing Tool (OWL NAT)");
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
