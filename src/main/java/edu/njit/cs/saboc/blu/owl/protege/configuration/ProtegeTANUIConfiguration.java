package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANUIConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons.ProtegeClusterOptionsPanel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeTANUIConfiguration extends OWLTANUIConfiguration {

    public ProtegeTANUIConfiguration(
            OWLTANConfiguration config, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener, 
            OWLAbNFrameManager frameManager,
            boolean showingBandTAN) {
        
        super(config, 
                new ProtegeTANListenerConfiguration(config, workspace), 
                displayListener, 
                frameManager, 
                showingBandTAN);
    }
    
    @Override
    public NodeOptionsPanel getNodeOptionsPanel() {
        return new ProtegeClusterOptionsPanel(getConfiguration(), getConfiguration().getAbstractionNetwork().isAggregated());
    }
}
