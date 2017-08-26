package edu.njit.cs.saboc.blu.owl.protege.live.gui.node;

import edu.njit.cs.saboc.blu.core.abn.diff.change.ChangeState;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Area;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.diff.DiffPAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.loading.LoadingPanel;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import java.awt.BorderLayout;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris O
 */
public class DiffTaxonomyDashboardPanel extends JPanel {
    
    private final Logger logger = LoggerFactory.getLogger(DiffTaxonomyDashboardPanel.class);
    
    private final LoadingPanel loadingPanel;
        
    private Optional<DiffPAreaSummaryPanel> pareaSummaryPanel = Optional.empty();
    private Optional<DiffAreaSummaryPanel> areaDetailsPanel = Optional.empty();

    private Optional<DiffPArea> selectedPArea = Optional.empty();
    private Optional<DiffArea> selectedArea = Optional.empty();
    
    public DiffTaxonomyDashboardPanel() {
        super(new BorderLayout());
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel",
                ""));
        
        loadingPanel = new LoadingPanel();
    }
    
    public void initialize(ProtegeDiffPAreaTaxonomyConfiguration configuration) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - initialize",
                ""));
        
        Optional<DiffPArea> optSelectedPArea = this.selectedPArea;
        Optional<DiffArea> optSelectedArea = this.selectedArea;
        
        clear();

        this.pareaSummaryPanel = Optional.of(new DiffPAreaSummaryPanel(configuration));
        this.areaDetailsPanel = Optional.of(new DiffAreaSummaryPanel(configuration));
        
        if(optSelectedPArea.isPresent()) {
            
            DiffPArea prevDiffPArea = optSelectedPArea.get();
            
            DiffPAreaTaxonomy diffTaxonomy = configuration.getPAreaTaxonomy();
            
            Set<DiffPArea> matchingPAreas = diffTaxonomy.getNodes().stream().filter( (diffPArea) -> {
                return diffPArea.getRoot().equals(prevDiffPArea.getRoot());
            }).collect(Collectors.toSet());
            
            if(matchingPAreas.isEmpty()) {
                return;
            }
            
            if(matchingPAreas.size() == 1) {
                
                displayDetailsForPArea(matchingPAreas.iterator().next());
                
            } else if(matchingPAreas.size() == 2) {
                
                // The previous diff parea had to be the removed one...
                Optional<DiffPArea> optRemovedDiffPArea = matchingPAreas.stream().filter( (diffPArea) -> {
                    return diffPArea.getPAreaState() == ChangeState.Removed;
                }).findAny();
                
                if(optRemovedDiffPArea.isPresent()) {
                    displayDetailsForPArea(optRemovedDiffPArea.get());
                }
            }

            
        } else if(optSelectedArea.isPresent()) {
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "DiffTaxonomyDashboardPanel - initialize",
                    "displaying area details: " + optSelectedArea.get().getName()));
            
            DiffArea prevDiffArea = optSelectedArea.get();
            
            DiffPAreaTaxonomy diffTaxonomy = configuration.getPAreaTaxonomy();
            
            Set<DiffArea> matchingAreas = diffTaxonomy.getDiffAreas().stream().filter( (diffArea) -> {
                return diffArea.getRelationships().equals(prevDiffArea.getRelationships());
            }).collect(Collectors.toSet());
             
            if(matchingAreas.isEmpty()) {
                return;
            }
            
            if(matchingAreas.size() == 1) {
                displayDetailsForArea(matchingAreas.iterator().next());
            } else if (matchingAreas.size() == 2) {
                
                Optional<DiffArea> optRemovedDiffArea = matchingAreas.stream().filter( (diffArea) -> {
                    return diffArea.getAreaState() == ChangeState.Removed;
                }).findAny();
                
                if(optRemovedDiffArea.isPresent()) {
                    displayDetailsForArea(optRemovedDiffArea.get());
                }
                
            }
        }
    }
    
    private void clearPartitionedNodePanels() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - clearPartitionedNodePanels",
                ""));
        
        selectedArea = Optional.empty();
        
        if (areaDetailsPanel.isPresent()) {
            areaDetailsPanel.get().clearContents();
        }
    }
    
    private void clearNodePanels() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - clearNodePanels",
                ""));
        
        selectedPArea = Optional.empty();
        
        if (pareaSummaryPanel.isPresent()) {
            pareaSummaryPanel.get().clearContents();
        }
    }
    
    public void displayDetailsForPArea(PArea parea) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - displayDetailsForPArea",
                parea.getName()));

        clearPartitionedNodePanels();

        this.selectedPArea = Optional.of((DiffPArea)parea);

        if (pareaSummaryPanel.isPresent()) {

            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "DiffTaxonomyDashboardPanel - displayDetailsForPArea",
                    "PArea summary panel present..."));
            
            showLoading();

            Thread loadThread = new Thread(() -> {
                
                pareaSummaryPanel.get().clearContents();
                
                pareaSummaryPanel.get().setContents(parea);

                SwingUtilities.invokeLater(() -> {
                    
                    logger.debug(LogMessageGenerator.createLiveDiffString(
                            "DiffTaxonomyDashboardPanel - displayDetailsForPArea",
                            "Setting panel contents..."));

                    setDetailsPanelContents(pareaSummaryPanel.get());
                });
            });
            
            logger.debug(LogMessageGenerator.createLiveDiffString(
                    "DiffTaxonomyDashboardPanel - displayDetailsForPArea",
                    "Starting load thread..."));

            loadThread.start();
        }
    }
    
    public void displayDetailsForArea(Area area) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - displayDetailsForArea",
                area.getName()));
        
        clearNodePanels();
        
        this.selectedArea = Optional.of((DiffArea)area);

        if (areaDetailsPanel.isPresent()) {
            
            showLoading();

            Thread loadThread = new Thread(() -> {
                
                areaDetailsPanel.get().clearContents();
                areaDetailsPanel.get().setContents(area);

                SwingUtilities.invokeLater(() -> {
                    setDetailsPanelContents(areaDetailsPanel.get());
                });
            });

            loadThread.start();
        }
    }
    
    private void showLoading() {
        setDetailsPanelContents(loadingPanel);
    }

    private void setDetailsPanelContents(JPanel panel) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - setDetailsPanelContents",
                panel.toString()));
        
        this.removeAll();
        
        this.add(panel, BorderLayout.CENTER);
        
        this.revalidate();
        this.repaint();
    }
    
    public void reset() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - reset",
                ""));
        
        clearNodePanels();
        clearPartitionedNodePanels();
    }
    
    public void clear() {

        logger.debug(LogMessageGenerator.createLiveDiffString(
                "DiffTaxonomyDashboardPanel - clear",
                ""));
        
        reset();

        areaDetailsPanel = Optional.empty();
        pareaSummaryPanel = Optional.empty();
        
        this.setDetailsPanelContents(loadingPanel);
    }
    
    public boolean anyNodeSelected() {
        return this.selectedArea.isPresent() || this.selectedPArea.isPresent();
    }
}
