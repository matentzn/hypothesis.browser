package owl.cs.hypothesis.browser;

import org.semanticweb.owlapi.model.OWLEntity;

public class BrowserEntity {
	
	OWLEntity e;
	String label;
	
	BrowserEntity(OWLEntity e,String label) {
		this.e = e;
		this.label= label;
	}

	@Override
	public String toString() {
		return label;
	}
	
	
}
