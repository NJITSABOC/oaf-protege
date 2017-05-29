package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.wizard.OWLPAreaTaxonomyWizardPanel;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.wizard.OWLPAreaTaxonomyWizardPanel.OWLPAreaTaxonomyDerivationAction;
import edu.njit.cs.saboc.blu.owl.protege.LiveTaxonomyView;
import java.util.Set;
import javax.swing.JDialog;

/**
 *
 * @author Chris Ochs
 */
public class DiffTaxonomyDerivationOptionsDialog extends JDialog {
    
    private final OWLPAreaTaxonomyWizardPanel wizardPanel;

    public DiffTaxonomyDerivationOptionsDialog(
            LiveTaxonomyView protegeTaxonomyView,
            ProtegeLiveTaxonomyDataManager protegeDataManager,
            OWLAbNFrameManager frameManager) {
        
        OWLPAreaTaxonomyDerivationAction derivationAction = 
                (dataManager, 
                        root, 
                        typesAndUsages, 
                        availableProperties, 
                        selectedProperties) -> {
                    
            DerivationSettings settings = new DerivationSettings(
                    root, 
                    typesAndUsages, 
                    (Set<InheritableProperty>)(Set<?>)selectedProperties, 
                    (Set<InheritableProperty>)(Set<?>)availableProperties);
            
                    protegeDataManager.getDiffTaxonomyManager().setDerivationSettings(settings);

            this.setVisible(false);
            this.dispose();
        };
        
        this.wizardPanel = new OWLPAreaTaxonomyWizardPanel(derivationAction, frameManager);
        this.wizardPanel.initialize(protegeDataManager);
        
        this.add(wizardPanel);
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.setResizable(false);
        this.setSize(1200, 600);
        this.setModal(true);
        
        this.setLocationRelativeTo(null);
        
        this.setVisible(true);
    }
    
}
