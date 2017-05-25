package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.DisjointAbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFRecentlyOpenedFileManager.RecentlyOpenedFileException;
import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.owl.abnhistory.OWLDerivationParser;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLFrameManagerAdapter;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLMultiAbNGraphFrameInitializers;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final long serialVersionUID = -4515710047558710080L;

    private final Map<OWLOntology, OAFOntologyDataManager> ontologyManagers = new HashMap<>();

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
        

        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);
    }
    
    
    private JPanel contentPanel;
    private MultiAbNGraphFrame graphFrame = null;
    
    
    private void initializeAbNView() {
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        URI physicalURI = getOWLModelManager().getOntologyPhysicalURI(ontology);
        
        if (!UIUtil.isLocalFile(physicalURI)) {
            
            // TODO: Error
            
            return;
        }
                
        if (!ontologyManagers.containsKey(ontology)) {
 
            OAFOntologyDataManager manager = new OAFOntologyDataManager(
                    stateFileManager,
                    ontologyManager, 
                    new File(physicalURI), 
                    ontology.getOntologyID().toString(), 
                    ontology);
            
            ontologyManagers.put(ontology, manager);
        }

        OAFOntologyDataManager dataManager = ontologyManagers.get(ontology);
        
        if(graphFrame == null) {
            createAbNView(dataManager);
        }
    }

    private void createAbNView(OAFOntologyDataManager dataManager) {
        
        OWLDerivationParser owlAbNDerivationParser = new OWLDerivationParser(dataManager);

        graphFrame = new MultiAbNGraphFrame(getMyFrame(),
                new OWLMultiAbNGraphFrameInitializers(
                        dataManager, 
                        new OWLFrameManagerAdapter(graphFrame), 
                        warningManager),
                owlAbNDerivationParser);
        
        this.contentPanel.add(graphFrame, BorderLayout.CENTER);
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
        OWLModelManager manager = getOWLModelManager();

        manager.removeListener(modelListener);
        manager.removeOntologyChangeListener(changeListener);
        manager.removeIOListener(ontologyIOListener);
    }
}
