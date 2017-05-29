package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.utils.toolstate.OAFStateFileManager;
import edu.njit.cs.saboc.blu.owl.nat.OWLNATLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;


import edu.njit.cs.saboc.blu.owl.ontology.OAFOntologyDataManager;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.nat.generic.NATBrowserPanel;
import edu.njit.cs.saboc.nat.generic.workspace.NATWorkspace;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

public class OAFProtegeNAT extends AbstractOWLViewComponent {

    private static final long serialVersionUID = -4515710047558710080L;

    private final OWLModelManagerListener modelListener = (event) -> {

        OWLReasoner reasoner = getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
        
        switch (event.getType()) {
            case ACTIVE_ONTOLOGY_CHANGED:
                displayCurrentOntology();
                break;

            case REASONER_CHANGED:
            case ABOUT_TO_CLASSIFY:
                ontologyAboutToClassify();
                break;

            case ONTOLOGY_CLASSIFIED:
                ontologyClassified();
                break;


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
        updateCurrentOntology();
    };
    
    private final Map<OWLOntology, ProtegeOAFOntologyDataManager> oafOntologyManagers = new HashMap<>();
    private final Map<OWLOntology, NATWorkspace> oafNATWorkspaces = new HashMap<>();
    
    private NATBrowserPanel<OWLConcept> natBrowserPanel;
    
    private OAFStateFileManager stateFileManager = null;

    @Override
    protected void initialiseOWLView() throws Exception {
        
        setLayout(new BorderLayout());
        
        this.stateFileManager = new OAFStateFileManager("BLUOWL");
        
        natBrowserPanel = new NATBrowserPanel<>(getMyFrame(), new OWLNATLayout());
        natBrowserPanel.setEnabled(false);
                
        this.add(natBrowserPanel, BorderLayout.CENTER);
        

        getOWLModelManager().addListener(modelListener);
        getOWLModelManager().addOntologyChangeListener(changeListener);
        getOWLModelManager().addIOListener(ontologyIOListener);
        
        
        System.out.println("Init listener...");
        
        getOWLWorkspace().getOWLSelectionModel().addListener( () -> {
            
            OWLEntity selectedEntity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
            
            System.out.println("Listener here...");
            
            if (selectedEntity != null && selectedEntity.isOWLClass()) {

                System.out.println("Listener triggered...");
                
                OWLOntology ontology = getOWLModelManager().getActiveOntology();

                ProtegeOAFOntologyDataManager currentOntologyDataManager = oafOntologyManagers.get(ontology);

                OWLClass cls = selectedEntity.asOWLClass();
                OWLConcept concept = currentOntologyDataManager.getOntology().getOWLConceptFor(cls);
                
                natBrowserPanel.navigateTo(concept);
            }
        });
        
        displayCurrentOntology();
    }

    private void ontologyAboutToClassify() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = oafOntologyManagers.get(ontology);
        currentOntologyDataManager.setInferredRelsAvailable(false);
        
        updateCurrentOntology();
    }
    
    private void ontologyClassified() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = oafOntologyManagers.get(ontology);
        currentOntologyDataManager.setInferredRelsAvailable(true);

        updateCurrentOntology();
    }
    
    private void updateCurrentOntology() {
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        ProtegeOAFOntologyDataManager currentOntologyDataManager = oafOntologyManagers.get(ontology);
        currentOntologyDataManager.reinitialize();
        
        displayCurrentOntology();
    }

    private void displayCurrentOntology() {
        
        OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ontology = getOWLModelManager().getActiveOntology();

        URI physicalURI = getOWLModelManager().getOntologyPhysicalURI(ontology);

        natBrowserPanel.reset();

        if (UIUtil.isLocalFile(physicalURI)) {
            File ontologyFile = new File(physicalURI);
            
            if (!oafOntologyManagers.containsKey(ontology)) {
                
                OAFOntologyDataManager dataManager = new OAFOntologyDataManager(
                                stateFileManager,
                                ontologyManager,
                                ontologyFile,
                                ontology.getOntologyID().toString(),
                                ontology);
                
                oafOntologyManagers.put(ontology, new ProtegeOAFOntologyDataManager(getOWLModelManager(), dataManager));
            }
            
            
            SwingUtilities.invokeLater(() -> {
                ProtegeOAFOntologyDataManager currentOntologyDataManager = oafOntologyManagers.get(ontology);
                
                natBrowserPanel.setDataSource(currentOntologyDataManager.getClassBrowserDataSource());

                if (oafNATWorkspaces.containsKey(currentOntologyDataManager.getSourceOntology())) {

                    NATWorkspace<OWLConcept> workspace = oafNATWorkspaces.get(currentOntologyDataManager.getSourceOntology());

                    natBrowserPanel.setWorkspace(workspace);

                } else {
                    natBrowserPanel.getFocusConceptManager().navigateToRoot();

                    NATWorkspace workspace = NATWorkspace.createNewWorkspaceFromCurrent(
                            natBrowserPanel,
                            null,
                            String.format("%s Default Workspace", currentOntologyDataManager.getOntologyFile().getName()));

                    oafNATWorkspaces.put(currentOntologyDataManager.getSourceOntology(), workspace);

                    natBrowserPanel.setWorkspace(workspace);
                }

                natBrowserPanel.setEnabled(true);
            });

        } else {
            
            JOptionPane.showMessageDialog(
                    null,
                    "<html>The OAF Class Browser requires that the ontology be saved to, or opened from, a local file."
                            + "<p>Please save the current ontology to a file and reset the NAT view in Protege.", 
                    "Save or open an ontology file", 
                    JOptionPane.ERROR_MESSAGE);
            
             natBrowserPanel.reset();
             natBrowserPanel.setEnabled(false);
        }
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
