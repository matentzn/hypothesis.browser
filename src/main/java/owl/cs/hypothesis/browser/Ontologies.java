package owl.cs.hypothesis.browser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.AutoIRIMapper;

public class Ontologies {

	public static final Map<String, Set<OWLEntity>> map_entity_label = new HashMap<>();
	public static final Map<String, OWLOntology> map_ontologies = new HashMap<>();
	public static final Set<AxiomType> axiomtypes = new HashSet<AxiomType>();
	public final static Map<OWLEntity, String> replacements = new HashMap<>();
	private static boolean isPrepared = false;
	
	public static void assertReplacementPatterns(OWLOntology o) {
		for (OWLEntity e : o.getSignature()) {
			String rep = e.getIRI().getRemainder().or(e.getIRI().toString());
			String label = getLabel(e, o);
			if (!rep.isEmpty() && !label.isEmpty()) {
				replacements.put(e, label);
			}
		}
	}
	
	public static void refreshOntologies(File versions) {
		replacements.clear();
		map_entity_label.clear();
		map_ontologies.clear();
		axiomtypes.clear();
		
		for (File file : versions.listFiles()) {
			if ((file.getName().startsWith("AC-") || file.getName().startsWith("E-"))
					&& file.getName().endsWith(".owl")) {
				OWLOntology o = loadOntology(file);
				map_ontologies.put(file.getName(), o);
				assertReplacementPatterns(o);
				o.getLogicalAxioms(Imports.INCLUDED).forEach(ax->axiomtypes.add(ax.getAxiomType()));
				populateLabels(o);
			}
		}
		
		
		isPrepared = true;
	}
	
	public static OWLOntology loadOntology(File f) {
		try {
			AutoIRIMapper am = new AutoIRIMapper(f.getParentFile(), true);
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
			man.addIRIMapper(am);
			return man.loadOntologyFromOntologyDocument(f);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isPrepared() {
		return isPrepared;
	}
	
	public static String getLabel(OWLEntity c, OWLOntology o) {
		for (OWLAnnotation a : EntitySearcher.getAnnotations(c, o,
				o.getOWLOntologyManager().getOWLDataFactory().getRDFSLabel())) {
			OWLAnnotationValue val = a.getValue();
			if (val instanceof OWLLiteral) {
				return ((OWLLiteral) val).getLiteral();
			}
		}
		return "";
	}
	
	private static void populateLabels(OWLOntology o) {
		for (OWLEntity c : o.getSignature(Imports.INCLUDED)) {
			String label = getLabel(c, o);
			if (label.isEmpty()) {
				label = c.getIRI().getRemainder().or(c.getIRI().toString());
			}
			if (!map_entity_label.containsKey(label)) {
				map_entity_label.put(label, new HashSet<>());
			}
			map_entity_label.get(label).add(c);
		}
	}
	
	public static void getAllLabels(Collection<String> labels) {
		map_entity_label.keySet().forEach(l->labels.add(l));
	}

	public static void resetPrepared() {
		isPrepared = false;
	}
}
