package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPArea;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.BaseNodeInformationPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.diff.DiffNodeChangesPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.diff.DiffRootChangeExplanationPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.label.DetailsPanelLabel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaChangeExplanationFactory;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Optional;
import javax.swing.JTabbedPane;

/**
 *
 * @author Chris Ochs
 */
public class DiffPAreaSummaryPanel extends BaseNodeInformationPanel<PArea> {
    
    private Optional<PArea> optCurrentPArea;
    
    private final DetailsPanelLabel nodeNameLabel;
    
    private final NodeOptionsPanel optionsPanel;
    
    private final JTabbedPane changeDetailsTabs;
    
    private final DiffNodeChangesPanel<DiffPArea> diffPAreaChangesPanel;
    
    private final DiffRootChangeExplanationPanel<DiffPArea> rootChangesPanel;
    
    public DiffPAreaSummaryPanel(
            ProtegeDiffPAreaTaxonomyConfiguration diffConfig) {
        
        this.optCurrentPArea = Optional.empty();

        this.nodeNameLabel = new DetailsPanelLabel(" ");

        this.optionsPanel = new NodeOptionsPanel();

        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("diff partial-area", () -> {
            PArea parea = optCurrentPArea.get();

            NodeDashboardPanel anp = diffConfig.getUIConfiguration().createNodeDetailsPanel();
            anp.setContents(parea);

            return anp;
        });

        optionsPanel.addOptionButton(popoutBtn);

        NodeHelpButton helpBtn = new NodeHelpButton(diffConfig);
        
        optionsPanel.addOptionButton(helpBtn);

        this.setLayout(new BorderLayout());

        this.add(nodeNameLabel, BorderLayout.CENTER);
        this.add(optionsPanel, BorderLayout.EAST);

        this.changeDetailsTabs = new JTabbedPane();
        
        this.diffPAreaChangesPanel = new DiffNodeChangesPanel<>(
                diffConfig,
                diffConfig.getTextConfiguration());
        
        this.rootChangesPanel = new DiffRootChangeExplanationPanel<>(
                diffConfig, 
                diffConfig.getTextConfiguration(),
                new OWLDiffPAreaChangeExplanationFactory(diffConfig));
        
        this.changeDetailsTabs.addTab("Diff PArea Changes", diffPAreaChangesPanel);
        this.changeDetailsTabs.addTab("Root Class Changes", rootChangesPanel);
        
        this.changeDetailsTabs.setPreferredSize(new Dimension(-1, 200));
        
        this.add(changeDetailsTabs, BorderLayout.SOUTH);
    }

    @Override
    public void setContents(PArea parea) {
        this.optCurrentPArea = Optional.of(parea);
        
        this.nodeNameLabel.setText(parea.getName());
        this.optionsPanel.setContents(parea);
        
        this.diffPAreaChangesPanel.setContents((DiffPArea)parea);
        this.rootChangesPanel.setContents((DiffPArea)parea);
    }

    @Override
    public void clearContents() {
        this.optCurrentPArea = Optional.empty();
        
        this.nodeNameLabel.setText(" ");
        this.optionsPanel.clearContents();
        
        this.diffPAreaChangesPanel.clearContents();
        this.rootChangesPanel.clearContents();
    }
}
