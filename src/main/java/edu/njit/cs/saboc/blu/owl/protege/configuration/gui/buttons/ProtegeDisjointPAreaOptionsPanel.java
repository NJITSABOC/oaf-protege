package edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons;

import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.disjoint.DisjointNode;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.DisjointPArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PAreaTaxonomy;
import edu.njit.cs.saboc.blu.core.abn.tan.TANFactory;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.disjointabn.buttons.CreateAncestorDisjointAbNButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.CreateTANFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.ExportSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.owl.abn.OWLAbstractionNetwork;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.disjointpareataxonomy.configuration.OWLDisjointPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.listeners.DisplayTANAction;

/**
 *
 * @author Chris O
 */
public class ProtegeDisjointPAreaOptionsPanel extends NodeOptionsPanel {

    public ProtegeDisjointPAreaOptionsPanel(OWLDisjointPAreaTaxonomyConfiguration config) {
        
        OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork)config.getAbstractionNetwork().getParentAbstractionNetwork();
        
        
        CreateAncestorDisjointAbNButton ancestorBtn = new CreateAncestorDisjointAbNButton(config, (disjointAbN) -> {

            DisjointAbstractionNetwork<DisjointNode<PArea>, PAreaTaxonomy<PArea>, PArea> disjointTaxonomy
                    = (DisjointAbstractionNetwork<DisjointNode<PArea>, PAreaTaxonomy<PArea>, PArea>) disjointAbN;

            config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointTaxonomy);
        });

        super.addOptionButton(ancestorBtn);
                
        
        CreateTANFromSinglyRootedNodeButton tanBtn = new CreateTANFromSinglyRootedNodeButton(
                new TANFactory(owlAbN.getDataManager().getOntology()),
                config, 
                new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(tanBtn);
        

        ExportSinglyRootedNodeButton exportBtn = new ExportSinglyRootedNodeButton(config);

        super.addOptionButton(exportBtn);
        

        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("disjoint partial-area", () -> {
            DisjointPArea parea = (DisjointPArea)super.getCurrentNode().get();
            
            NodeDashboardPanel anp = config.getUIConfiguration().createNodeDetailsPanel();
            anp.setContents(parea);

            return anp;
        });

        super.addOptionButton(popoutBtn);
        
        
        NodeHelpButton helpBtn = new NodeHelpButton(config);

        super.addOptionButton(helpBtn);
    }
}
