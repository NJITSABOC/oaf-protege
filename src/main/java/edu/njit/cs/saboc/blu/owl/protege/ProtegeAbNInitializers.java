package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.tan.ClusterTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.AbNExplorationPanelGUIInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.initializer.DisjointAbNExplorationPanelInitializer;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.AbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.configuration.DisjointAbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.configuration.PAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.tan.configuration.TANConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.targetbased.configuration.TargetAbNConfiguration;
import edu.njit.cs.saboc.blu.core.gui.gep.warning.AbNWarningManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.GraphFrameInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.MultiAbNGraphFrame;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.TaskBarPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.AreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.BandTANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.DisjointAbNInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.PAreaTaxonomyInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TANInitializer;
import edu.njit.cs.saboc.blu.core.gui.graphframe.multiabn.initializers.TargetAbNInitializer;
import edu.njit.cs.saboc.blu.owl.abn.OWLAbstractionNetwork;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.gui.graphframe.initializers.OWLMultiAbNGraphFrameInitializers;
import edu.njit.cs.saboc.blu.owl.protege.configuration.ProtegeDisjointPAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.configuration.ProtegeDisjointTANConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.configuration.ProtegePAreaTaxonomyConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.configuration.ProtegeRangeAbNConfigurationFactory;
import edu.njit.cs.saboc.blu.owl.protege.configuration.ProtegeTANConfigurationFactory;
import javax.swing.JLabel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris Ochs
 */
public class ProtegeAbNInitializers extends OWLMultiAbNGraphFrameInitializers {

    private final ProtegeOAFOntologyDataManager ontologyManager;
    
    private final OWLWorkspace workspace;
    
    public ProtegeAbNInitializers(
            ProtegeOAFOntologyDataManager ontologyManager,
            OWLWorkspace workspace,
            OWLAbNFrameManager frameManager,
            AbNWarningManager warningManager) {

        super(ontologyManager, frameManager, warningManager);
        
        this.ontologyManager = ontologyManager;
        this.workspace = workspace;
    }
    
    private JLabel createRelStateLabel() {
        
        String str;
        
        if(ontologyManager.inferredRelsAvailable()) {
            
            str = String.format("<b><font color = 'RED'>inferred hierarchy</font></b>");
            
        } else {
            str = String.format("<b><font color = 'BLUE'>stated hierarchy</font></b>");
        }
        
        String labelStr = String.format("<html>Created using: %s", str);
        
        JLabel statusLabel = new JLabel(labelStr);
        
        return statusLabel;
    }
    
    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getPAreaTaxonomyInitializer() {

        return new PAreaTaxonomyInitializer(getWarningManager()) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getPAreaTaxonomy();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new ProtegePAreaTaxonomyConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager(), 
                        false);
            }
        };
    }

    @Override
    public GraphFrameInitializer<PAreaTaxonomy, PAreaTaxonomyConfiguration> getAreaTaxonomyInitializer() {

        return new AreaTaxonomyInitializer(getWarningManager()) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, PAreaTaxonomyConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getPAreaTaxonomy();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(PAreaTaxonomy abn, AbNDisplayManager displayManager) {
                return new ProtegePAreaTaxonomyConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager(), 
                        true);
            }
        };
    }

    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getTANInitializer() {

        return new TANInitializer(getWarningManager()) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getAbstractionNetwork();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new ProtegeTANConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager(), 
                        false);
            }
        };
    }

    @Override
    public GraphFrameInitializer<ClusterTribalAbstractionNetwork, TANConfiguration> getBandTANInitializer() {

        return new BandTANInitializer(getWarningManager()) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TANConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getAbstractionNetwork();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(ClusterTribalAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new ProtegeTANConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager(), 
                        true);
            }
        };
    }

    @Override
    public GraphFrameInitializer<TargetAbstractionNetwork, TargetAbNConfiguration> getTargetAbNInitializer() {

        return new TargetAbNInitializer(getWarningManager()) {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, TargetAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getAbstractionNetwork();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(TargetAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new ProtegeRangeAbNConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager());
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointPAreaTaxonomyInitializer() {

        return new DisjointAbNInitializer() {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getAbstractionNetwork().getParentAbstractionNetwork();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new ProtegeDisjointPAreaTaxonomyConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager());
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {

                PAreaTaxonomy taxonomy = (PAreaTaxonomy) config.getAbstractionNetwork().getParentAbstractionNetwork();

                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        new OWLPAreaTaxonomyConfigurationFactory().createConfiguration(
                                taxonomy,
                                config.getUIConfiguration().getAbNDisplayManager(),
                                getFrameManager(),
                                false),
                        (bound, isWeightedAggregate) -> {
                            DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(bound, isWeightedAggregate);
                            config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointAbN);
                        },
                        getWarningManager());
            }
        };
    }

    @Override
    public GraphFrameInitializer<DisjointAbstractionNetwork, DisjointAbNConfiguration> getDisjointTANInitializer() {
        return new DisjointAbNInitializer() {

            @Override
            public TaskBarPanel getTaskBar(MultiAbNGraphFrame graphFrame, DisjointAbNConfiguration config) {
                TaskBarPanel panel = super.getTaskBar(graphFrame, config);

                OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork) config.getAbstractionNetwork().getParentAbstractionNetwork();

                panel.addToggleableButtonToMenu(
                        createDerivationSelectionButton(graphFrame, owlAbN.getDataManager()));
                
                panel.addOtherOptionsComponent(createRelStateLabel());

                return panel;
            }

            @Override
            public AbNConfiguration getConfiguration(DisjointAbstractionNetwork abn, AbNDisplayManager displayManager) {
                return new ProtegeDisjointTANConfigurationFactory().createConfiguration(
                        abn, 
                        workspace,
                        displayManager, 
                        getFrameManager());
            }

            @Override
            public AbNExplorationPanelGUIInitializer getExplorationGUIInitializer(DisjointAbNConfiguration config) {

                ClusterTribalAbstractionNetwork tan = (ClusterTribalAbstractionNetwork) config.getAbstractionNetwork().getParentAbstractionNetwork();
                
                OWLTANConfiguration tanConfig = new OWLTANConfigurationFactory().createConfiguration(
                                tan,
                                config.getUIConfiguration().getAbNDisplayManager(),
                                getFrameManager(),
                                false);
                
                return new DisjointAbNExplorationPanelInitializer(
                        config,
                        tanConfig,
                        (bound, isWeightedAggregate) -> {
                            DisjointAbstractionNetwork disjointAbN = config.getAbstractionNetwork().getAggregated(bound, isWeightedAggregate);
                            config.getUIConfiguration().getAbNDisplayManager().displayDisjointTribalAbstractionNetwork(disjointAbN);
                        },
                        
                        getWarningManager());
            }
        };
    }
}
