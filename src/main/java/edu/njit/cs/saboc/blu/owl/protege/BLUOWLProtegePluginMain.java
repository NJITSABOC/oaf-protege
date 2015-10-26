package edu.njit.cs.saboc.blu.owl.protege;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.awt.Container;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import org.semanticweb.owlapi.model.*;

import edu.njit.cs.saboc.blu.owl.utils.owlproperties.*;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.*;
import edu.njit.cs.saboc.blu.owl.datastructure.hierarchy.OWLClassHierarchy;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLDisplayFrameListener;

import edu.njit.cs.saboc.blu.owl.gui.graphframe.OWLInternalPAreaTaxonomyGraphFrame;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLInternalConceptBrowserFrame;
import edu.njit.cs.saboc.blu.owl.gui.nat.OWLNATAdjustableLayout;
import edu.njit.cs.saboc.blu.owl.protege.nat.UpdatingOWLBrowserDataSource;
import edu.njit.cs.saboc.nat.generic.gui.panels.FocusConceptPanel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owlapi.change.AddAxiomData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeRecord;

public class BLUOWLProtegePluginMain extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    public static final Logger log = Logger.getLogger(BLUOWLProtegePluginMain.class);
    
    private final HashMap<OWLOntology, ProtegeBLUOntologyDataManager> ontologyManagers = new HashMap<>();

    private class ProtegeOWLTaxonomyPanel extends JPanel {

        public ProtegeOWLTaxonomyPanel(OWLInternalPAreaTaxonomyGraphFrame graphFrame) {
            super(new BorderLayout());

            this.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        }
    }

    private final OWLModelManagerListener modelListener = new OWLModelManagerListener() {

        public void handleChange(OWLModelManagerChangeEvent event) {
            log.info("BLUOWL Event: " + event.getType());

            switch (event.getType()) {
                case ACTIVE_ONTOLOGY_CHANGED:
                    initializeBLUOWL();
                    break;

                case ONTOLOGY_VISIBILITY_CHANGED:
                case ENTITY_RENDERER_CHANGED:
                case ENTITY_RENDERING_CHANGED:
                    break;

                case REASONER_CHANGED:
                    log.info("BLUOWL: Reasoner Changed");

                    break;

                case ABOUT_TO_CLASSIFY:
                    useInferredBtn.setEnabled(false);
                    break;

                case ONTOLOGY_CLASSIFIED:
                    if(getOWLModelManager().getOWLReasonerManager().getCurrentReasoner().isConsistent()) {
                        useInferredBtn.setEnabled(true);
                    } else {
                        useInferredBtn.setEnabled(false);
                    }
                    
                    useInferredBtn.setText("Use Inferred Hierarchy");

                    break;

                case ONTOLOGY_CREATED:
                case ONTOLOGY_LOADED:
                case ONTOLOGY_RELOADED:
                case ONTOLOGY_SAVED:

                default:
            }
        }
    };
    
    private final IOListener ontologyIOListener = new IOListener() {

        @Override
        public void beforeSave(IOListenerEvent iole) {
            
        }

        @Override
        public void afterSave(IOListenerEvent iole) {
            
        }

        @Override
        public void beforeLoad(IOListenerEvent iole) {
            
        }

        @Override
        public void afterLoad(IOListenerEvent iole) {
            
        }
        
    };
    
    private final OWLOntologyChangeListener changeListener = new OWLOntologyChangeListener() {
        
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

            useInferredBtn.setEnabled(false);
            useInferredBtn.setText("Use Inferred Hierarchy (run reasoner to reenable)");

            dataSource.update(conceptBrowserFrame.getBrowser().getFocusConcept());

            updateStatedTaxonomy(true);
        }
    };

    private JTabbedPane tabbedPane = new JTabbedPane();
    
    private OWLDisplayFrameListener displayListener;
    
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

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
        
        graphFrame = new OWLInternalPAreaTaxonomyGraphFrame(getMyFrame(), 
                new ProtegeOWLPAreaTaxonomyConfigurationFactory(getOWLWorkspace()), displayListener);           
        
        useInferredBtn = new JToggleButton("Use Inferred Hierarchy");
        useInferredBtn.setEnabled(false);
        
        useInferredBtn.addActionListener( (ActionEvent ae) -> {
            
            OWLOntology ontology = getOWLModelManager().getActiveOntology();
                
            ProtegeBLUOntologyDataManager dataManager = ontologyManagers.get(ontology);
            
            log.info("BLUOWL: DataManager: " + dataManager);
            
            if(useInferredBtn.isSelected()) {
                dataManager.setUseInferred(true);
                    
                log.info("BLUOWL: UPDATING INFERRED...");
                
                if(dataManager.getCurrentInferredTaxonomy().isPresent()) {
                    log.info("BLUOWL: Displaying currently inferred...");
                    
                    graphFrame.replaceInternalFrameDataWith(dataManager.getCurrentInferredTaxonomy().get(), ontology);
                } else {
                    log.info("BLUOWL: Displaying new inferred...");
                    
                    updateInferredTaxonomy(true);
                }
            } else {
                dataManager.setUseInferred(false);
                
                log.info("BLUOWL: Displaying current stated...");
                
                graphFrame.replaceInternalFrameDataWith(dataManager.getCurrentStatedTaxonomy(), ontology);
            }
        });
        
        graphFrame.addReportButtonToMenu(useInferredBtn);
        graphFrame.addDisplayedTaxonomyChangedListener(new OWLInternalPAreaTaxonomyGraphFrame.DisplayedPAreaTaxonomyChangedListener() {

            @Override
            public void taxonomyDisplayed(OWLPAreaTaxonomy taxonomy) {
                OWLOntology ontology = getOWLModelManager().getActiveOntology();
                
                ProtegeBLUOntologyDataManager dataManager = ontologyManagers.get(ontology);
                
                HashSet<PropertyUsageType> oldUsages = dataManager.getCurrentPropertyUsages();
                HashSet<PropertyUsageType> newUsages = taxonomy.getPropertyTypes();
                
                boolean updateUsages = !oldUsages.equals(newUsages);
               
                log.info("BLUOWL: DataManager: " + dataManager + " | UseInferredVersion: " + dataManager.useInferredVersion());
                
                if(dataManager.useInferredVersion()) {
                    
                    if(dataManager.getCurrentInferredTaxonomy().isPresent()) {
                        if(dataManager.getCurrentInferredTaxonomy().get() != taxonomy) {
                            log.info("BLUOWL: Setting current inferred taxonomy...");
                            dataManager.setCurrentInferredTaxonomy(taxonomy);
                        }
                    } else {
                        log.info("BLUOWL: Setting current inferred taxonomy...");
                        
                        dataManager.setCurrentInferredTaxonomy(taxonomy);
                    }
                    
                    if(updateUsages) {
                        log.info("BLUOWL: Updating stated taxonomy...");
                        
                        updateStatedTaxonomy(false);
                    }
                } else {

                    if (taxonomy != dataManager.getCurrentStatedTaxonomy()) {
                        log.info("BLUOWL: Setting current stated taxonomy...");
                        
                        dataManager.setCurrentStatedTaxonomy(taxonomy);
                    }
                    
                    if (updateUsages) {
                        if(dataManager.getCurrentInferredTaxonomy().isPresent()) {
                            updateInferredTaxonomy(false);
                        }
                    }
                }
            }
        });
                       
        tabbedPane.add(new ProtegeOWLTaxonomyPanel(graphFrame), "OWL Partial-area Taxonomies");
        
        this.add(tabbedPane, BorderLayout.CENTER);
        
        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);
        
        initializeBLUOWL();
   
        log.info("BLUOWL STATUS: Initialized");
    }
    
    private OWLInternalPAreaTaxonomyGraphFrame graphFrame;
    
    private JToggleButton useInferredBtn;

    private OWLInternalConceptBrowserFrame conceptBrowserFrame;
    
    private UpdatingOWLBrowserDataSource dataSource;
    
    private void initializeBLUOWL() {
                
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();

        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        
        if(!ontologyManagers.containsKey(ontology)) {
            ontologyManagers.put(ontology, new ProtegeBLUOntologyDataManager(ontologyManager, null, ontology.getOntologyID().toString(), ontology));
        }

        ProtegeBLUOntologyDataManager loader = ontologyManagers.get(ontology);

        HashSet<PropertyUsageType> usages = new HashSet<>();
        
        if(loader.getOntologyMetrics().totalOPWithDomainCount > 0) {
            usages.add(PropertyUsageType.OPExplicitDomain);
        } else if(loader.getOntologyMetrics().totalOPWithRestrictionCount > 1) {
            usages.add(PropertyUsageType.OPRestriction);
        } else {
            // TODO: Figure out whats really available...
            usages.add(PropertyUsageType.OPExplicitDomain);
        }
        
        OWLPAreaTaxonomy taxonomy = loader.deriveCompleteStatedTaxonomy(usages);
        
        loader.setCurrentStatedTaxonomy(taxonomy);
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
    
    private void updateStatedTaxonomy(boolean showTaxonomy) {
        
        Thread taxonomyUpdateThread = new Thread(() -> {
            
            log.info("BLUOWL: Updating stated taxonomy");
            
            OWLOntology ontology = getOWLModelManager().getActiveOntology();

            ProtegeBLUOntologyDataManager loader = ontologyManagers.get(ontology);
            loader.reinitialize();

            HashSet<PropertyUsageType> usages = loader.getCurrentPropertyUsages();

            OWLPAreaTaxonomy taxonomy = loader.deriveCompleteStatedTaxonomy(usages);
            loader.setCurrentStatedTaxonomy(taxonomy);
            
            if(showTaxonomy) {
                graphFrame.replaceInternalFrameDataWith(taxonomy, ontology);
            }
        });

        taxonomyUpdateThread.start();
    }
    
    private void updateInferredTaxonomy(boolean showTaxonomy) {
        
        Thread loadThread = new Thread(() -> {
            OWLOntology ontology = getOWLModelManager().getActiveOntology();

            ProtegeBLUOntologyDataManager loader = ontologyManagers.get(ontology);

            OWLModelManager modelManager = getOWLModelManager();

            OWLObjectHierarchyProvider<OWLClass> inferredHierarchy = modelManager.getOWLHierarchyManager().getInferredOWLClassHierarchyProvider();

            OWLClass root = modelManager.getOWLDataFactory().getOWLThing();
            
            OWLClassHierarchy hierarchy = new OWLClassHierarchy(root);

            HashSet<OWLClass> processed = new HashSet<>();
            HashSet<OWLClass> inQueue = new HashSet<>();

            Queue<OWLClass> queue = new ArrayDeque<>();

            queue.add(root);
            inQueue.add(root);

            while (!queue.isEmpty()) {
                OWLClass cls = queue.remove();

                processed.add(cls);
                inQueue.remove(cls);

                Set<OWLClass> children = inferredHierarchy.getChildren(cls);

                children.forEach((OWLClass child) -> {
                    hierarchy.addIsA(child, cls);

                    if (!inQueue.contains(child) && !processed.contains(child)) {
                        queue.add(child);
                        inQueue.add(child);
                    }
                });
            }

            HashSet<PropertyUsageType> usages = loader.getCurrentPropertyUsages();

            loader.setInferredHierarchy(hierarchy);

            OWLPAreaTaxonomy inferredTaxonomy = loader.deriveCompleteInferredTaxonomy(usages);
            loader.setCurrentInferredTaxonomy(inferredTaxonomy);
            
            if(showTaxonomy) {
                graphFrame.replaceInternalFrameDataWith(inferredTaxonomy, ontology);
            }
        });
        
        loadThread.start();
    }

    protected void disposeOWLView() {
        OWLModelManager manager = getOWLModelManager();
        
        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}
