package cern.ch.mathexplorer.webbeans;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;

import cern.ch.mathexplorer.core.Constants;
import cern.ch.mathexplorer.core.EquationResult;
import cern.ch.mathexplorer.core.MathExplorer;

@ManagedBean(name="mathBean")
@SessionScoped
public class MathBean implements Serializable {
	private static final long serialVersionUID = 1L;
	String queryText;
	// The rewrote and (maybe) translated query. The one that is passed to the search engine
	String finalQueryText;
	String [] sampleEquations;
	transient MathExplorer mathExplorer;
	String message;
	List<EquationResult> queryResult;
	List<SelectItem> inputFormats = new ArrayList<SelectItem>();
	String inputFormat;
	
	public MathBean() {
		ServletContext servletContext = (ServletContext) FacesContext
			    .getCurrentInstance().getExternalContext().getContext();
		mathExplorer = MathExplorer.getInstance(servletContext);
		queryText = "";
		sampleEquations = Constants.SAMPLE_EQUATIONS;
		queryResult = new ArrayList<>();
		message = "";
		inputFormat = Constants.MATHML;
		inputFormats.add(new SelectItem(Constants.MATHML));
		inputFormats.add(new SelectItem(Constants.LATEX));
	}
	
	public String search() {
		try {
			finalQueryText = URLDecoder.decode(queryText, "UTF-8");
			if (inputFormat.equals(Constants.LATEX)) {
				finalQueryText = mathExplorer.texToMathML(finalQueryText);
			}
			queryResult = mathExplorer.search(finalQueryText);
			if (queryResult.isEmpty()) {
				message = "Your query did not produced any result";
			} else {
				message = "Ok";
			}
			return "query";
		} catch (Exception e) {
			message = "There was an error performing while processing your query: " + e.getMessage();
			e.printStackTrace();
			return "query";
		}
	}
	
	
	
	public String[] getSampleEquations() {
		return sampleEquations;
	}
	
	public void setSampleEquations(String[] sampleEquations) {
		this.sampleEquations = sampleEquations;
	}

	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<EquationResult> getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(List<EquationResult> queryResult) {
		this.queryResult = queryResult;
	}

	public List<SelectItem> getInputFormats() {
		return inputFormats;
	}

	public void setInputFormats(List<SelectItem> inputFormats) {
		this.inputFormats = inputFormats;
	}

	public String getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public String getFinalQueryText() {
		return finalQueryText;
	}

	public void setFinalQueryText(String finalQueryText) {
		this.finalQueryText = finalQueryText;
	}
	
}
