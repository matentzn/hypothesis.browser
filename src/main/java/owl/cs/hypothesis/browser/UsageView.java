package owl.cs.hypothesis.browser;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAxiom;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class UsageView extends VerticalLayout {

	private static final long serialVersionUID = -5715596746201303104L;

	public UsageView(List<OWLAxiom> usagesub) {
		Panel panel = new Panel();
		panel.setHeight("200px");
		panel.setWidth("800px");

		panel.setScrollTop(0);
		VerticalLayout vl = new VerticalLayout();
		
		setCaption("Usage");
		

		for (OWLAxiom ax : usagesub) {
			vl.addComponent(new AxiomView(ax));
		}
		panel.setContent(vl);
		//panel.getContent().setHeight("600px");
		addComponent(panel);
	}

}
