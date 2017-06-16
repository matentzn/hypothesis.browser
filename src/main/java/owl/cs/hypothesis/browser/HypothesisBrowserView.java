package owl.cs.hypothesis.browser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;

public class HypothesisBrowserView extends VerticalLayout {

	final File versions;
	final File hypotheses;
	// final OntologyUploader receiver;
	Upload upload;

	VerticalLayout vl_configuration = new VerticalLayout();
	VerticalLayout vl_hypotheses = new VerticalLayout();
	VerticalLayout vl_downloads = new VerticalLayout();
	VerticalLayout vl_filtering = new VerticalLayout();
	HorizontalLayout vl_doselect = new HorizontalLayout();

	Slider sl_maxhyp = new Slider(1, 200);
	Slider sl_maxuse = new Slider(1, 100);
	Slider sl_minconfidence = new Slider(0.7, 1.0, 3);
	Slider sl_minassumption = new Slider(0, 100);
	Slider sl_minsupport = new Slider(1, 100);
	Slider sl_minlift = new Slider(0.0, 100.0, 1);

	Button bt_refresh = new Button("Reload ontologies");
	Button bt_download = new Button("Download");

	ComboBox cb_do = new ComboBox("DO Fragment");
	ListSelect select = new ListSelect("Exclude Axiom Types");
	AutocompleteTextField ac_entityselect = new AutocompleteTextField("Select entity");

	KnowledgeComponentListView list_kcomps = new KnowledgeComponentListView();

	/**
	 * 
	 */
	private static final long serialVersionUID = -2884436400103864837L;
	private static final Double MAX_VALUE = 99999.0;

	public HypothesisBrowserView(File tempdir) {
		versions = new File(tempdir, "versions");
		hypotheses = new File(tempdir, "hypotheses");
		// setMargin(true);
		setSpacing(true);
		layoutSelectorPanel();
		layoutConfiguration();
		layoutKnowledgePanel();
		layoutDownload();
		bt_refresh.addClickListener(e->{Ontologies.resetPrepared();update(AppStatus.INIT);});
		
		update(AppStatus.INIT);
	}

	private void update(AppStatus status) {
		switch (status) {
		case INIT:

			if (!Ontologies.isPrepared()) {
				Ontologies.refreshOntologies(versions);
			}

			removeAllComponents();
			addComponent(bt_refresh);
			addComponent(vl_doselect);
			prepareEntitySelector();
			
			break;
		case DOSELECTED:

			// prepareEntitySelector();
			break;
		case ENTITYSELECTED:
			prepareKnowledge();
			prepareAxiomTypeSelector();
			addComponent(vl_configuration);
			addComponent(vl_hypotheses);
			// addComponent(vl_downloads);
			break;
		case SETTINGSCHANGE:
			list_kcomps.filter(getKnowledgeComponentFilter(), getExcludedAxiomTypes());
			break;
		default:
			break;

		}
	}

	private KnowledgeComponentViewFilter getKnowledgeComponentFilter() {
		KnowledgeComponentViewFilter kcvf = new KnowledgeComponentViewFilter();
		kcvf.setMaxHypotheses(getMaxHypothesisValue());
		kcvf.setMaxUsage(getMaxUsageValue());
		kcvf.setMinAssumption(getMinAssumptionValue());
		kcvf.setMinLift(getMinLiftValue());
		kcvf.setMinPrecision(getMinPrecisionValue());
		kcvf.setMinSupport(getMinSupportValue());
		return kcvf;
	}

	private void prepareKnowledge() {
		String label = getSelectedEntity();
		List<KnowledgeComponent> components = new ArrayList<>();
		for (OWLEntity cl : Ontologies.map_entity_label.get(label)) {
			for (File file : versions.listFiles()) {
				if (file.getName().startsWith("E-") && file.getName().endsWith(".owl")) {
					KnowledgeComponent kc = new KnowledgeComponent(file.getName(), cl, label);
					OWLOntology o = Ontologies.map_ontologies.get(file.getName());
					Set<OWLAxiom> use = getUsage(o, cl);
					kc.setUsage(use);
					File hyp = new File(hypotheses, file.getName().replaceAll(".owl", "") + "-hypotheses.owl");
					if (hyp.exists()) {
						OWLOntology ho = Ontologies.loadOntology(hyp);
						Set<OWLAxiom> useh = getUsage(ho, cl);
						kc.setHypotheses(useh);
					}
					components.add(kc);
				}
			}
		}
		list_kcomps.resetComponents(components, getKnowledgeComponentFilter(), getExcludedAxiomTypes());
	}

	private Set<OWLAxiom> getUsage(OWLOntology o, OWLEntity cl) {
		Set<OWLAxiom> usage = new HashSet<>();
		for (OWLLogicalAxiom ax : o.getLogicalAxioms(Imports.INCLUDED)) {
			if (ax.containsEntityInSignature(cl)) {
				usage.add(ax);
			}
		}
		return usage;
	}

	private String getSelectedEntity() {
		return ac_entityselect.getValue();
	}

	private String getSelectedDO() {
		return (String) cb_do.getValue();
	}

	private void prepareDOSelector() {
		for (File file : versions.listFiles()) {
			if (file.getName().startsWith("AC-") && file.getName().endsWith(".owl")) {
				cb_do.addItem(file.getName());
			}
		}
		cb_do.addValueChangeListener(e -> update(AppStatus.DOSELECTED));
	}

	private void layoutKnowledgePanel() {
		vl_hypotheses.setSpacing(true);
		vl_hypotheses.setMargin(true);
		vl_hypotheses.addComponent(list_kcomps);
	}

