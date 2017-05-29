package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayWidget;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.protege.LiveTaxonomyView;
import edu.njit.cs.saboc.blu.owl.protege.live.DiffDerivationTypeManager.DerivationType;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Chris Ochs
 */
public class DerivationSelectionWidget extends AbNDisplayWidget {

    private Optional<ProtegeLiveTaxonomyDataManager> optCurrentDataManager = Optional.empty();
    
    private final DiffDerivationTypeManager derivationTypeManager;
    
    private final Dimension panelSize = new Dimension(400, 60);
    
    private final JToggleButton btnUseInferredHierarchy;
    
    private final JButton btnDerivationOptions;
    
    public DerivationSelectionWidget(
            LiveTaxonomyView protegeTaxonomyView, 
            OWLAbNFrameManager frameManager,
            AbNDisplayPanel displayPanel, 
            DiffDerivationTypeManager derivationTypeManager) {
        
        super(displayPanel);
        
        this.derivationTypeManager = derivationTypeManager;

        JButton resetButton = new JButton("<html><div align='center'>Reset<br>Fixed Point");
        resetButton.addActionListener( (ae) -> {
            derivationTypeManager.resetFixedPointDerivation();
        });
        
        this.add(resetButton);
        
        JSlider derivationTypeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);
        derivationTypeSlider.setMajorTickSpacing(1);
        derivationTypeSlider.setPaintTicks(true);
        derivationTypeSlider.setPaintLabels(true);
        
        derivationTypeSlider.setPreferredSize(new Dimension(150, 50));
        
        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("Fixed Point"));
        labelTable.put(1, new JLabel("Progressive"));
        
        derivationTypeSlider.setLabelTable(labelTable);
        
        derivationTypeSlider.addChangeListener( (ce) -> {
            if(!derivationTypeSlider.getValueIsAdjusting()) {
                if(derivationTypeSlider.getValue() == 0) {
                    resetButton.setEnabled(true);
                    derivationTypeManager.setDerivationType(DerivationType.FixedPoint);
                } else {
                    resetButton.setEnabled(false);
                    derivationTypeManager.setDerivationType(DerivationType.Progressive);
                }
            }
        }); 
        
        this.add(derivationTypeSlider);
        
        this.btnUseInferredHierarchy = new JToggleButton("<html><div align='center'>Use Inferred<br>Hierarchy");
        this.btnUseInferredHierarchy.setEnabled(false);
        
        this.btnUseInferredHierarchy.addActionListener( (ae) -> {
            if(btnUseInferredHierarchy.isSelected()) {
                derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Inferred);
            } else {
                derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Stated);
            }
        });
        
        this.add(btnUseInferredHierarchy);
        
        this.btnDerivationOptions = new JButton("<html><div align='center'>Derivation<br>Options");
        this.btnDerivationOptions.addActionListener( (ae) -> {
            
            if(this.optCurrentDataManager.isPresent()) {
                
                 DiffTaxonomyDerivationOptionsDialog optionsDialog = 
                    new DiffTaxonomyDerivationOptionsDialog(
                            protegeTaxonomyView,
                            this.optCurrentDataManager.get(), 
                            frameManager);
            }
        });
        
        this.add(btnDerivationOptions);
        
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }
    
    public void setInferredHierarchyAvailable(boolean value) {
        
        if(value) {
            btnUseInferredHierarchy.setEnabled(true);
        } else {
            btnUseInferredHierarchy.setEnabled(false);
            btnUseInferredHierarchy.setSelected(false);
        }
    }

    @Override
    public void displayPanelResized(AbNDisplayPanel displayPanel) {
        
        this.setBounds(10, 
                displayPanel.getBounds().height - panelSize.height - 20, 
                panelSize.width, 
                panelSize.height);
    }
    
    public void setCurrentDataManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.optCurrentDataManager = Optional.of(dataManager);
    }
}