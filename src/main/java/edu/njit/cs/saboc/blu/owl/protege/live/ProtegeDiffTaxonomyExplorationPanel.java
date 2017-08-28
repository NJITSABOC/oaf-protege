package edu.njit.cs.saboc.blu.owl.protege.live;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.Region;
import edu.njit.cs.saboc.blu.core.graph.AbstractionNetworkGraph;
import edu.njit.cs.saboc.blu.core.graph.nodes.GenericPartitionEntry;
import edu.njit.cs.saboc.blu.core.graph.nodes.SinglyRootedNodeEntry;
import edu.njit.cs.saboc.blu.core.gui.gep.AbNDisplayPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.Viewport;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.BaseAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.utils.drawing.AbNPainter;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.owl.protege.LogMessageGenerator;
import edu.njit.cs.saboc.blu.owl.protege.live.configuration.ProtegeDiffPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.live.gui.node.DiffTaxonomyDashboardPanel;
import edu.njit.cs.saboc.blu.owl.protege.live.gui.node.DiffTaxonomyFloatingDashboardFrame;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.DiffDerivationTypeManager.DerivationTypeChangedListener;
import edu.njit.cs.saboc.blu.owl.protege.live.manager.LiveDiffTaxonomyManager;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeDiffTaxonomyExplorationPanel extends JPanel {
    
    private final Logger logger = LoggerFactory.getLogger(ProtegeDiffTaxonomyExplorationPanel.class);

    private AbNConfiguration configuration;
    
    private final AbNWarningManager warningManager;
    
    private final AbNDisplayPanel displayPanel = new AbNDisplayPanel();
    
    private final DiffTaxonomyFloatingDashboardFrame dashboardFrame;
    private final DiffTaxonomyDashboardPanel dashboardPanel;
    
    private final DiffDerivationTypeManager derivationTypeManager;
    private final LiveDiffTaxonomyManager diffTaxonomyManager;
    
    private boolean firstLoad = true;
    
    private boolean viewportDirty = false;

    public ProtegeDiffTaxonomyExplorationPanel(
            DiffDerivationTypeManager derivationTypeManager,
            LiveDiffTaxonomyManager diffTaxonomyManager) {
        
        super(new BorderLayout());
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "ProtegeDifTaxonomyExplorationPanel", 
                ""));
        
        this.derivationTypeManager = derivationTypeManager;
        this.diffTaxonomyManager = diffTaxonomyManager;
        
        this.warningManager = new AbNWarningManager() {
            
            @Override
            public boolean showAggregationWarning() {
                return false;
            }
        };

        displayPanel.addAbNSelectionListener(new AbNDisplayPanel.AbNEntitySelectionListener() {

            @Override
            public void nodeEntrySelected(SinglyRootedNodeEntry nodeEntry) {
                
                dashboardPanel.displayDetailsForPArea((PArea)nodeEntry.getNode());
                dashboardFrame.setVisible(true);
                
            }

            @Override
            public void partitionEntrySelected(GenericPartitionEntry entry) {
                
                Region region = (Region)entry.getNode();
                
                dashboardPanel.displayDetailsForArea(region.getArea());
                dashboardFrame.setVisible(true);
                
            }

            @Override
            public void noEntriesSelected() {
                dashboardFrame.setVisible(false);
            }
        });
        
        displayPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               
                int x = (displayPanel.getWidth() - dashboardFrame.getWidth()) / 2;
                int y = displayPanel.getHeight() - dashboardFrame.getHeight() - 200;
               
                dashboardFrame.setLocation(x, y);
            }
            
        });
        
        derivationTypeManager.addDerivationTypeChangedListener(new DerivationTypeChangedListener() {
            
            @Override
            public void derivationTypeChanged(DiffDerivationTypeManager.DerivationType newDerivationType) {
                
            }

            @Override
            public void relationshipTypeChanged(DiffDerivationTypeManager.RelationshipType version) {
                viewportDirty = true;
            }

            @Override
            public void resetFixedPointStart() {
                
            }
        });
        
        diffTaxonomyManager.addDerivationSettingsChangedListener( (derivationSettings) -> {
            viewportDirty = true;
        });
        
        this.dashboardPanel = new DiffTaxonomyDashboardPanel();
        this.dashboardFrame = new DiffTaxonomyFloatingDashboardFrame(dashboardPanel);
        
        this.dashboardFrame.setVisible(false);
        
        this.displayPanel.add(dashboardFrame);
        
        this.add(displayPanel, BorderLayout.CENTER);
    }
    
    public AbNDisplayPanel getDisplayPanel() {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "ProtegeDiffTaxonomyExplorationPanel - getDisplayPanel",
                ""));
        
        return displayPanel;
    }
    
    public void showLoading() {
        displayPanel.doLoading();
    }
    
     public void initialize(
            AbstractionNetworkGraph graph, 
            ProtegeDiffPAreaTaxonomyConfiguration config, 
            AbNPainter painter) {
         
         logger.debug(LogMessageGenerator.createLiveDiffString(
                 "ProtegeDiffTaxonomyExplorationPanel - initialize",
                 ""));
         
         initialize(graph, 
                 config, 
                 painter, 
                 new BaseAbNExplorationPanelInitializer(warningManager));
     }

    public void initialize(
            AbstractionNetworkGraph graph, 
            ProtegeDiffPAreaTaxonomyConfiguration config, 
            AbNPainter painter,
            AbNExplorationPanelGUIInitializer initializer) {
        
        logger.debug(LogMessageGenerator.createLiveDiffString(
                "ProtegeDiffTaxonomyExplorationPanel - initialize",
                ""));

        this.configuration = config;
        
        boolean dashboardFrameVisible = dashboardFrame.isVisible();
        Viewport oldViewport = displayPanel.getViewport();
        
        dashboardFrame.setVisible(false);
        
        displayPanel.initialize(graph, painter, initializer.getInitialDisplayAction());
        
        dashboardPanel.initialize(config);
        
        initializer.initializeAbNDisplayPanel(displayPanel, firstLoad);
        
        this.firstLoad = false;

        displayPanel.resetUpdateables();
        
        config.getUIConfiguration().setDisplayPanel(displayPanel);
        
        dashboardFrame.revalidate();
        dashboardFrame.repaint();
        
        if(dashboardFrameVisible && dashboardPanel.anyNodeSelected()) {
            dashboardFrame.setVisible(true);
        }
        
        if (!viewportDirty) {

            SwingUtilities.invokeLater(() -> {
                
                Viewport currentViewport = displayPanel.getViewport();

                Rectangle region = currentViewport.getViewRegion();
                
                region.x = oldViewport.getViewRegion().x;
                region.y = oldViewport.getViewRegion().y;
                region.width = oldViewport.getViewRegion().width;
                region.height = oldViewport.getViewRegion().height;
                
                currentViewport.forceZoom(oldViewport.getZoomFactor());
                
                viewportDirty = false;
            });
        }

    }
}