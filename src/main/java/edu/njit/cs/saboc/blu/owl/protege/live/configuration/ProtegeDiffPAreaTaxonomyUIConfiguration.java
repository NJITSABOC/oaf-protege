
package edu.njit.cs.saboc.blu.owl.protege.live.configuration;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.core.gui.dialogs.concepthierarchy.ConceptPainter;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.models.OAFAbstractTableModel;
import edu.njit.cs.saboc.blu.core.gui.graphframe.AbNDisplayManager;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.OWLPropertyTableModel;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.diff.configuration.OWLDiffPAreaTaxonomyUIConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeDiffPAreaTaxonomyUIConfiguration extends OWLDiffPAreaTaxonomyUIConfiguration {
    
    public ProtegeDiffPAreaTaxonomyUIConfiguration(
            ProtegeDiffPAreaTaxonomyConfiguration config,
            ProtegeDiffPAreaTaxonomyListenerConfiguration listenerConfig,
            AbNDisplayManager displayListener) {

        super(config,
                listenerConfig,
                displayListener);
    }

    public ProtegeDiffPAreaTaxonomyUIConfiguration(
            ProtegeDiffPAreaTaxonomyConfiguration config, 
            OWLWorkspace workspace,
            AbNDisplayManager displayListener) {
        
        this(config, 
                new ProtegeDiffPAreaTaxonomyListenerConfiguration(config, workspace), 
                displayListener);
    }
    
    @Override
    public ProtegeDiffPAreaTaxonomyConfiguration getConfiguration() {
        return (ProtegeDiffPAreaTaxonomyConfiguration)super.getConfiguration();
    }

    @Override
    public OAFAbstractTableModel<InheritableProperty> getPropertyTableModel(boolean forArea) {
        return new OWLPropertyTableModel(forArea);
    }

    @Override
    public ConceptPainter getConceptHierarchyPainter() {
        return new ConceptPainter();
    }
}
