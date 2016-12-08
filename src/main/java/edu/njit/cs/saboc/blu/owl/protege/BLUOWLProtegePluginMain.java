package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.graph.BluGraph;
import edu.njit.cs.saboc.blu.core.graph.pareataxonomy.diff.DiffPAreaBluGraph;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.SinglyRootedNodeLabelCreator;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.pareataxonomy.DiffTaxonomyPainter;
import java.awt.BorderLayout;
import java.util.HashSet;
import java.awt.Container;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import org.semanticweb.owlapi.model.*;

import edu.njit.cs.saboc.blu.owl.utils.owlproperties.*;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.*;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.diffpareataxonomy.OWLDiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLDisplayFrameListener;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.DiffDerivationTypeManager.DerivationType;
import edu.njit.cs.saboc.blu.owl.protege.DiffDerivationTypeManager.DerivationTypeChangedListener;
import edu.njit.cs.saboc.blu.owl.protege.gui.gep.widgets.DerivationSelectionWidget;
import edu.njit.cs.saboc.blu.owl.protege.gui.gep.widgets.ProtegeAbNExplorationPanel;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owlapi.change.AddAxiomData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeRecord;

public class BLUOWLProtegePluginMain extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;
    public static final Logger log = Logger.getLogger(BLUOWLProtegePluginMain.class);
    
    private final ProtegeAbNExplorationPanel explorationPanel = new ProtegeAbNExplorationPanel();
    
    private final HashMap<OWLOntology, ProtegeBLUOntologyDataManager> ontologyManagers = new HashMap<>();
    
    private final DiffDerivationTypeManager derivationTypeManager = new DiffDerivationTypeManager();

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
                    
                    break;

                case ONTOLOGY_CLASSIFIED:
                    
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

            updateStatedTaxonomy(true);
        }
    };

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

                SwingUtilities.invokeLater(() -> {
                    contentFrame.toFront();
                    contentFrame.repaint();
                });

            }
        };
        
        derivationTypeManager.addDerivationTypeChangedListener(new DerivationTypeChangedListener() {

            @Override
            public void derivationTypeChange(DerivationType newDerivationType) {
                updateStatedTaxonomy(true);
            }

            @Override
            public void resetFixedPointStart() {
                OWLOntology ontology = getOWLModelManager().getActiveOntology();

                ProtegeBLUOntologyDataManager dataManager = ontologyManagers.get(ontology);
                dataManager.setLastFixedPointTaxonomy(dataManager.getCurrentStatedTaxonomy());

                updateStatedTaxonomy(true);
            }
        });

        explorationPanel.getDisplayPanel().addWidget(
                new DerivationSelectionWidget(explorationPanel.getDisplayPanel(), derivationTypeManager));

        this.add(explorationPanel, BorderLayout.CENTER);

        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);

        initializeBLUOWL();

        log.info("BLUOWL STATUS: Initialized");
    }

    private void initializeBLUOWL() {

        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();

        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        if (!ontologyManagers.containsKey(ontology)) {
            ontologyManagers.put(ontology, new ProtegeBLUOntologyDataManager(ontologyManager, null, ontology.getOntologyID().toString(), ontology));
        }

        ProtegeBLUOntologyDataManager dataManager = ontologyManagers.get(ontology);

        Set<PropertyTypeAndUsage> usages = new HashSet<>();

        if (dataManager.getOntologyMetrics().totalOPWithDomainCount > 0) {
            usages.add(PropertyTypeAndUsage.OP_DOMAIN);
        } else if (dataManager.getOntologyMetrics().totalOPWithRestrictionCount > 0) {
            usages.add(PropertyTypeAndUsage.OP_DOMAIN);
        } else {
            // TODO: Figure out whats really available...
            usages.add(PropertyTypeAndUsage.OP_DOMAIN);
        }

        OWLPAreaTaxonomy toTaxonomy = dataManager.deriveCompleteStatedTaxonomy(usages);
        dataManager.setCurrentStatedTaxonomy(toTaxonomy);

        dataManager.setLastUpdatedTaxonomy(toTaxonomy);
        dataManager.setLastFixedPointTaxonomy(toTaxonomy);
        
        OWLDiffPAreaTaxonomy diffTaxonomy;
        
        if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
            diffTaxonomy = dataManager.deriveDiffFixedPoint(toTaxonomy);
        } else {
            diffTaxonomy = dataManager.deriveDiffLastUpdated(toTaxonomy);
        }

        displayDiffPAreaTaxonomy(diffTaxonomy);
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

            ProtegeBLUOntologyDataManager dataManager = ontologyManagers.get(ontology);         
            dataManager.refreshOntology();

            Set<PropertyTypeAndUsage> usages = dataManager.getCurrentPropertyUsages();

            OWLPAreaTaxonomy toTaxonomy = dataManager.deriveCompleteStatedTaxonomy(usages);           
            dataManager.setCurrentStatedTaxonomy(toTaxonomy);

            OWLDiffPAreaTaxonomy diffTaxonomy;
            
            if (derivationTypeManager.getDerivationType() == DerivationType.FixedPoint) {
                diffTaxonomy = dataManager.deriveDiffFixedPoint(toTaxonomy);
            } else {
                diffTaxonomy = dataManager.deriveDiffLastUpdated(toTaxonomy);
            }
            
            dataManager.setLastUpdatedTaxonomy(toTaxonomy);

            if (showTaxonomy) {
                displayDiffPAreaTaxonomy(diffTaxonomy);
            }
        });

        taxonomyUpdateThread.start();
    }
    
    private void displayDiffPAreaTaxonomy(OWLDiffPAreaTaxonomy diffTaxonomy) {
        OWLDiffPAreaTaxonomyConfigurationFactory configFactory = new OWLDiffPAreaTaxonomyConfigurationFactory();
        OWLDiffPAreaTaxonomyConfiguration config = configFactory.createConfiguration(diffTaxonomy, displayListener);
        
        BluGraph graph = new DiffPAreaBluGraph(
                getMyFrame(),
                diffTaxonomy,
                new SinglyRootedNodeLabelCreator(),
                config);
        
        graph.setBounds(0, 0, graph.getAbNWidth(), graph.getAbNHeight());
        
        explorationPanel.initialize(graph, config, new DiffTaxonomyPainter());
        
        explorationPanel.revalidate();
        explorationPanel.repaint();
    }

    protected void disposeOWLView() {
        OWLModelManager manager = getOWLModelManager();

        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}