	private void layoutSelectorPanel() {
		// vl_doselect.setMargin(true);
		// vl_doselect.setSpacing(true);
		// vl_doselect.addComponent(cb_do);
		
		vl_doselect.addComponent(ac_entityselect);
		
	}

	private void prepareEntitySelector() {

		Collection<String> labels = new ArrayList<>();

		Ontologies.getAllLabels(labels);

		AutocompleteSuggestionProvider suggestionProvider = new CollectionSuggestionProvider(labels, MatchMode.CONTAINS,
				true, Locale.US);

		ac_entityselect.setCache(true); // Client side should cache suggestions
		ac_entityselect.setDelay(150); // Delay before sending a query to the
		ac_entityselect.setItemAsHtml(false); // Suggestions contain html
		ac_entityselect.setMinChars(2); // The required value length to trigger
		ac_entityselect.setScrollBehavior(ScrollBehavior.NONE); // The method
		ac_entityselect.setSuggestionLimit(0); // The max amount of suggestions
		ac_entityselect.setSuggestionProvider(suggestionProvider);
		ac_entityselect.addValueChangeListener(e -> {
			update(AppStatus.ENTITYSELECTED);
		});
	}

	private void layoutDownload() {

		vl_downloads.addComponent(bt_download);

		StreamResource myResource = createResource();
		FileDownloader fileDownloader = new FileDownloader(myResource);
		fileDownloader.extend(bt_download);

	}

	private StreamResource createResource() {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {
				String text = "Hypotheses";

				OWLOntology exportedHypotheses = getExportedHypotheses();

				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					exportedHypotheses.getOWLOntologyManager().saveOntology(exportedHypotheses, os);
					return new ByteArrayInputStream(os.toByteArray());
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

		}, "hypotheses.owl");
	}

	private OWLOntology getExportedHypotheses() {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology o = man.createOntology();
			for (HypothesisI hyp : getHypotheses()) {
				man.addAxiom(o, hyp.getAxiom());
			}
			return o;
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private Set<HypothesisI> getHypotheses() {
		// TODO Auto-generated method stub
		return null;
	}

	private void layoutConfiguration() {
		vl_configuration.setMargin(true);
		vl_configuration.addComponent(new Label("Configuration"));

		VerticalLayout sliders = new VerticalLayout();
		layoutSliderOption("Maximum number of Hypotheses", sl_maxhyp, 10, sliders);
		layoutSliderOption("Maximum number of axioms in Usage", sl_maxuse, 10, sliders);
		layoutSliderOption("Minimum Precison", sl_minconfidence, 0.9, sliders);
		layoutSliderOption("Minimum Lift", sl_minlift, 1.0, sliders);
		layoutSliderOption("Minimum Support", sl_minsupport, 10, sliders);
		layoutSliderOption("Minimum Assumption", sl_minassumption, 0, sliders);

		VerticalLayout axiomtypes = new VerticalLayout();

		// Create the selection component

		axiomtypes.setMargin(true);
		select.setSizeFull();
		select.setMultiSelect(true);
		axiomtypes.addComponent(select);

		HorizontalLayout l = new HorizontalLayout();
		l.addComponent(sliders);
		l.addComponent(axiomtypes);
		vl_configuration.addComponent(l);
	}

	private void prepareAxiomTypeSelector() {
		for (AxiomType t : getAxiomTypes()) {
			select.addItem(t);
		}
		
		Set<AxiomType> defaultExclude = new HashSet<>();
		defaultExclude.addAll(AxiomType.ABoxAxiomTypes);
		select.setValue(defaultExclude);

		select.addValueChangeListener(event -> {
			update(AppStatus.SETTINGSCHANGE);
		});
	}

	private Set<AxiomType> getExcludedAxiomTypes() {
		return (Set<AxiomType>) select.getValue();
	}

	private Set<AxiomType> getAxiomTypes() {
		return Ontologies.axiomtypes;
	}

	private void layoutSliderOption(String caption, Slider c, double defaultValue, VerticalLayout layout) {
		HorizontalLayout hl = new HorizontalLayout();
		Label l = new Label(caption);
		l.setWidth("150px");
		TextField t_val = new TextField();

		hl.addComponent(l);
		hl.addComponent(c);
		hl.addComponent(t_val);
		c.setValue(defaultValue);
		c.setWidth("200px");
		t_val.setWidth("60px");
		t_val.setValue(c.getValue() + "");
		layout.addComponent(hl);
		c.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (c.getMin() == c.getValue()) {
					t_val.setValue("MIN");
				} else if (c.getMax() == c.getValue()) {
					t_val.setValue("MAX");
				} else {
					t_val.setValue(c.getValue() + "");
				}

				update(AppStatus.SETTINGSCHANGE);
			}
		});
	}

	private int getMinSupportValue() {
		return getMinValue(sl_minsupport).intValue();
	}

	private Double getMinValue(Slider sl) {
		Double value = sl.getValue();
		if (value == sl.getMin()) {
			return 0.0;
		} else if (value == sl.getMax()) {
			return MAX_VALUE;
		} else {
			return value;
		}
	}

	private int getMinAssumptionValue() {
		return getMinValue(sl_minassumption).intValue();
	}

	private double getMinLiftValue() {
		return getMinValue(sl_minlift);
	}

	private double getMinPrecisionValue() {
		return getMinValue(sl_minconfidence);
	}

	private int getMaxHypothesisValue() {
		return getMinValue(sl_maxhyp).intValue();
	}

	private int getMaxUsageValue() {
		return getMinValue(sl_maxuse).intValue();
	}

}
