package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.diff.change.ChangeState;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.diff.DiffPAreaTaxonomyGraph;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.SinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.pareataxonomy.DiffTaxonomyPainter;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLInheritableProperty;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSelectionWidget;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager.DerivationType;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager.DerivationTypeChangedListener;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager.RelationshipType;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.LiveDiffTaxonomyManager;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeDiffTaxonomyExplorationPanel;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.ProtegeLiveTaxonomyDataManager;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class LiveTaxonomyView extends AbstractOWLViewComponent {
    
    private final Logger logger = LoggerFactory.getLogger(LiveTaxonomyView.class);
    
    private final OAFStateFileManager stateFileManager = new OAFStateFileManager("BLUOWL");

    private final Map<OWLOntology, ProtegeLiveTaxonomyDataManager> ontologyManagers = new HashMap<>();

    private final DiffDerivationTypeManager derivationTypeManager = new DiffDerivationTypeManager();

    private ProtegeDiffTaxonomyExplorationPanel explorationPanel;
    
    private DerivationSelectionWidget derivationSelectionWidget;
    
    private boolean reasonerRunning = false;

    private final OWLModelManagerListener modelListener = (event) -> {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "ModelManagerListener", 
                String.format("Event: %s", event.getType())));

        switch (event.getType()) {

            case REASONER_CHANGED:
                this.reasonerRunning = false;
                
                handleReasonerStopped();
                
                break;

            case ABOUT_TO_CLASSIFY:
                derivationSelectionWidget.setInferredHierarchyAvailable(false);
                
                break;

            case ONTOLOGY_CLASSIFIED:

                if(reasonerRunning) {
                    
                    SwingUtilities.invokeLater(() -> {
                        derivationSelectionWidget.clearInferredTaxonomyDirty();
                        updateTaxonomyData();
                        updateTaxonomyDisplay();
                    });
                    
                } else {
                    reasonerRunning = true;
                    handleOntologyReasoned();
                }

                break;

                
            case ACTIVE_ONTOLOGY_CHANGED:
            case ONTOLOGY_VISIBILITY_CHANGED:
            case ENTITY_RENDERER_CHANGED:
            case ENTITY_RENDERING_CHANGED:
            case ONTOLOGY_CREATED:
            case ONTOLOGY_LOADED:
            case ONTOLOGY_RELOADED:
            case ONTOLOGY_SAVED:

            default:
        }
    };

    private final IOListener ontologyIOListener = new IOListener() {

        @Override
        public void beforeSave(IOListenerEvent iole) {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "IOListener - beforeSave", ""));
        }

        @Override
        public void afterSave(IOListenerEvent iole) {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "IOListener - afterSave", ""));
        }

        @Override
        public void beforeLoad(IOListenerEvent iole) {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "IOListener - beforeLoad", ""));
        }

        @Override
        public void afterLoad(IOListenerEvent iole) {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "IOListener - afterLoad", ""));
        }
    };

    private final OWLOntologyChangeListener changeListener = (changes) -> {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "OntologyChangeListener", 
                String.format("reasonerRunner: %s", Boolean.toString(reasonerRunning))));
        
        if(reasonerRunning) {

            SwingUtilities.invokeLater( () -> {
                derivationSelectionWidget.setInferredTaxonomyDirty();
            });
        }
        
        updateTaxonomyData();
        updateTaxonomyDisplay();
    };

    private OWLAbNFrameManager displayManager;

    @Override
    protected void initialiseOWLView() throws Exception {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "initialiseOWLView", 
                ""));

        setLayout(new BorderLayout());

        displayManager = new OWLAbNFrameManager(
                getMyFrame(), 
                stateFileManager, 
                (frame) -> {
            
            Container contentPane = frame.getContentPane();

            JFrame contentFrame = new JFrame();

            contentFrame.setTitle("Ontology Abstraction Framework (OAF) "
                    + "Live Diff Partial-area Taxonomy");

            contentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            contentFrame.setBounds(new Rectangle(0, 0, 1200, 800));

            contentFrame.add(contentPane);

            contentFrame.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                contentFrame.toFront();
                contentFrame.repaint();
            });
        });

        derivationTypeManager.addDerivationTypeChangedListener(new DerivationTypeChangedListener() {

            @Override
            public void derivationTypeChanged(DerivationType newDerivationType) {
                updateTaxonomyDisplay();
            }

            @Override
            public void relationshipTypeChanged(RelationshipType version) {
                updateTaxonomyDisplay();
            }

            @Override
            public void resetFixedPointStart() {
               resetFixedPoint();
            }
        });
        
        
        getOWLWorkspace().getOWLSelectionModel().addListener( () -> {
            
            if(explorationPanel.getDisplayPanel().getGraph() == null) {
                return;
            }
            
            OWLEntity selectedEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
            
            if (selectedEntity != null && selectedEntity.isOWLClass()) {

                OWLOntology ontology = getOWLModelManager().getActiveOntology();

                ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);

                OWLClass cls = selectedEntity.asOWLClass();
                OWLConcept concept = currentOntologyDataManager.getOntology().getOWLConceptFor(cls);
                
                DiffPAreaTaxonomy diffTaxonomy = 
                        (DiffPAreaTaxonomy)explorationPanel.getDisplayPanel().getGraph().getAbstractionNetwork();
                
                Set<DiffPArea> nodes = diffTaxonomy.getNodesWith(concept);
                
                if(nodes.isEmpty()) {
                    return;
                }
                
                DiffPArea node;
                
                if(nodes.size() == 1) {
                    node = nodes.iterator().next();
                } else {
                    
                    Optional<DiffPArea> optIntroducedNode = nodes.stream().filter( (parea) -> {
                        return parea.getPAreaState() == ChangeState.Introduced;
                    }).findAny();
                    
                    if(!optIntroducedNode.isPresent()) {
                        return;
                    }
                    
                    node = optIntroducedNode.get();
                }
                
                SinglyRootedNodeEntry entry = explorationPanel.getDisplayPanel().
                            getGraph().getNodeEntries().get(node);
                
                explorationPanel.getDisplayPanel().getAutoScroller().autoNavigateToNodeEntry(entry);
            }
        });
        
        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);

        initializeOAFView();
    }
    
    private void initializeOAFView() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "initialiseOAFView",
                ""));
        
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        // TODO: As of right now the plugin can't handle changing ontologies within the same 
        // view... Need to change ProtegeDiffTaxonomyExplorationPanel to no take
        // diffManager as argument.
        
        if (!ontologyManagers.containsKey(ontology)) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "initialiseOAFView",
                    String.format("Creating new ontology manager entry: %s", ontology.getOntologyID().toString())));
            
            OAFOntologyDataManager dataManager = new OAFOntologyDataManager(
                    null,
                    ontologyManager,
                    null,
                    ontology.getOntologyID().toString(),
                    ontology);

            ontologyManagers.put(ontology, 
                    new ProtegeLiveTaxonomyDataManager(
                            this.getOWLModelManager(),
                            dataManager)
                    );
        }

        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        LiveDiffTaxonomyManager diffManager = dataManager.getDiffTaxonomyManager();
        
        this.explorationPanel = new ProtegeDiffTaxonomyExplorationPanel(derivationTypeManager, diffManager);
        this.explorationPanel.getDisplayPanel().addWidget(derivationSelectionWidget);

        this.derivationSelectionWidget = new DerivationSelectionWidget(
                this,
                displayManager,
                explorationPanel.getDisplayPanel(), 
                derivationTypeManager);
        
        this.derivationSelectionWidget.setCurrentDataManager(dataManager);

        this.add(explorationPanel, BorderLayout.CENTER);

        diffManager.setDerivationSettings(createDefaultSettings(dataManager));
        diffManager.initialize();

        OWLDiffPAreaTaxonomy diffTaxonomy;

        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
            diffTaxonomy = diffManager.getStatedDiffTaxonomyManager().deriveFixedPointDiffTaxonomy();
        } else {
            diffTaxonomy = diffManager.getStatedDiffTaxonomyManager().deriveProgressiveDiffTaxonomy();
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
    }
    
    private void handleReasonerStopped() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "handleReasonerStopped",
                ""));

        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        
        dataManager.setInferredRelsAvailable(false);
        dataManager.getDiffTaxonomyManager().setInferredRelsAvailable(false);
        
        derivationSelectionWidget.setInferredHierarchyAvailable(false);
        derivationTypeManager.setInferredHierarchyAvailable(false);
    }

    private void handleOntologyReasoned() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "handleOntologyReasoned",
                ""));
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        
        OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
        
        if (reasoner.isConsistent()) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "handleOntologyReasoned",
                "Reasoner consistent"));
            
            derivationSelectionWidget.setInferredHierarchyAvailable(true);
            derivationTypeManager.setInferredHierarchyAvailable(true);
            
            dataManager.setInferredRelsAvailable(true);
            dataManager.getDiffTaxonomyManager().setInferredRelsAvailable(true);
        } else {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "handleOntologyReasoned",
                    "Reasoner inconsistent"));
            
            derivationSelectionWidget.setInferredHierarchyAvailable(false);
            derivationTypeManager.setInferredHierarchyAvailable(false);
        }
    }
    
    private DerivationSettings createDefaultSettings(ProtegeLiveTaxonomyDataManager dataManager) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "createDefaultSettings",
                String.format("Creating default derivation settings for: %s", 
                        dataManager.getSourceOntology().getOntologyID().toString())));
        
        OWLConcept root = dataManager.getOntology().getConceptHierarchy().getRoot();
        
        Set<PropertyTypeAndUsage> usages = new HashSet<>();

        usages.add(PropertyTypeAndUsage.OP_RESTRICTION);
        usages.add(PropertyTypeAndUsage.OP_EQUIV);
        
        Set<OWLInheritableProperty> properties = dataManager.getPropertiesInSubhierarchy(root, usages);
        
        return new DerivationSettings(
                root, 
                usages, 
                (Set<InheritableProperty>)(Set<?>)properties, 
                (Set<InheritableProperty>)(Set<?>)properties);
    }

    private JFrame getMyFrame() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "getMyFrame",
                ""));

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
    
    public void resetFixedPoint() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "resetFixedPoint",
                ""));
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        LiveDiffTaxonomyManager diffManager = dataManager.getDiffTaxonomyManager();

        diffManager.reset();
        
        updateTaxonomyData();
        updateTaxonomyDisplay();
    }
    
    public void updateTaxonomyData() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "updateTaxonomyData",
                ""));
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        
        dataManager.refreshOntology();
        dataManager.getDiffTaxonomyManager().update();
    }

    public void updateTaxonomyDisplay() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "updateTaxonomyDisplay",
                ""));
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
                
        boolean useStated = derivationTypeManager.getRelationshipType().equals(
                RelationshipType.Stated);
        
        if(useStated) {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateTaxonomyDisplay",
                    "Use stated hierarchy"));
            
            updateStatedTaxonomy(dataManager.getDiffTaxonomyManager());
        } else {
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateTaxonomyDisplay",
                    "Use inferred hierarchy"));

            updateInferredTaxonomy(dataManager.getDiffTaxonomyManager());
        }
    }

    private void updateStatedTaxonomy(LiveDiffTaxonomyManager manager) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "updateStatedTaxonomy",
                ""));

        OWLDiffPAreaTaxonomy diffTaxonomy;

        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {

            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateStatedTaxonomy",
                    "Fixed Point"));
            
            diffTaxonomy = manager.getStatedDiffTaxonomyManager().deriveFixedPointDiffTaxonomy();
            
        } else {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateStatedTaxonomy",
                    "Progressive"));
            
            diffTaxonomy = manager.getStatedDiffTaxonomyManager().deriveProgressiveDiffTaxonomy();
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
    }

    private void updateInferredTaxonomy(LiveDiffTaxonomyManager manager) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "updateInferredTaxonomy",
                ""));

        OWLDiffPAreaTaxonomy diffTaxonomy;

        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateInferredTaxonomy",
                    "Fixed Point"));
            
            diffTaxonomy = manager.getInferredTaxonomyManager().deriveFixedPointDiffTaxonomy();
            
        } else {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "updateInferredTaxonomy",
                    "Progressive"));
            
            diffTaxonomy = manager.getInferredTaxonomyManager().deriveProgressiveDiffTaxonomy();
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
    }

    private void displayDiffPAreaTaxonomy(OWLDiffPAreaTaxonomy diffTaxonomy) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "displayDiffPAreaTaxonomy",
                ""));
        
        SwingUtilities.invokeLater(() -> {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                "displayDiffPAreaTaxonomy",
                "Displaying taxonomy"));
            
            ProtegeDiffPAreaTaxonomyConfigurationFactory configFactory = 
                    new ProtegeDiffPAreaTaxonomyConfigurationFactory();
            
            ProtegeDiffPAreaTaxonomyConfiguration config = configFactory.createConfiguration(
                    diffTaxonomy, 
                    this.getOWLWorkspace(), 
                    displayManager);

            AbstractionNetworkGraph graph = new DiffPAreaTaxonomyGraph(
                    getMyFrame(),
                    diffTaxonomy,
                    new SinglyRootedNodeLabelCreator(),
                    config);

            graph.setBounds(0, 0, graph.getAbNWidth(), graph.getAbNHeight());

            explorationPanel.initialize(graph, config, new DiffTaxonomyPainter());

            explorationPanel.revalidate();
            explorationPanel.repaint();
        });
    }

    @Override
    protected void disposeOWLView() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "disposeOWLView",
                ""));
        
        OWLModelManager manager = getOWLModelManager();

        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}