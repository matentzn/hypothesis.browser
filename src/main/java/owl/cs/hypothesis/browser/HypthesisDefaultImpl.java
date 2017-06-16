package owl.cs.hypothesis.browser;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;

public class HypthesisDefaultImpl implements HypothesisI {
	final OWLAxiom h;
	int support;
	int assumption;
	double confidence;
	double lift;

	public HypthesisDefaultImpl(OWLAxiom h) {
		this.h = h;
		for (OWLAnnotation a : h.getAnnotations()) {
			OWLAnnotationProperty p = a.getProperty();
			if (p.equals(Utils.getDf().getOWLAnnotationProperty(IRI.create("http://www.dlminer.io#support")))) {
				support = (int)a.getValue().asLiteral().orNull().parseDouble();
			} else if (p
					.equals(Utils.getDf().getOWLAnnotationProperty(IRI.create("http://www.dlminer.io#assumption")))) {
				assumption = (int)a.getValue().asLiteral().orNull().parseDouble();
			} else if (p
					.equals(Utils.getDf().getOWLAnnotationProperty(IRI.create("http://www.dlminer.io#precision")))) {
				confidence = a.getValue().asLiteral().orNull().parseDouble();
			} else if (p.equals(Utils.getDf().getOWLAnnotationProperty(IRI.create("http://www.dlminer.io#lift")))) {
				lift = a.getValue().asLiteral().orNull().parseDouble();
			}
		}
	}

	@Override
	public String toString() {
		return Utils.render(h);
	}

	@Override
	public int getSupport() {
		return support;
	}

	@Override
	public int getAssumption() {
		return assumption;
	}

	@Override
	public double getConfidence() {
		return confidence;
	}

	@Override
	public double getLift() {
		return lift;
	}

	@Override
	public OWLAxiom getAxiom() {
		return h;
	}

}
