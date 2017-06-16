package owl.cs.hypothesis.browser;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

public class KnowledgeComponent {
	
	private final String sourceLabel;
	private final String clLabel;
	private final OWLEntity cl;
	
	Set<OWLAxiom> usage = new HashSet<>();
	Set<OWLAxiom> hypotheses = new HashSet<>();

	public KnowledgeComponent(String sourceLabel, OWLEntity e, String clLabel) {
		this.sourceLabel = sourceLabel;
		this.clLabel = clLabel;
		this.cl = e;
		
	}

	public void setUsage(Set<OWLAxiom> use) {
		usage.clear();
		usage.addAll(use);
	}

	public void setHypotheses(Set<OWLAxiom> hypos) {
		hypotheses.clear();
		hypotheses.addAll(hypos);
	}

	public String getSourceLabel() {
		return sourceLabel;
	}

	public Set<OWLAxiom> getUsage() {
		return usage;
	}

	public String getEntityLabel() {
		return clLabel;
	}

	public Set<OWLAxiom> getHypotheses() {
		return hypotheses;
	}

	public OWLEntity getOWLClass() {
		return cl;
	}

}
