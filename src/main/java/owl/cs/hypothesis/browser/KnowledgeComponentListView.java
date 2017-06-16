package owl.cs.hypothesis.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class KnowledgeComponentListView extends VerticalLayout {

	private static final long serialVersionUID = -5616618569949522746L;
	
	List<KnowledgeComponent> components = new ArrayList<>();
	
	public void resetComponents(List<KnowledgeComponent> components,KnowledgeComponentViewFilter kcvf, Set<AxiomType> excludedaxiomtypes) {
		this.components.clear();
		this.components.addAll(components);
		initView(kcvf,excludedaxiomtypes);
	}

	private void initView(KnowledgeComponentViewFilter kcvf, Set<AxiomType> excludedaxiomtypes) {
		removeAllComponents();
		for(KnowledgeComponent kc:components) {
			addComponent(new KnowledgeComponentView(kc,kcvf,excludedaxiomtypes));
		}
	}

	public void filter(KnowledgeComponentViewFilter kcvf, Set<AxiomType> excludedaxiomtypes) {
		for(int i = 0;i<getComponentCount();i++) {
			Component c = getComponent(i);
			if(c instanceof KnowledgeComponentView) {
				((KnowledgeComponentView)c).filter(kcvf,excludedaxiomtypes);
			}
		}
	}
	
}
