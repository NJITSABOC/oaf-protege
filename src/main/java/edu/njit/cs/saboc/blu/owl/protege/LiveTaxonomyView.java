package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.diff.DiffPAreaTaxonomyGraph;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.SinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.pareataxonomy.DiffTaxonomyPainter;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLInheritableProperty;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSelectionWidget;
import edu.njit.cs.saboc.blu.owl.protege.live.DerivationSettings;
import edu.njit.cs.saboc.blu.owl.protege.live.DiffDerivationTypeManager;
import edu.njit.cs.saboc.blu.owl.protege.live.DiffDerivationTypeManager.DerivationType;
import edu.njit.cs.saboc.blu.owl.protege.live.DiffDerivationTypeManager.DerivationTypeChangedListener;
import edu.njit.cs.saboc.blu.owl.protege.live.DiffDerivationTypeManager.RelationshipType;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.LiveDiffTaxonomyManager;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeAbNExplorationPanel;
import edu.njit.cs.saboc.blu.owl.protege.live.ProtegeLiveTaxonomyDataManager;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 *
 * @author Chris Ochs
 */
public class LiveTaxonomyView extends AbstractOWLViewComponent {

    private final ProtegeAbNExplorationPanel explorationPanel = new ProtegeAbNExplorationPanel();

    private final HashMap<OWLOntology, ProtegeLiveTaxonomyDataManager> ontologyManagers = new HashMap<>();

    private final DiffDerivationTypeManager derivationTypeManager = new DiffDerivationTypeManager();

    private DerivationSelectionWidget derivationSelectionWidget;
    
    private boolean reasonerRunning = false;

    private final OWLModelManagerListener modelListener = (event) -> {

        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:

                break;

            case ONTOLOGY_VISIBILITY_CHANGED:
            case ENTITY_RENDERER_CHANGED:
            case ENTITY_RENDERING_CHANGED:
                break;

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
                        updateTaxonomyDisplay();
                    });
                } else {
                    reasonerRunning = true;
                    handleOntologyReasoned();
                }

                break;

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

    private final OWLOntologyChangeListener changeListener = (changes) -> {
        
        if(reasonerRunning) {
            SwingUtilities.invokeLater( () -> {
                derivationSelectionWidget.setInferredTaxonomyDirty();
            });
        }
        
        updateTaxonomyDisplay();
    };

    private OWLAbNFrameManager displayManager;

    @Override
    protected void initialiseOWLView() throws Exception {
        
        setLayout(new BorderLayout());

        displayManager = new OWLAbNFrameManager(getMyFrame(), (frame) -> {
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

        this.derivationSelectionWidget = new DerivationSelectionWidget(
                this,
                displayManager,
                explorationPanel.getDisplayPanel(), 
                derivationTypeManager);

        explorationPanel.getDisplayPanel().addWidget(derivationSelectionWidget);

        this.add(explorationPanel, BorderLayout.CENTER);

        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);

        initializeOAFView();
    }
    
    private void initializeOAFView() {
        
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        if (!ontologyManagers.containsKey(ontology)) {
            
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

        derivationSelectionWidget.setCurrentDataManager(dataManager);
        
        LiveDiffTaxonomyManager diffManager = dataManager.getDiffTaxonomyManager();
        
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
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        
        dataManager.setInferredRelsAvailable(false);
        dataManager.getDiffTaxonomyManager().setInferredRelsAvailable(false);
        
        derivationSelectionWidget.setInferredHierarchyAvailable(false);
        derivationTypeManager.setInferredHierarchyAvailable(false);
        
    }

    private void handleOntologyReasoned() {
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        
        OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
        
        if (reasoner.isConsistent()) {
            derivationSelectionWidget.setInferredHierarchyAvailable(true);
            derivationTypeManager.setInferredHierarchyAvailable(true);
            
            dataManager.setInferredRelsAvailable(true);
            dataManager.getDiffTaxonomyManager().setInferredRelsAvailable(true);
        } else {
            derivationSelectionWidget.setInferredHierarchyAvailable(false);
            derivationTypeManager.setInferredHierarchyAvailable(false);
        }
    }
    
    private DerivationSettings createDefaultSettings(ProtegeLiveTaxonomyDataManager dataManager) {
        
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
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        LiveDiffTaxonomyManager diffManager = dataManager.getDiffTaxonomyManager();

        diffManager.reset();
        
        updateTaxonomyDisplay();
    }

    public void updateTaxonomyDisplay() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeLiveTaxonomyDataManager dataManager = ontologyManagers.get(ontology);
        dataManager.refreshOntology();
        
        dataManager.getDiffTaxonomyManager().update();
        
        boolean useStated = derivationTypeManager.getRelationshipType().equals(
                RelationshipType.Stated);
        
        if(useStated) {
            updateStatedTaxonomy(dataManager.getDiffTaxonomyManager());
        } else {
            updateInferredTaxonomy(dataManager.getDiffTaxonomyManager());
        }
    }

    private void updateStatedTaxonomy(LiveDiffTaxonomyManager manager) {

        OWLDiffPAreaTaxonomy diffTaxonomy;

        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
            diffTaxonomy = manager.getStatedDiffTaxonomyManager().deriveFixedPointDiffTaxonomy();
        } else {
            diffTaxonomy = manager.getStatedDiffTaxonomyManager().deriveProgressiveDiffTaxonomy();
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
    }

    private void updateInferredTaxonomy(LiveDiffTaxonomyManager manager) {

        OWLDiffPAreaTaxonomy diffTaxonomy;

        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
            diffTaxonomy = manager.getInferredTaxonomyManager().deriveFixedPointDiffTaxonomy();
        } else {
            diffTaxonomy = manager.getInferredTaxonomyManager().deriveProgressiveDiffTaxonomy();
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
    }

    private void displayDiffPAreaTaxonomy(OWLDiffPAreaTaxonomy diffTaxonomy) {
        
        SwingUtilities.invokeLater(() -> {
            OWLDiffPAreaTaxonomyConfigurationFactory configFactory = new OWLDiffPAreaTaxonomyConfigurationFactory();
            OWLDiffPAreaTaxonomyConfiguration config = configFactory.createConfiguration(diffTaxonomy, displayManager);

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
        OWLModelManager manager = getOWLModelManager();

        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}