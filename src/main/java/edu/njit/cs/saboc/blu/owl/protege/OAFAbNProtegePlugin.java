package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomyGenerator;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.DisjointAbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLPAreaTaxonomyFactory;
import edu.njit.cs.saboc.blu.owl.abnhistory.OWLDerivationParser;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLFrameManagerAdapter;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLMultiAbNGraphFrameInitializers;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
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

    private static final long serialVersionUID = -4515710047558710080L;

    private final Map<OWLOntology, ProtegeOAFOntologyDataManager> ontologyManagers = new HashMap<>();

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
                    this.ontologyAboutToClassify();
                break;

            case ABOUT_TO_CLASSIFY:
                    this.ontologyAboutToClassify();
                break;

            case ONTOLOGY_CLASSIFIED:
                    this.ontologyClassified();
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

    private final OWLOntologyChangeListener changeListener = (List<? extends OWLOntologyChange> changes) -> {
        
    };

    private final AbNWarningManager warningManager = new DisjointAbNWarningManager();
    private OAFStateFileManager stateFileManager = null;
        
    @Override
    protected void initialiseOWLView() throws Exception {
        
        setLayout(new BorderLayout());
        
        this.contentPanel = new JPanel(new BorderLayout());

        try {
             stateFileManager = new OAFStateFileManager("BLUOWL");
        } catch (RecentlyOpenedFileException rofe) {
            
        }
        
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
        
        if (!UIUtil.isLocalFile(physicalURI)) {

            JOptionPane.showMessageDialog(
                    null,
                    "<html>The OAF requires that an ontology be saved to, or opened from, a local file."
                    + "<p>Please save the current ontology to a file and reset the OAF view in Protege.",
                    "Save or open an ontology file",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }
                
        if (!ontologyManagers.containsKey(ontology)) {

            OAFOntologyDataManager manager = new OAFOntologyDataManager(
                    stateFileManager,
                    ontologyManager,
                    new File(physicalURI),
                    ontology.getOntologyID().toString(),
                    ontology);

            ontologyManagers.put(ontology, new ProtegeOAFOntologyDataManager(getOWLModelManager(), manager));
        }

        ProtegeOAFOntologyDataManager dataManager = ontologyManagers.get(ontology);
        
        if(graphFrame == null) {
            createAbNView(dataManager);
        }
        
        displayDefaultAbN(dataManager);
    }

    private void createAbNView(ProtegeOAFOntologyDataManager dataManager) {

        OWLMultiAbNGraphFrameInitializers initializers = new OWLMultiAbNGraphFrameInitializers(
                        dataManager,
                        new OWLFrameManagerAdapter(graphFrame),
                        warningManager);

        graphFrame = new MultiAbNGraphFrame(getMyFrame());
        graphFrame.setInitializers(initializers);

        this.contentPanel.add(graphFrame.getContentPane(), BorderLayout.CENTER);
        
        this.contentPanel.revalidate();
        this.contentPanel.repaint();
    }
    
    private void displayDefaultAbN(ProtegeOAFOntologyDataManager dataManager) {
        
        System.out.println("OAF Status: " + dataManager.inferredRelsAvailable());
        
        Hierarchy<OWLConcept> conceptHierarchy = dataManager.getOntology().getConceptHierarchy();
        
        Set<PropertyTypeAndUsage> usages = dataManager.getAvailablePropertyTypesInSubhierarchy(conceptHierarchy.getRoot());
        
        OWLPAreaTaxonomyFactory factory = new OWLPAreaTaxonomyFactory(dataManager, usages);

        PAreaTaxonomyGenerator taxonomyGenerator = new PAreaTaxonomyGenerator();
        PAreaTaxonomy taxonomy = taxonomyGenerator.derivePAreaTaxonomy(factory, conceptHierarchy);
        
        
        SwingUtilities.invokeLater( () -> {
            this.graphFrame.displayPAreaTaxonomy(taxonomy);
        });
    }
    
    private void ontologyAboutToClassify() {
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

        JOptionPane.showMessageDialog(
                null,
                "<html>The ontology has been classified. The OAF Class Browser"
                + "<p>will display the inferred hierarchy.",
                "Ontology Classified",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateCurrentOntology() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = ontologyManagers.get(ontology);
        currentOntologyDataManager.reinitialize();

        displayDefaultAbN(currentOntologyDataManager);
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
