package owl.cs.hypothesis.browser;

import java.io.File;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("owlcs")
public class HypothesisBrowserUI extends UI {

	private static final long serialVersionUID = 1204307482865521119L;

	final Label name = new Label("<h1>Hypothesis Browser</h1>", ContentMode.HTML);

	

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();
		File tempdir;
		try {
			tempdir = getTempDir();
			layout.addComponents(name, new HypothesisBrowserView(tempdir));
		} catch (IOException e) {
			layout.addComponent(new Label("Config directory not found"));
			e.printStackTrace();
		}
		
		layout.setMargin(true);
		layout.setSpacing(true);

		setContent(layout);
	}

	private File getTempDir() throws IOException {
		File tempdir = new File("D:\\svn\\cerner\\misc\\discern-ontology\\discern-owl");
		if(tempdir.exists()) {
			return tempdir;
		} 
		tempdir = new File("/home/zeus/cerner/discern/discern-owl/");
		if(tempdir.exists()) {
			return tempdir;
		} else {
			throw new IOException("Configuration Directory not found!");
		}
	}

	@WebServlet(urlPatterns = "/*", name = "HypothesisBrowserUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = HypothesisBrowserUI.class, productionMode = false)
	public static class HypothesisBrowserUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 2346525150981945148L;
	}
}
