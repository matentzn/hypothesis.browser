package owl.cs.hypothesis.browser;

import org.semanticweb.owlapi.model.OWLAxiom;

public interface HypothesisI {

	int getSupport();

	int getAssumption();

	double getConfidence();

	double getLift();

	OWLAxiom getAxiom();

}
