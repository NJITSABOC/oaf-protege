package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.abn.node.SinglyRootedNode;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayWidget;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.reports.diff.DiffPAreaImplicitChangeReport;
import edu.njit.cs.saboc.blu.core.gui.iconmanager.ImageManager;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris O
 */
public class ChangeAlertWidget extends AbNDisplayWidget {
    
    private final Logger logger = LoggerFactory.getLogger(ChangeAlertWidget.class);

    private final Dimension panelSize = new Dimension(50, 50);
    
    private final JButton alertBtn;
    
    private Optional<DiffPAreaTaxonomy> optCurrentDiffTaxonomy = Optional.empty();
    
    private final JPopupMenu alertOptionMenu;
    
    public ChangeAlertWidget(AbNDisplayPanel panel) {
        super(panel);
        
        this.setLayout(new BorderLayout());
        
        this.alertBtn = new JButton(ImageManager.getImageManager().getIcon("alert.png"));
        
        this.add(alertBtn);
        
        this.alertBtn.setEnabled(false);
        
        this.alertOptionMenu = new JPopupMenu();
        
        this.alertBtn.addActionListener( (ae) -> {
            alertOptionMenu.show(
                    this.alertBtn, 
                    0, 
                    0);
        });
    }
    
    public void update(DiffPAreaTaxonomy diffTaxonomy) {
        
        this.optCurrentDiffTaxonomy = Optional.of(diffTaxonomy);
        
        DiffPAreaImplicitChangeReport changeReport = new DiffPAreaImplicitChangeReport(diffTaxonomy);
        
        this.alertOptionMenu.setVisible(false);
        this.alertOptionMenu.removeAll();
        
        boolean enableAlert = false;
        
        String toolTip = "<html><ul>";
        
        if(!changeReport.getPAreasWithOnlyImplicitChanges().isEmpty()) {
            this.alertBtn.setEnabled(true);
            
            enableAlert = true;
            
            toolTip += "<li>Diff partial-area(s) affected indirectly</i>";
            
            JMenu implicitAlertOptions = new JMenu("Diff PAreas Affected Indirectly");
            
            JMenuItem highlightOption = new JMenuItem("Highlight");
            highlightOption.addActionListener( (ae) -> {
                Set<SinglyRootedNode> affectedPAreas = new HashSet<>(changeReport.getPAreasWithOnlyImplicitChanges());
                
                this.getDisplayPanel().highlightSinglyRootedNodes(affectedPAreas);
            });
            
            implicitAlertOptions.add(highlightOption);
            
            this.alertOptionMenu.add(implicitAlertOptions);
        }
        
        if(enableAlert) {
            toolTip += "</ul></html>";
        } else {
            toolTip = "No Diff Taxomomy Alerts";
        }
        
        this.alertBtn.setToolTipText(toolTip);
        this.alertBtn.setEnabled(enableAlert);
    }
    
    public void clear() {
        this.optCurrentDiffTaxonomy = Optional.empty();
    }
    
    @Override
    public void displayPanelResized(AbNDisplayPanel displayPanel) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "displayPanelResized",
                ""));
        
        this.setBounds(
                displayPanel.getBounds().width - panelSize.width - 20, 
                displayPanel.getBounds().height - panelSize.height - 20, 
                panelSize.width, 
                panelSize.height);
        
        this.validate();
    }
}
