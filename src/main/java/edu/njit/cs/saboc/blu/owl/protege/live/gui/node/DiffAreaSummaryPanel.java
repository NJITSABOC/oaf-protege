package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.BaseNodeInformationPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.label.DetailsPanelLabel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import java.awt.BorderLayout;
import java.util.Optional;

/**
 *
 * @author Chris Ochs
 */
public class DiffAreaSummaryPanel extends BaseNodeInformationPanel<Area> {
    
    private Optional<Area> optCurrentArea;
    
    private final DetailsPanelLabel nodeNameLabel;
    
    private final NodeOptionsPanel optionsPanel;
    
    public DiffAreaSummaryPanel(ProtegeDiffPAreaTaxonomyConfiguration diffConfig) {
        this.optCurrentArea = Optional.empty();

        this.nodeNameLabel = new DetailsPanelLabel(" ");

        this.optionsPanel = new NodeOptionsPanel();

        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("diff area", () -> {
            Area parea = optCurrentArea.get();

            NodeDashboardPanel anp = diffConfig.getUIConfiguration().createPartitionedNodeDetailsPanel();
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
    public void setContents(Area parea) {
        this.optCurrentArea = Optional.of(parea);
        
        this.nodeNameLabel.setText(parea.getName());
        this.optionsPanel.setContents(parea);
    }

    @Override
    public void clearContents() {
        this.optCurrentArea = Optional.empty();
        
        this.nodeNameLabel.setText(" ");
        this.optionsPanel.clearContents();
    }
}
