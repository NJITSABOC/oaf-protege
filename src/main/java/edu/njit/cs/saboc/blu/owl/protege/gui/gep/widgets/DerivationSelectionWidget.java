package edu.njit.cs.saboc.blu.owl.protege.gui.gep.widgets;

import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayWidget;
import edu.njit.cs.saboc.blu.owl.protege.DiffDerivationTypeManager;
import edu.njit.cs.saboc.blu.owl.protege.DiffDerivationTypeManager.DerivationType;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author cro3
 */
public class DerivationSelectionWidget extends AbNDisplayWidget {

    private final DiffDerivationTypeManager derivationTypeManager;
    
    private final Dimension panelSize = new Dimension(250, 50);
    
    private final JToggleButton btnUseInferredHierarchy;
    
    public DerivationSelectionWidget(
            AbNDisplayPanel displayPanel, 
            DiffDerivationTypeManager derivationTypeManager) {
        
        super(displayPanel);
        
        this.derivationTypeManager = derivationTypeManager;
        
        this.setLayout(new BorderLayout());
        
        JButton resetButton = new JButton("<html><div align='center'>Reset<br>Fixed<br>Point");
        resetButton.addActionListener( (ae) -> {
            derivationTypeManager.resetFixedPointDerivation();
        });
        
        this.add(resetButton, BorderLayout.WEST);
        
        JSlider derivationTypeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);
        derivationTypeSlider.setMajorTickSpacing(1);
        derivationTypeSlider.setPaintTicks(true);
        derivationTypeSlider.setPaintLabels(true);
        
        derivationTypeSlider.setPreferredSize(new Dimension(100, 50));
        
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
        
        this.add(derivationTypeSlider, BorderLayout.CENTER);
        
        this.btnUseInferredHierarchy = new JToggleButton("<html><div align='center'>Use Inferred<br>Hierarchy");
        this.btnUseInferredHierarchy.setEnabled(false);
        this.btnUseInferredHierarchy.addActionListener( (ae) -> {
            if(btnUseInferredHierarchy.isSelected()) {
                derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Inferred);
            } else {
                derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Stated);
            }
        });
        
        this.add(btnUseInferredHierarchy, BorderLayout.EAST);
    }
    
    public void setInferredHierarchyAvailable(boolean value) {
        SwingUtilities.invokeLater( () -> {
            btnUseInferredHierarchy.setEnabled(value);
        });
    }

    @Override
    public void displayPanelResized(AbNDisplayPanel displayPanel) {
        this.setBounds(10, displayPanel.getBounds().height - panelSize.height - 20, panelSize.width, panelSize.height);
    }
}
