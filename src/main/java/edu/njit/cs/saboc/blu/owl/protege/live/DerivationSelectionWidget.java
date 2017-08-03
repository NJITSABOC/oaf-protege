package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.ProtegeLiveTaxonomyDataManager;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayWidget;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.protege.LiveTaxonomyView;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager.DerivationType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Chris Ochs
 */
public class DerivationSelectionWidget extends AbNDisplayWidget {

    private Optional<ProtegeLiveTaxonomyDataManager> optCurrentDataManager = Optional.empty();
    
    private final DiffDerivationTypeManager derivationTypeManager;
    
    private final Dimension NO_WARNING_SIZE = new Dimension(550, 40);
    private final Dimension WARNING_SIZE = new Dimension(550, 70);

    private Dimension panelSize = NO_WARNING_SIZE;
    
    private final JButton btnResetFixedPoint;
    
    private final JToggleButton btnUseFixedPoint;
    private final JToggleButton btnUseProgressive;
    
    private final JToggleButton btnUseStatedHierarchy;
    private final JToggleButton btnUseInferredHierarchy;
    
    private final JButton btnDerivationOptions;
    
    private final JLabel lblRefreshInferred;
    
    public DerivationSelectionWidget(
            LiveTaxonomyView protegeTaxonomyView, 
            OWLAbNFrameManager frameManager,
            AbNDisplayPanel displayPanel, 
            DiffDerivationTypeManager derivationTypeManager) {
        
        super(displayPanel);
        
        this.setBorder(null);
        
        JPanel derivationPanel = new JPanel();
        derivationPanel.setBorder(null);
        
        this.derivationTypeManager = derivationTypeManager;

        btnResetFixedPoint = new JButton("Reset Fixed Point");
        btnResetFixedPoint.addActionListener( (ae) -> {
            derivationTypeManager.resetFixedPointDerivation();
        });
        
        derivationPanel.add(btnResetFixedPoint);
        
        ButtonGroup derivationTypeGroup = new ButtonGroup();
        
        btnUseFixedPoint = new JToggleButton("Fixed Point");
        btnUseProgressive = new JToggleButton("Progressive");

        derivationTypeGroup.add(btnUseFixedPoint);
        derivationTypeGroup.add(btnUseProgressive);
        
        btnUseFixedPoint.setSelected(true);
        
        btnUseFixedPoint.addActionListener( (ae) -> {
            btnResetFixedPoint.setEnabled(true);
            derivationTypeManager.setDerivationType(DerivationType.FixedPoint);
        });
        
        btnUseProgressive.addActionListener( (ae) -> {
            btnResetFixedPoint.setEnabled(false);
            derivationTypeManager.setDerivationType(DerivationType.Progressive);
        });
        
        JPanel derivationTypePanel = new JPanel(new GridLayout(1, 2));
        derivationTypePanel.add(btnUseFixedPoint);
        derivationTypePanel.add(btnUseProgressive);
        
        derivationTypePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        derivationPanel.add(derivationTypePanel);
        
        ButtonGroup relTypeGroup = new ButtonGroup();
        
        this.btnUseStatedHierarchy = new JToggleButton("Asserted");
        this.btnUseInferredHierarchy = new JToggleButton("Inferred");
        
        relTypeGroup.add(btnUseStatedHierarchy);
        relTypeGroup.add(btnUseInferredHierarchy);
        
        btnUseStatedHierarchy.setSelected(true);
        
        btnUseInferredHierarchy.setEnabled(false);
        
        btnUseStatedHierarchy.addActionListener( (ae) -> {
            derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Stated);
        });
        
        btnUseInferredHierarchy.addActionListener( (ae) -> {
            derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Inferred);
        });
        
        JPanel relTypePanel = new JPanel(new GridLayout(1, 2));
        
        relTypePanel.add(btnUseStatedHierarchy);
        relTypePanel.add(btnUseInferredHierarchy);
        relTypePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        derivationPanel.add(relTypePanel);
        
        this.btnDerivationOptions = new JButton("Options");
        this.btnDerivationOptions.addActionListener( (ae) -> {
            
            if(this.optCurrentDataManager.isPresent()) {
                
                 DiffTaxonomyDerivationOptionsDialog optionsDialog = 
                    new DiffTaxonomyDerivationOptionsDialog(
                            protegeTaxonomyView,
                            this.optCurrentDataManager.get(), 
                            frameManager);
            }
        });
        
        derivationPanel.add(btnDerivationOptions);
        
        this.lblRefreshInferred = new JLabel(" ");
        
        this.setLayout(new BorderLayout());
        
        this.add(derivationPanel, BorderLayout.CENTER);
        
        this.add(lblRefreshInferred, BorderLayout.SOUTH);
        
        lblRefreshInferred.setVisible(false);
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
                displayPanel.getBounds().height - panelSize.height - 10, 
                panelSize.width, 
                panelSize.height);
        
        this.validate();
    }
    
    public void setCurrentDataManager(ProtegeLiveTaxonomyDataManager dataManager) {
        this.optCurrentDataManager = Optional.of(dataManager);
    }
    
    public void setInferredTaxonomyDirty() {
                
        this.lblRefreshInferred.setText("<html><font color = 'RED'>"
                + "Synchronize reasoner to update inferred diff taxonomy");
        
    }
    
    public void clearInferredTaxonomyDirty() {
         this.lblRefreshInferred.setText(" ");
    }
}