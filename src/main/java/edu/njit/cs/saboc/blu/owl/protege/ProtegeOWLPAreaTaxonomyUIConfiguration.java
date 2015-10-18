package edu.njit.cs.saboc.blu.owl.protege;

import edu.njit.cs.saboc.blu.owl.gui.abnselection.OWLDisplayFrameListener;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyUIConfiguration;
import org.protege.editor.owl.model.OWLWorkspace;

/**
 *
 * @author Chris O
 */
public class ProtegeOWLPAreaTaxonomyUIConfiguration extends OWLPAreaTaxonomyUIConfiguration {
    public ProtegeOWLPAreaTaxonomyUIConfiguration(OWLPAreaTaxonomyConfiguration config, OWLDisplayFrameListener displayListener, OWLWorkspace workspace) {
        super(config, new ProtegeOWLPAreaTaxonomyListenerConfiguration(config, workspace), displayListener);
    }
}
