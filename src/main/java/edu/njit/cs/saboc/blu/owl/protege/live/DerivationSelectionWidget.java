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
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author Chris Ochs
 */
public class DerivationSelectionWidget extends AbNDisplayWidget {

    private Optional<ProtegeLiveTaxonomyDataManager> optCurrentDataManager = Optional.empty();
    
    private final DiffDerivationTypeManager derivationTypeManager;
    
    private final Dimension NO_WARNING_SIZE = new Dimension(550, 40);
    private final Dimension WARNING_SIZE = new Dimension(550, 50);

    private Dimension panelSize = NO_WARNING_SIZE;
    
    private final JButton btnResetFixedPoint;
    
    private final JToggleButton btnUseFixedPoint;
    private final JToggleButton btnUseProgressive;
    
    private final JToggleButton btnUseStatedHierarchy;
    private final JToggleButton btnUseInferredHierarchy;
    
    private final JButton btnDerivationOptions;
    
    private final JLabel lblRefreshInferred;
    
    private boolean initialized = false;
    
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
           fixedPointSelected();
        });
        
        btnUseProgressive.addActionListener( (ae) -> {
            progressiveSelected();
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
            assertedHierarchySelected();
        });
        
        btnUseInferredHierarchy.addActionListener( (ae) -> {
           inferredHierarchySelected();
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

        fixedPointSelected();
        assertedHierarchySelected();
        
        this.lblRefreshInferred = new JLabel(" ");
        
        this.setLayout(new BorderLayout());
        
        this.add(derivationPanel, BorderLayout.CENTER);
        
        this.add(lblRefreshInferred, BorderLayout.NORTH);
        
        lblRefreshInferred.setVisible(false);
        
        this.initialized = true;
    }
    
    private void fixedPointSelected() {
        
        if(initialized) {
            derivationTypeManager.setDerivationType(DerivationType.FixedPoint);
        }
        
        btnResetFixedPoint.setEnabled(true);
        
        btnUseFixedPoint.setForeground(Color.BLACK);
        btnUseFixedPoint.setFont(btnUseFixedPoint.getFont().deriveFont(Font.BOLD));
        
        btnUseProgressive.setForeground(Color.DARK_GRAY);
        btnUseProgressive.setFont(btnUseProgressive.getFont().deriveFont(Font.PLAIN));
    }
    
    private void progressiveSelected() {
        
        if(initialized) {
            derivationTypeManager.setDerivationType(DerivationType.Progressive);
        }
        
        btnResetFixedPoint.setEnabled(false);
        
        btnUseProgressive.setForeground(Color.BLACK);
        btnUseProgressive.setFont(btnUseProgressive.getFont().deriveFont(Font.BOLD));
        
        btnUseFixedPoint.setForeground(Color.DARK_GRAY);
        btnUseFixedPoint.setFont(btnUseFixedPoint.getFont().deriveFont(Font.PLAIN));
        
    }
    
    private void assertedHierarchySelected() {

        if (initialized) {
            derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Stated);
        }

        btnUseStatedHierarchy.setForeground(Color.BLACK);
        btnUseStatedHierarchy.setFont(btnUseStatedHierarchy.getFont().deriveFont(Font.BOLD));

        btnUseInferredHierarchy.setForeground(Color.DARK_GRAY);
        btnUseInferredHierarchy.setFont(btnUseInferredHierarchy.getFont().deriveFont(Font.PLAIN));
    }
    
    private void inferredHierarchySelected() {
        
        if(initialized) {
            derivationTypeManager.setRelationshipType(DiffDerivationTypeManager.RelationshipType.Inferred);
        }

        btnUseInferredHierarchy.setForeground(Color.BLACK);
        btnUseInferredHierarchy.setFont(btnUseInferredHierarchy.getFont().deriveFont(Font.BOLD));

        btnUseStatedHierarchy.setForeground(Color.DARK_GRAY);
        btnUseStatedHierarchy.setFont(btnUseStatedHierarchy.getFont().deriveFont(Font.PLAIN));
        
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
        
        this.panelSize = WARNING_SIZE;
                
        this.lblRefreshInferred.setText("<html><font color = 'RED'>"
                + "Synchronize reasoner to update inferred diff "
                + "taxonomy");
        
        this.lblRefreshInferred.setVisible(true);
        
        this.displayPanelResized(this.getDisplayPanel());
    }
    
    public void clearInferredTaxonomyDirty() {
        
        this.panelSize = NO_WARNING_SIZE;
        
        this.lblRefreshInferred.setText(" ");
        this.lblRefreshInferred.setVisible(false);
        
        this.displayPanelResized(this.getDisplayPanel());
    }
}