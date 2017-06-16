package owl.cs.hypothesis.browser;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;

public class Utils {

	public final static OWLObjectRenderer dlrender = new DLSyntaxObjectRenderer();
	private final static OWLDataFactory df = OWLManager.getOWLDataFactory();
	

	public static String render(OWLAxiom ax) {
		String s = dlrender.render(ax);
		for (OWLEntity e : ax.getSignature()) {
			if(Ontologies.replacements.containsKey(e)) {
				s.replaceAll(e.getIRI().getRemainder().or(""), Ontologies.replacements.get(e));
			}
		}
		return s;
	}
	

	public static OWLDataFactory getDf() {
		return df;
	}
}
