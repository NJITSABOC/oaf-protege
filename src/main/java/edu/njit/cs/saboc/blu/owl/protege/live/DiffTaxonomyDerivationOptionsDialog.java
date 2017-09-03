package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.owl.protege.live.manager.ProtegeLiveTaxonomyDataManager;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.wizard.OWLPAreaTaxonomyWizardPanel;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.wizard.OWLPAreaTaxonomyWizardPanel.OWLPAreaTaxonomyDerivationAction;
import edu.njit.cs.saboc.blu.owl.protege.LiveTaxonomyView;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import java.util.Optional;
import java.util.Set;
import javax.swing.JDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyDerivationOptionsDialog extends JDialog {
    
    private final Logger logger = LoggerFactory.getLogger(DiffTaxonomyDerivationOptionsDialog.class);
    
    private final OWLPAreaTaxonomyWizardPanel wizardPanel;
        
    private Optional<ProtegeLiveTaxonomyDataManager> optProtegeDataManager = Optional.empty();

    
    public DiffTaxonomyDerivationOptionsDialog(
            LiveTaxonomyView protegeTaxonomyView,
            OWLAbNFrameManager frameManager) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                    "DiffTaxonomyDerivationOptionsDialog", 
                    "Constructed"));
 
        OWLPAreaTaxonomyDerivationAction derivationAction = 
                (dataManager, 
                        root, 
                        typesAndUsages, 
                        availableProperties, 
                        selectedProperties) -> {
                    
            if(!optProtegeDataManager.isPresent()) {
                return;
            }
            
            ProtegeLiveTaxonomyDataManager protegeDataManager = optProtegeDataManager.get();
                    
            String debugMessage = String.format("root: %s, typesAndUsages: %s, availableProperties: %s, selectedProperties: %s",
                    root.getName(),
                    typesAndUsages.toString(), 
                    availableProperties.toString(),
                    selectedProperties.toString());
            
            logger.debug(LogMessageGenerator.createLiveDiffString("DiffTaxonomyDerivationOptionsDialog", 
                    String.format("OWLPAreaTaxonomyDerivationAction: %s", debugMessage)));
                    
            DerivationSettings settings = new DerivationSettings(
                    root, 
                    typesAndUsages, 
                    (Set<InheritableProperty>)(Set<?>)selectedProperties, 
                    (Set<InheritableProperty>)(Set<?>)availableProperties);
            
            protegeDataManager.getDiffTaxonomyManager().setDerivationSettings(settings);
            protegeDataManager.getDiffTaxonomyManager().reset();
            
            protegeTaxonomyView.updateTaxonomyData();
            protegeTaxonomyView.updateTaxonomyDisplay();

            this.setVisible(false);
            this.dispose();
        };
        
        // TODO: This doesn't propertly handle the removal of a property
        this.wizardPanel = new OWLPAreaTaxonomyWizardPanel(derivationAction, frameManager);

        this.add(wizardPanel);
        
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        this.setResizable(false);
        this.setSize(1200, 600);
        this.setModal(true);
        
        this.setLocationRelativeTo(null);
    }
    
    public void setCurrentDataManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.optProtegeDataManager = Optional.of(dataManager);
        
        this.wizardPanel.initialize(dataManager);
    }
    
    public void clearDataManager() {
        this.optProtegeDataManager = Optional.empty();
        this.wizardPanel.clearContents();
    }
}
