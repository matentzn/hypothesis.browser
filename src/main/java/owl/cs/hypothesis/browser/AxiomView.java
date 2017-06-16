package owl.cs.hypothesis.browser;

import org.semanticweb.owlapi.model.OWLAxiom;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class AxiomView extends VerticalLayout {

	private static final long serialVersionUID = -8664522059279667013L;

	public AxiomView(OWLAxiom ax) {
		String axs = Utils.render(ax);
		Label l = new Label(
				"<div style=\"background-color:rgb(255, 255, 204);margin: 10px 5px 5px 5px;padding: 10px 5px 5px 5px;font-size:20px;border-style: solid;border-width: 5px;\">"
						+ axs + "</div>",
				ContentMode.HTML);
		addComponent(l);
	}

}
