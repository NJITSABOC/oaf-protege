package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import edu.njit.cs.saboc.blu.core.abn.node.PartitionedNode;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.loading.LoadingPanel;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import java.awt.BorderLayout;
import java.util.Optional;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Chris O
 */
public class DiffTaxonomyDashboardPanel extends JPanel {
    
    private final LoadingPanel loadingPanel;
        
    private Optional<DiffPAreaSummaryPanel> pareaSummaryPanel = Optional.empty();
    private Optional<DiffAreaSummaryPanel> areaDetailsPanel = Optional.empty();

    private Optional<PArea> selectedPArea = Optional.empty();
    
    private Optional<PartitionedNode> selectedArea = Optional.empty();
    
    public DiffTaxonomyDashboardPanel() {
        super(new BorderLayout());
        
        loadingPanel = new LoadingPanel();
    }
    
    public void initialize(ProtegeDiffPAreaTaxonomyConfiguration configuration) {
        this.pareaSummaryPanel = Optional.of(new DiffPAreaSummaryPanel(configuration));
        this.areaDetailsPanel = Optional.of(new DiffAreaSummaryPanel(configuration));
    }
    
    private void clearPartitionedNodePanels() {
        selectedArea = Optional.empty();
        
        if (areaDetailsPanel.isPresent()) {
            areaDetailsPanel.get().clearContents();
        }
    }
    
    private void clearNodePanels() {
        selectedPArea = Optional.empty();
        
        if (pareaSummaryPanel.isPresent()) {
            pareaSummaryPanel.get().clearContents();
        }
    }
    
    public void displayDetailsForPArea(PArea parea) {

        clearPartitionedNodePanels();

        this.selectedPArea = Optional.of(parea);

        if (pareaSummaryPanel.isPresent()) {
            
            setDetailsPanelContents(loadingPanel);

            pareaSummaryPanel.get().clearContents();

            Thread loadThread = new Thread(() -> {
                pareaSummaryPanel.get().setContents(parea);

                SwingUtilities.invokeLater(() -> {
                    setDetailsPanelContents(pareaSummaryPanel.get());
                });
            });

            loadThread.start();
        }
    }
    
    public void displayDetailsForArea(Area area) {
        
        clearNodePanels();

        this.selectedArea = Optional.of(area);

        if (areaDetailsPanel.isPresent()) {
            setDetailsPanelContents(loadingPanel);

            areaDetailsPanel.get().clearContents();

            Thread loadThread = new Thread(() -> {
                areaDetailsPanel.get().setContents(area);

                SwingUtilities.invokeLater(() -> {
                    setDetailsPanelContents(areaDetailsPanel.get());
                });
            });

            loadThread.start();
        }
    }

    private void setDetailsPanelContents(JPanel panel) {
        this.removeAll();
        
        this.add(panel, BorderLayout.CENTER);
        
        this.revalidate();
        this.repaint();
    }
    
    public void reset() {
        clearNodePanels();
        clearPartitionedNodePanels();
    }
    
    public void clear() {
        reset();

        areaDetailsPanel = Optional.empty();
        pareaSummaryPanel = Optional.empty();
        
        this.setDetailsPanelContents(loadingPanel);
    }
}
