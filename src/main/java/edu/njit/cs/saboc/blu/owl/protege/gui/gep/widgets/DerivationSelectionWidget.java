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

/**
 *
 * @author cro3
 */
public class DerivationSelectionWidget extends AbNDisplayWidget {

    private final DiffDerivationTypeManager derivationTypeManager;
    
    private final Dimension panelSize = new Dimension(170, 50);
    
    public DerivationSelectionWidget(
            AbNDisplayPanel displayPanel, 
            DiffDerivationTypeManager derivationTypeManager) {
        
        super(displayPanel);
        
        this.derivationTypeManager = derivationTypeManager;
        
        this.setLayout(new BorderLayout());
        
        JButton resetButton = new JButton("<html><div align='center'>Set<br>Fixed<br>Point");
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
    }

    @Override
    public void displayPanelResized(AbNDisplayPanel displayPanel) {
        this.setBounds(10, displayPanel.getBounds().height - panelSize.height - 20, panelSize.width, panelSize.height);
    }
}
