
package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.range.configuration.OWLRangeAbNConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.range.configuration.OWLRangeAbNUIConfiguration;
import edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons.ProtegeTargetGroupOptionsPanel;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeRangeAbNUIConfiguration extends OWLRangeAbNUIConfiguration {

    public ProtegeRangeAbNUIConfiguration(
            OWLRangeAbNConfiguration config, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener, 
            OWLAbNFrameManager frameManager) {
        
        super(config, 
                new ProtegeRangeAbNListenerConfiguration(config), 
                displayListener, 
                frameManager);
    }
    
    @Override
    public NodeOptionsPanel getNodeOptionsPanel() {
        return new ProtegeTargetGroupOptionsPanel(this.getConfiguration());
    }
}
