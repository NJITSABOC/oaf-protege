package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.AbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.provenance.PAreaTaxonomyDerivation;
import edu.njit.cs.saboc.blu.core.abn.provenance.AbNDerivation;
import edu.njit.cs.saboc.blu.core.abn.targetbased.provenance.TargetAbNDerivation;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.DisjointAbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.history.AbNDerivationHistory;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.owl.abn.OWLLiveAbNFactory;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLFrameManagerAdapter;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class OAFAbNProtegePlugin extends AbstractOWLViewComponent {

    private final Map<OWLOntology, ProtegeOAFOntologyDataManager> ontologyManagers = new HashMap<>();
    
    private boolean firstSave = true;

    private final OWLModelManagerListener modelListener = (event) -> {

        OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();

        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
                initializeAbNView();
                break;

            case ONTOLOGY_VISIBILITY_CHANGED:
            case ENTITY_RENDERER_CHANGED:
            case ENTITY_RENDERING_CHANGED:
                break;

            case REASONER_CHANGED:
                    this.ontologyUnclassified();
                break;

            case ABOUT_TO_CLASSIFY:
                break;

            case ONTOLOGY_CLASSIFIED:
                this.ontologyClassified();
                
                break;

            case ONTOLOGY_SAVED:
                
                if(firstSave) {
                    initializeAbNView();
                    firstSave = false;
                }
                
                break;
                
            case ONTOLOGY_CREATED:
            case ONTOLOGY_LOADED:
            case ONTOLOGY_RELOADED:

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

    private final OWLOntologyChangeListener changeListener = 
            (List<? extends OWLOntologyChange> changes) -> {

        this.updateCurrentOntology();
    };

    private final AbNWarningManager warningManager = new DisjointAbNWarningManager();
    private final OAFStateFileManager stateFileManager = new OAFStateFileManager("BLUOWL");
        
    @Override
    protected void initialiseOWLView() throws Exception {
        
        setLayout(new BorderLayout());
        
        this.contentPanel = new JPanel(new BorderLayout());

        this.add(contentPanel, BorderLayout.CENTER);

        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);
        
        initializeAbNView();
    }

    private JPanel contentPanel;
    private MultiAbNGraphFrame graphFrame = null;

    private void initializeAbNView() {
        
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        URI physicalURI = getOWLModelManager().getOntologyPhysicalURI(ontology);
        
        if (!ontologyManagers.containsKey(ontology)) {

            OAFOntologyDataManager manager = new OAFOntologyDataManager(
                    stateFileManager,
                    ontologyManager,
                    null,
                    ontology.getOntologyID().toString(),
                    ontology);

            ontologyManagers.put(ontology, new ProtegeOAFOntologyDataManager(getOWLModelManager(), manager));
        }
        
        ProtegeOAFOntologyDataManager dataManager = ontologyManagers.get(ontology);
        
        if (dataManager.getOntologyFile() == null && UIUtil.isLocalFile(physicalURI)) {
            File ontologyFile = new File(physicalURI);

            dataManager.setOntologyFile(ontologyFile);
        }

        if(graphFrame == null) {
            createAbNView(dataManager);
            displayDefaultAbN(dataManager);
        } else {
            refreshCurrentView();
        }
    }

    private void createAbNView(ProtegeOAFOntologyDataManager dataManager) {

        graphFrame = new MultiAbNGraphFrame(getMyFrame(), stateFileManager);
        graphFrame.setInitializers(createInitializers(dataManager));

        this.contentPanel.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
    }
    
    boolean firstAbN = true;
    
    private void displayDefaultAbN(ProtegeOAFOntologyDataManager dataManager) {
        
        Hierarchy<OWLConcept> conceptHierarchy = dataManager.getOntology().getConceptHierarchy();
        
        Set<PropertyTypeAndUsage> usages = dataManager.getAvailablePropertyTypesInSubhierarchy(conceptHierarchy.getRoot());
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(dataManager, usages);

        PAreaTaxonomyGenerator taxonomyGenerator = new PAreaTaxonomyGenerator();
        PAreaTaxonomy taxonomy = taxonomyGenerator.derivePAreaTaxonomy(factory, conceptHierarchy);
        
        this.graphFrame.displayPAreaTaxonomy(taxonomy, firstAbN);
        
        this.firstAbN = false;
    }
    
    private void ontologyUnclassified() {
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);
        currentOntologyDataManager.setInferredRelsAvailable(false);

        updateCurrentOntology();
    }

    private void ontologyClassified() {
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);
        currentOntologyDataManager.setInferredRelsAvailable(true);

        updateCurrentOntology();
    }
    
    private void updateCurrentOntology() {
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);
        currentOntologyDataManager.reinitialize();

        this.graphFrame.setInitializers(createInitializers(currentOntologyDataManager));

        refreshCurrentView();
    }
    
    private void refreshCurrentView() {
        
        OWLOntology ontology = getOWLModelManager().getActiveOntology();
        ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);

        reinitializeAllActiveFactories();
        
        AbstractionNetwork abn = graphFrame.getAbNExplorationPanel().getDisplayPanel().getGraph().getAbstractionNetwork();        
        AbstractionNetwork<?> newAbN = abn.getDerivation().getAbstractionNetwork(currentOntologyDataManager.getOntology());

        graphFrame.displayAbstractionNetwork(newAbN, false);
    }
    
    private void reinitializeAllActiveFactories() {
        AbNDerivationHistory history = graphFrame.getDerivationHistory();

        history.getHistory().forEach((historyEntry) -> {
            AbNDerivation derivation = historyEntry.getDerivation();

            if (derivation instanceof PAreaTaxonomyDerivation) {

                PAreaTaxonomyDerivation pareaDerivation = (PAreaTaxonomyDerivation) derivation;
                ((OWLLiveAbNFactory) pareaDerivation.getFactory()).reinitialize();

            } else if (derivation instanceof TargetAbNDerivation) {

                TargetAbNDerivation targetDerivation = (TargetAbNDerivation) derivation;
                ((OWLLiveAbNFactory) targetDerivation.getFactory()).reinitialize();
            }

        });
    }

    private ProtegeAbNInitializers createInitializers(ProtegeOAFOntologyDataManager dataManager) {
        
        ProtegeAbNInitializers initializers = new ProtegeAbNInitializers(
                        dataManager,
                        this.getOWLWorkspace(),
                        new OWLFrameManagerAdapter(graphFrame),
                        warningManager);
        
        return initializers;
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

    @Override
    protected void disposeOWLView() {
        OWLModelManager manager = getOWLModelManager();

        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}
