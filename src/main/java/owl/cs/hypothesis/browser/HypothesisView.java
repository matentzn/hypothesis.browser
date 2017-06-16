package owl.cs.hypothesis.browser;

import java.text.DecimalFormat;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class HypothesisView extends VerticalLayout {

	private static final long serialVersionUID = -5645805239999122426L;

	public HypothesisView(List<HypothesisI> hyposub) {
		Panel panel = new Panel();
		panel.setHeight("300px");
		panel.setWidth("800px");

		panel.setScrollTop(0);

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		// setMargin(true);
		setCaption("Hypotheses");

		for (HypothesisI h : hyposub) {
			vl.addComponent(new AxiomView(h.getAxiom()));
			String annos = "<ol><li>Support: " + h.getSupport() + "</li><li>Assumption: " + h.getAssumption()
					+ "</li><li>Confidence: "
					+ formatDouble(h.getConfidence()) + "</li><li>Lift: " + formatDouble(h.getLift()) + "</li></ol>";
			vl.addComponent(new Label(annos, ContentMode.HTML));
			vl.addComponent(new Label("<hr/>", ContentMode.HTML));
		}
		panel.setContent(vl);
		// panel.getContent().setHeight("600px");
		addComponent(panel);
	}

	private String formatDouble(Double value) {
		DecimalFormat df = new DecimalFormat("####0.00");
		return df.format(value);
	}

}
