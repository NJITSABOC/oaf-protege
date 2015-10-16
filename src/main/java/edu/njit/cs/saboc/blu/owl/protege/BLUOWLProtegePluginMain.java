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

import edu.njit.cs.saboc.blu.owl.gui.graphframe.OWLInternalPAreaTaxonomyGraphFrame;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLInternalConceptBrowserFrame;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLNATAdjustableLayout;
import edu.njit.cs.saboc.blu.owl.protege.nat.UpdatingOWLBrowserDataSource;
import edu.njit.cs.saboc.nat.generic.gui.panels.FocusConceptPanel;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
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

        public ProtegeOWLTaxonomyPanel(OWLInternalPAreaTaxonomyGraphFrame graphFrame) {
            super(new BorderLayout());

            this.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        }
    }

    private JTabbedPane tabbedPane = new JTabbedPane();
    
    private OWLDisplayFrameListener displayListener;
         

    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        OWLModelManager modelManager = getOWLModelManager();

        OWLModelManagerListener modelListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                log.debug("In ontology model manager change listener...");
                
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
        
        OWLOntologyChangeListener changeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {

                changes.forEach((OWLOntologyChange change) -> {
                    OWLOntologyChangeData changeData = change.getChangeData();
                    OWLOntologyChangeRecord changeRecord = change.getChangeRecord();

                    if (change.isAddAxiom()) {
                        AddAxiom addAxiom = (AddAxiom) change;

                        AddAxiomData addAxiomData = addAxiom.getChangeData();

                    } else if (change.isAxiomChange()) {
                        OWLAxiomChange changeAxiom = (OWLAxiomChange) change;

                    } else if (change.isImportChange()) {
                        ImportChange importChange = (ImportChange) change;

                    } else if (change.isRemoveAxiom()) {
                        RemoveAxiom removeAxiom = (RemoveAxiom) change;

                    } else {

                    }
                });
                
                log.debug("In ontology change listener...");

                dataSource.update(conceptBrowserFrame.getBrowser().getFocusConcept());

                Thread taxonomyUpdateThread = new Thread(() -> {
                    OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();

                    OWLOntology ontology = getOWLModelManager().getActiveOntology();

                    BLUOntologyDataManager loader = new BLUOntologyDataManager(ontologyManager, null, ontology.getOntologyID().toString(), ontology);

                    OWLPAreaTaxonomy currentTaxonomy = (OWLPAreaTaxonomy)graphFrame.getGraph().getAbstractionNetwork();
                    
                    HashSet<PropertyUsageType> usages = currentTaxonomy.getPropertyTypes();

                    OWLPAreaTaxonomy newTaxonomy = loader.createCompleteOntologyTaxonomy(usages);

                    if(!currentTaxonomy.getRootGroup().getRootAsOWLClass().equals(newTaxonomy.getRootGroup().getRootAsOWLClass())) {
                        log.debug("CREATING SUBTAXONOMY...");
                        newTaxonomy = newTaxonomy.getRootSubtaxonomy(currentTaxonomy.getRootGroup());
                    }
                    
                    if(currentTaxonomy.isReduced()) {
                        ArrayList<OWLPArea> pareas = new ArrayList<>(currentTaxonomy.getGroupHierarchy().getNodesInHierarchy());
                        
                        int smallestSize = pareas.get(0).getConceptCount();
                        
                        for(int c = 0; c < pareas.size(); c++) {
                            if(pareas.get(c).getConceptCount() < smallestSize) {
                                smallestSize = pareas.get(c).getConceptCount();
                            }
                        }
                        
                        newTaxonomy = newTaxonomy.getReduced(smallestSize);
                    }
                    
                    graphFrame.replaceInternalFrameDataWith(newTaxonomy, ontology);
                });
                
                taxonomyUpdateThread.start();
            }
        };
        
        displayListener = new OWLDisplayFrameListener(getMyFrame()) {
            public void displayFrame(final JInternalFrame internalFrame) {
                Container contentPane = internalFrame.getContentPane();

                JFrame contentFrame = new JFrame();

                contentFrame.setTitle("Biomedical Layout Utility for OWL (BLUOWL) by SABOC");

                contentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                contentFrame.setBounds(new Rectangle(0, 0, 1200, 800));

                contentFrame.add(contentPane);

                contentFrame.setVisible(true);
                
                SwingUtilities.invokeLater( () -> {
                    contentFrame.toFront();
                    contentFrame.repaint();
                });
                
            }
        };
                
        modelManager.addOntologyChangeListener(changeListener);
        modelManager.addListener(modelListener);

        graphFrame = new OWLInternalPAreaTaxonomyGraphFrame(getMyFrame(), displayListener);              
                            
        tabbedPane.add(new ProtegeOWLTaxonomyPanel(graphFrame), "OWL Partial-area Taxonomies");
        
        this.add(tabbedPane, BorderLayout.CENTER);
        
        initializeBLUOWL();

        log.info("BLUOWL STATUS: Initialized");
    }
    
    private OWLInternalPAreaTaxonomyGraphFrame graphFrame;

    private OWLInternalConceptBrowserFrame conceptBrowserFrame;
    
    private UpdatingOWLBrowserDataSource dataSource;
    
    private void initializeBLUOWL() {
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();

        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        BLUOntologyDataManager loader = new BLUOntologyDataManager(ontologyManager, null, ontology.getOntologyID().toString(), ontology);

        HashSet<PropertyUsageType> usages = new HashSet<PropertyUsageType>();
        
        if(loader.getOntologyMetrics().totalOPWithDomainCount > 0) {
            usages.add(PropertyUsageType.OPExplicitDomain);
        } else if(loader.getOntologyMetrics().totalOPWithRestrictionCount > 1) {
            usages.add(PropertyUsageType.OPRestriction);
        } else {
            // TODO: Figure out whats really available...
            usages.add(PropertyUsageType.OPExplicitDomain);
        }

        OWLPAreaTaxonomy taxonomy = loader.createCompleteOntologyTaxonomy(usages);
        
        graphFrame.replaceInternalFrameDataWith(taxonomy, ontology);
        
        dataSource = new UpdatingOWLBrowserDataSource(ontologyManager, ontology);
        
        if(tabbedPane.getTabCount() > 1) {
            tabbedPane.remove(1);
        }
        
        conceptBrowserFrame = new OWLInternalConceptBrowserFrame(getMyFrame(), dataSource, displayListener);

        final FocusConceptPanel<OWLClass> fcPanel = ((OWLNATAdjustableLayout) conceptBrowserFrame.getBrowser().getNATLayout()).getFocusConceptPanel();

        JButton editButton = new JButton("Edit Class");

        editButton.addActionListener(e -> {
            OWLClass cls = (OWLClass) conceptBrowserFrame.getBrowser().getFocusConcept().getConcept();

            OWLWorkspace workspace = getOWLWorkspace();

            workspace.getOWLSelectionModel().setSelectedEntity(cls);
            workspace.displayOWLEntity(cls);
        });
        
        fcPanel.addOptionButton(editButton);
        
        tabbedPane.add(conceptBrowserFrame.getContentPane(), "OWL Neighborhood Auditing Tool (OWL NAT)");
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
