package edu.njit.cs.saboc.blu.owl.protege.configuration.gui.buttons;

import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.InheritableProperty;
import edu.njit.cs.saboc.blu.core.abn.pareataxonomy.PArea;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetwork;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetAbstractionNetworkGenerator;
import edu.njit.cs.saboc.blu.core.abn.targetbased.TargetGroup;
import edu.njit.cs.saboc.blu.core.datastructure.hierarchy.Hierarchy;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeOptionsPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.NodeDashboardPanel;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.CreateDisjointAbNFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.CreateTANFromSinglyRootedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.ExportPartitionedNodeButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.node.NodeHelpButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.optionbuttons.PopoutDetailsButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateAncestorSubtaxonomyButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateExpandedSubtaxonomyButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateRootSubtaxonomyButton;
import edu.njit.cs.saboc.blu.core.gui.gep.panels.details.pareataxonomy.buttons.CreateTargetAbNFromPAreaButton;
import edu.njit.cs.saboc.blu.core.ontology.Concept;
import edu.njit.cs.saboc.blu.owl.abn.OWLAbstractionNetwork;
import edu.njit.cs.saboc.blu.owl.abn.pareataxonomy.OWLInheritableProperty;
import edu.njit.cs.saboc.blu.owl.abn.range.OWLRangeAbNFromPAreaFactory;
import edu.njit.cs.saboc.blu.owl.abn.tan.OWLTANFactory;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.buttons.OWLOpenBrowserButton;
import edu.njit.cs.saboc.blu.owl.gui.gep.panels.pareataxonomy.configuration.OWLPAreaTaxonomyConfiguration;
import edu.njit.cs.saboc.blu.owl.gui.listeners.DisplayPAreaTaxonomyAction;
import edu.njit.cs.saboc.blu.owl.gui.listeners.DisplayTANAction;
import edu.njit.cs.saboc.blu.owl.ontology.OWLConcept;
import edu.njit.cs.saboc.blu.owl.utils.owlproperties.PropertyTypeAndUsage;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Chris O
 */
public class ProtegePAreaOptionsPanel extends NodeOptionsPanel<PArea> {
    
    public ProtegePAreaOptionsPanel(OWLPAreaTaxonomyConfiguration config, boolean forAggregate) {
        
        if (forAggregate) {
            CreateExpandedSubtaxonomyButton expandedSubtaxonomyBtn = new CreateExpandedSubtaxonomyButton(config,
                    new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));

            super.addOptionButton(expandedSubtaxonomyBtn);
        } else {
            CreateDisjointAbNFromSinglyRootedNodeButton<PArea> createDisjointBtn = new CreateDisjointAbNFromSinglyRootedNodeButton<>(
                    config,
                    (disjointTaxonomy) -> {
                        config.getUIConfiguration().getAbNDisplayManager().displayDisjointPAreaTaxonomy(disjointTaxonomy);
                    });

            super.addOptionButton(createDisjointBtn);
        }
        
        
        CreateRootSubtaxonomyButton rootSubtaxonomyBtn = new CreateRootSubtaxonomyButton(config,
            new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(rootSubtaxonomyBtn);
        
        
        CreateAncestorSubtaxonomyButton ancestorSubtaxonomyBtn = new CreateAncestorSubtaxonomyButton(config, 
            new DisplayPAreaTaxonomyAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(ancestorSubtaxonomyBtn);
        
        
        OWLAbstractionNetwork owlAbN = (OWLAbstractionNetwork)config.getPAreaTaxonomy();
        
        CreateTANFromSinglyRootedNodeButton createTANBtn = new CreateTANFromSinglyRootedNodeButton(
                new OWLTANFactory(owlAbN.getDataManager()),
                config, 
            new DisplayTANAction(config.getUIConfiguration().getAbNDisplayManager()));
        
        super.addOptionButton(createTANBtn);
        
        
        CreateTargetAbNFromPAreaButton targetAbNBtn = new CreateTargetAbNFromPAreaButton() {

            @Override
            protected Set<InheritableProperty> getUsableProperties(PArea parea) {
                
                 Set<InheritableProperty> validProperties = parea.getRelationships().stream().filter( (property) -> {
                     
                    OWLInheritableProperty owlProperty = (OWLInheritableProperty)property;
                    
                    return owlProperty.getPropertyTypeAndUsage().equals(PropertyTypeAndUsage.OP_EQUIV) ||
                            owlProperty.getPropertyTypeAndUsage().equals(PropertyTypeAndUsage.OP_RESTRICTION);
                 }).collect(Collectors.toSet());
                
                return validProperties;
            }

            @Override
            protected void createAndDisplayTargetAbN(InheritableProperty property) {

                OWLRangeAbNFromPAreaFactory rangeFactory = new OWLRangeAbNFromPAreaFactory(
                        config.getPAreaTaxonomy().getDerivation(),
                        config.getTaxonomy().getDataManager(),
                        (OWLConcept)this.getCurrentEntity().get().getRoot(),
                        (OWLInheritableProperty)property);

                TargetAbstractionNetworkGenerator generator = new TargetAbstractionNetworkGenerator();

                TargetAbstractionNetwork<TargetGroup> rangeAbN = generator.deriveTargetAbNFromPArea(
                        rangeFactory, 
                        config.getPAreaTaxonomy(), 
                        this.getCurrentEntity().get(), 
                        property, 
                        (Hierarchy<Concept>) (Hierarchy<?>)config.getTaxonomy().getDataManager().getOntology().getConceptHierarchy());
                
                config.getUIConfiguration().getAbNDisplayManager().displayTargetAbstractionNetwork(rangeAbN);
                
            }
        };

        super.addOptionButton(targetAbNBtn);


        PopoutDetailsButton popoutBtn = new PopoutDetailsButton("partial-area", () -> {
            PArea parea = (PArea)super.getCurrentNode().get();
            
            NodeDashboardPanel anp = config.getUIConfiguration().createNodeDetailsPanel();
            anp.setContents(parea);

            return anp;
        });

        super.addOptionButton(popoutBtn);
        
        
        ExportPartitionedNodeButton exportBtn = new ExportPartitionedNodeButton(config);
        
        super.addOptionButton(exportBtn);
        
        
        NodeHelpButton helpBtn = new NodeHelpButton(config);

        super.addOptionButton(helpBtn);
    }
}
