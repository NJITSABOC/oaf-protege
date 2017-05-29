package edu.njit.cs.saboc.blu.owl.protege.configuration;

import edu.njit.cs.saboc.blu.core.abn.tan.ClusterTribalAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLAbNFrameManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.tan.configuration.OWLTANTextConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeTANConfigurationFactory {
    
    public OWLTANConfiguration createConfiguration(
            ClusterTribalAbstractionNetwork tan, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener,
            OWLAbNFrameManager frameManager,
            boolean showingBandTAN) {

        OWLTANConfiguration tanConfiguration = new OWLTANConfiguration(tan);
        
        tanConfiguration.setUIConfiguration(new ProtegeTANUIConfiguration(
                tanConfiguration, 
                workspace,
                displayListener, 
                frameManager, 
                showingBandTAN));
        
        tanConfiguration.setTextConfiguration(new OWLTANTextConfiguration(tan));

        return tanConfiguration;
    }
}
