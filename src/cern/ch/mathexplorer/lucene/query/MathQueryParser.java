package cern.ch.mathexplorer.lucene.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import cern.ch.mathexplorer.mathematica.MathematicaConfig;
import cern.ch.mathexplorer.mathematica.MathematicaEngine;
import cern.ch.mathexplorer.mathematica.StructuralPattern;
import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.Constants.CHARACTER_CATEGORIES;
import cern.ch.mathexplorer.utils.Constants.MATH_FIELD;
import cern.ch.mathexplorer.utils.Regex;

public class MathQueryParser extends QParser {
	static Logger aLogger = java.util.logging.Logger
			.getLogger(MathQueryParser.class.getName());

	public MathQueryParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		super(qstr, localParams, params, req);
	}

	private static SnuggleEngine engine = new SnuggleEngine();

	public void addNotationalTokens(BooleanQuery query, String mathML, MATH_FIELD field) {
		Collection<String> tokenInQuery = Regex.extractElements(mathML);
		for (String mathMLToken : tokenInQuery) {
			query.add(new BooleanClause(new TermQuery(new Term(
					field.getName(), mathMLToken)),
					Occur.SHOULD));
			addSynonyms(mathMLToken, query, field);
			Console.print(mathMLToken);
		}
	}

	void addSynonyms(String mathMLToken, BooleanQuery query, MATH_FIELD field) {
		if (mathMLToken.startsWith("<mo>") && mathMLToken.endsWith("</mo>")) { // OPERATOR CATEGORY TOKEN
			String operator = mathMLToken.replace("<mo>", "").replace("</mo>", "");
			CHARACTER_CATEGORIES category = null;
			if ((category = (Constants.CHARACTERS_TO_CATEGORY.get(operator))) != null) {
				query.add(new BooleanClause(new TermQuery(new Term(
						field.getName(), category.name())),
						Occur.SHOULD));
			}
		}
	}

	void addStructuralPatterns(BooleanQuery query, String mathML){
		List<StructuralPattern> patterns = new ArrayList<>();
		try {
			patterns = MathematicaEngine.getInstance("QUERY")
					.getPatternsWithTimeout(mathML);
			for (StructuralPattern pattern : patterns) {
				query.add(new BooleanClause(
						new TermQuery(new Term(
								MATH_FIELD.MATH_STRUCTURAL_FIELD.getName(), pattern
										.getPattern())), Occur.SHOULD));
			}
		} catch (Exception e) {
		}

	}
	
	private String fromLatexToMathML() throws SyntaxError {
		qstr = qstr.replace("FORMAT(latex)", "");
		if (engine == null) {
			engine = new SnuggleEngine();
		}
		try {
			qstr = texToMathML(qstr);
			return qstr;
		} catch (IOException e) {
			e.printStackTrace();
			throw new SyntaxError("Error parsing LaTeX: " + e.getMessage());
		}
	} 

	@Override
	public Query parse() throws SyntaxError {
		aLogger.info("Query before: " + qstr);
		qstr = Regex.cleanQuery(qstr);

		if (qstr.contains("FORMAT(latex)")) {
			qstr = fromLatexToMathML();
		}
		else {
			qstr = qstr.replace("FORMAT(mathml)", "");
		}

		aLogger.info("Query after: " + qstr);

		BooleanQuery query = new BooleanQuery();
		addNotationalTokens(query, qstr, MATH_FIELD.MATH_NOTATIONAL_FIELD);
		if (MathematicaConfig.USE_MATHEMATICA) {
			String normalizedStr = MathematicaEngine.getInstance("QUERY")
					.simplyExpressionWithTimeout(qstr);
			addStructuralPatterns(query, qstr);
			addNotationalTokens(query, normalizedStr, MATH_FIELD.MATH_NORMALIZED_NOTATIONAL_FIELD);
		}
		for (BooleanClause a : query.getClauses()) {
			Console.print(a);
		}
		query.setMinimumNumberShouldMatch(1);

		return query;
	}

	public static String texToMathML(String texText) throws IOException {
		texText = texText.trim();
		texText.replaceAll("\\$\\$", "$");
		if (!texText.endsWith("$")) {
			texText += "$";
		}

		if (!texText.startsWith("$")) {
			texText = "$" + texText;
		}

		SnuggleSession session = engine.createSession();
		session.parseInput(new SnuggleInput(texText));
		String result = session.buildXMLString();
		aLogger.info(texText + " --> \n" + result);
		return result;
	}

	public static void main(String[] args) throws Exception {
		String query = Regex.cleanQuery(texToMathML("\\left (-\\frac{\\hbar^{2}}{2m_e}\\nabla^{2} + V(\\mathbf{r})\\right )\\psi(\\mathbf{r}) = E\\psi(\\mathbf{r})"));
		MathQueryParser mqp = new MathQueryParser(query, null, null, null);
		mqp.parse();
	}
}
