package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.range.configuration.OWLRangeAbNConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.range.configuration.OWLRangeAbNTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeRangeAbNConfigurationFactory {
    
    public OWLRangeAbNConfiguration createConfiguration(
            TargetAbstractionNetwork targetAbN, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener, 
            OWLAbNFrameManager frameManager) {
        
        OWLRangeAbNConfiguration targetAbNConfiguration = new OWLRangeAbNConfiguration(targetAbN);
        targetAbNConfiguration.setUIConfiguration(
                new ProtegeRangeAbNUIConfiguration(
                        targetAbNConfiguration, 
                        workspace,
                        displayListener, 
                        frameManager));
        targetAbNConfiguration.setTextConfiguration(new OWLRangeAbNTextConfiguration(targetAbN));
        
        return targetAbNConfiguration;
    }
}
