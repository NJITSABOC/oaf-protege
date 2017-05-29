package edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons;

import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetGroup;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.targetabn.buttons.CreateAncestorTargetAbNButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.targetabn.buttons.CreateDescendantTargetAbNButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.targetabn.buttons.ExpandAggregateTargetGroupButton;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.range.configuration.OWLRangeAbNConfiguration;

/**
 *
 * @author Chris O
 */
public class ProtegeTargetGroupOptionsPanel extends NodeOptionsPanel<TargetGroup> {

    public ProtegeTargetGroupOptionsPanel(OWLRangeAbNConfiguration config) {

        CreateAncestorTargetAbNButton createAncestorBtn = new CreateAncestorTargetAbNButton(config, (targetAbN) -> {
            TargetAbstractionNetwork abn = (TargetAbstractionNetwork) targetAbN;

            config.getUIConfiguration().getAbNDisplayManager().displayTargetAbstractionNetwork(abn);
        });
        
        super.addOptionButton(createAncestorBtn);
        
        CreateDescendantTargetAbNButton createDescendantBtn = new CreateDescendantTargetAbNButton(config, (targetAbN) -> {
            TargetAbstractionNetwork abn = (TargetAbstractionNetwork) targetAbN;

            config.getUIConfiguration().getAbNDisplayManager().displayTargetAbstractionNetwork(abn);
        });
        
        super.addOptionButton(createDescendantBtn);
        

        if (config.getTargetAbstractionNetwork().isAggregated()) {
            ExpandAggregateTargetGroupButton expandAggregateButton = new ExpandAggregateTargetGroupButton(config, (targetAbN) -> {
                TargetAbstractionNetwork abn = (TargetAbstractionNetwork) targetAbN;

                config.getUIConfiguration().getAbNDisplayManager().displayTargetAbstractionNetwork(abn);
            });

            super.addOptionButton(expandAggregateButton);
        }

        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("partial-area", () -> {
            TargetGroup group = super.getCurrentNode().get();

            NodeDashboardPanel anp = config.getUIConfiguration().createNodeDetailsPanel();
            anp.setContents(group);

            return anp;
        });

        super.addOptionButton(popoutBtn);

        NodeHelpButton helpBtn = new NodeHelpButton(config);

        super.addOptionButton(helpBtn);
    }
}
