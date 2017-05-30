package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.BaseNodeInformationPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.label.DetailsPanelLabel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.ExportPartitionedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import java.awt.BorderLayout;
import java.util.Optional;

/**
 *
 * @author Chris Ochs
 */
public class DiffPAreaSummaryPanel extends BaseNodeInformationPanel<PArea> {
    
    private Optional<PArea> optCurrentPArea;
    
    private final DetailsPanelLabel nodeNameLabel;
    
    private final NodeOptionsPanel optionsPanel;
    
    public DiffPAreaSummaryPanel(ProtegeDiffPAreaTaxonomyConfiguration diffConfig) {
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
    }

    @Override
    public void setContents(PArea parea) {
        this.optCurrentPArea = Optional.of(parea);
        
        this.nodeNameLabel.setText(parea.getName());
        this.optionsPanel.setContents(parea);
    }

    @Override
    public void clearContents() {
        this.optCurrentPArea = Optional.empty();
        
        this.nodeNameLabel.setText(" ");
        this.optionsPanel.clearContents();
    }
}
