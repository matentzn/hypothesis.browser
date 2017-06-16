package owl.cs.hypothesis.browser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class KnowledgeComponentView extends VerticalLayout {

	private static final long serialVersionUID = -3736753281975326725L;

	KnowledgeComponent kc;

	public KnowledgeComponentView(KnowledgeComponent kc, KnowledgeComponentViewFilter kcvf, Set<AxiomType> excludedaxiomtypes) {
		this.kc = kc;
		filter(kcvf,excludedaxiomtypes);
	}

	public void filter(KnowledgeComponentViewFilter kcvf, Set<AxiomType> excludedaxiomtypes) {
		removeAllComponents();

		VerticalLayout vl = new VerticalLayout();

		Label head = new Label("<h2>" + kc.getSourceLabel() + " (" + kc.getEntityLabel() + ")</h2>", ContentMode.HTML);
		vl.addComponent(head);
		List<OWLAxiom> usage = new ArrayList<>();
		for (OWLAxiom axu : kc.getUsage()) {
			if (!excludedaxiomtypes.contains(axu.getAxiomType())) {
				usage.add(axu);
			}
		}
		/// sort usage?
		List<OWLAxiom> usagesub = new ArrayList<>();
		if (usage.size() > kcvf.getMaxUsage()) {
			usagesub.addAll(usage.subList(0, kcvf.getMaxUsage()));
		} else {
			usagesub.addAll(usage);
		}

		List<HypothesisI> hypotheses = new ArrayList<>();
		for (OWLAxiom ax : kc.getHypotheses()) {
			HypothesisI h = new HypthesisDefaultImpl(ax);
			if (h.getAssumption() > kcvf.getMinAssumption()) {
				if (h.getLift() > kcvf.getMinLift()) {
					if (h.getConfidence() > kcvf.getMinPrecision()) {
						if (h.getSupport() > kcvf.getMinSupport()) {
							if (!excludedaxiomtypes.contains(h.getAxiom().getAxiomType())) {
								hypotheses.add(h);
							}

						}
					}
				}
			}
		}
		List<HypothesisI> hypothesessub = new ArrayList<>();
		if (hypotheses.size() > kcvf.getMaxUsage()) {
			hypothesessub.addAll(hypotheses.subList(0, kcvf.getMaxHypotheses()));
		} else {
			hypothesessub.addAll(hypotheses);
		}

		vl.addComponent(new UsageView(usagesub));
		vl.addComponent(new Label(""));
		vl.addComponent(new HypothesisView(hypothesessub));
		addComponent(vl);
	}

}
