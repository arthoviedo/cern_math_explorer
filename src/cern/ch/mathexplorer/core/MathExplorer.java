package cern.ch.mathexplorer.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Appender;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import cern.ch.mathexplorer.core.EquationResult.EquationBuilder;
import cern.ch.mathexplorer.lucene.analysis.VecTextField;
import cern.ch.mathexplorer.lucene.analysis.analyzers.SolrNotationalAnalyzer;
import cern.ch.mathexplorer.lucene.analysis.analyzers.SolrNormalizerNotationalAnalyzer;
import cern.ch.mathexplorer.lucene.analysis.analyzers.SolrStructuralAnalyzer;
import cern.ch.mathexplorer.lucene.query.MathQueryParser;
import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.Constants.MATH_FIELD;
import cern.ch.mathexplorer.utils.Regex;

public class MathExplorer {
	private static final String EQUATION_EXTENSION = ".eq";
	private static final String LINE = "line";
	private static final String FILENAME = "filename";
	
	//private final static String equationsDataPath = "";
	private final static String equationsDataPath = Constants.getDataSetLocation();
	private static final String LOCAL_PATH = "./WebContent/data";
	
	
	private final static int RESULTS_NUMBER = 20;
	
	private static SnuggleEngine engine = new SnuggleEngine();
	
	private String working_dir;
	static Version matchVersion = Version.LUCENE_46;
	private Directory indexDirectory;
	private IndexWriterConfig indexConfig;
	
	private Analyzer analyzer = new SolrNotationalAnalyzer(matchVersion);
	private Analyzer analyzer2 = new SolrStructuralAnalyzer(matchVersion);
	private Analyzer analyzer3 = new SolrNormalizerNotationalAnalyzer(matchVersion);
	
	
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private static MathExplorer singleton;
	static java.util.logging.Logger aLogger = java.util.logging.Logger
			.getLogger(MathExplorer.class.getName());

	static Appender myAppender;

	public static final boolean INDEX_WHOLE_ARTICLE = false;
	
	private MathExplorer(ServletContext context, boolean forceRebuild) {
		try {
			working_dir = (context == null) ? LOCAL_PATH : context
					.getRealPath("/data");
			aLogger.info("Working dir: " + working_dir);
			indexDirectory = FSDirectory.open(new File(working_dir + "/index"));
			indexConfig = new IndexWriterConfig(matchVersion, analyzer);
			File index_dir = new File(working_dir + "/index");
			if ((forceRebuild || !index_dir.exists()) && context == null) {
				aLogger.info("Indexing");
				index(INDEX_WHOLE_ARTICLE);
			}
			ireader = DirectoryReader.open(indexDirectory);
			isearcher = new IndexSearcher(ireader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			aLogger.severe(e.getMessage() + ":" + e.getStackTrace());
		}
	}

	public static MathExplorer getInstance(ServletContext context) {
		try {
			if (singleton == null) {
				singleton = new MathExplorer(context, true);
			}
			return singleton;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	void index(boolean wholeArticle) throws Exception {

		IndexWriter iwriter = null;
		try {
			//indexConfig.setRAMBufferSizeMB(11);
			iwriter = new IndexWriter(indexDirectory, indexConfig);
			// Path path = FileSystems.getDefault().getPath(text_data_path);

			aLogger.info("Indexing files in : " + equationsDataPath);
			File dir = new File(equationsDataPath);
			int size = dir.list().length;
			int fileCounter = 0;
			for (File currentFile : dir.listFiles()) {
				if (!currentFile.getName().endsWith(EQUATION_EXTENSION)) {
					continue;
				}
				fileCounter++;
				if (fileCounter % 100 == 0)
					aLogger.info("Processed file: " + fileCounter + "/" + size);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(currentFile), "UTF-8"));
				if (wholeArticle) {
					indexArticle(iwriter, br, currentFile);
				} else {
					indexSingleEquation(iwriter, br, currentFile);
				}
				
			}
			iwriter.close();
			aLogger.info("Finished indexing");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	void indexSingleEquation(IndexWriter iwriter, BufferedReader br, File currentFile) throws IOException {
		String text = new String();
		Set<String> equationsInFile = new HashSet<String>();
		int lineNumber = 0;
		while ((text = br.readLine()) != null) {
			lineNumber++;
			if (equationsInFile.contains(text)) {
				continue;
			}
			equationsInFile.add(text);
			Document doc = new Document();
			Field equationField = new VecTextField(MATH_FIELD.MATH_NOTATIONAL_FIELD.getName(),
					text, Store.YES);
			doc.add(equationField);
			doc.add(new Field(FILENAME, currentFile.getName(),
					TextField.TYPE_STORED));
			doc.add(new Field(LINE, String.valueOf(lineNumber),
					TextField.TYPE_STORED));
			iwriter.addDocument(doc);
		}
		br.close();
	}
	
	void indexArticle(IndexWriter iwriter, BufferedReader br, File currentFile) throws IOException {
		String text = new String();
		StringBuffer textBuffer = new StringBuffer(); 
		while ((text = br.readLine()) != null) {
			textBuffer.append(text);
		}
		Document article = new Document();
		Field equationField = new VecTextField(Constants.EQUATIONS_TEXT,
				textBuffer.toString(), Store.YES);
		article.add(equationField);
		article.add(new Field(FILENAME, currentFile.getName(),
				TextField.TYPE_STORED));
		iwriter.addDocument(article);
		br.close();

	}

	public String texToMathML(String texText) throws IOException {
		
		
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
		aLogger.info(texText +" --> \n" +result);
		return result;
	}

	public List<EquationResult> search(String mathmlQuery) throws Exception {
		return search(mathmlQuery, false, INDEX_WHOLE_ARTICLE);
	} 
	
	public List<EquationResult> search(String mathmlQuery, boolean explainQuery, boolean entireArticles) throws Exception {
		List<EquationResult> result = new ArrayList<EquationResult>();
		aLogger.info("Size of index: " + isearcher.getIndexReader().numDocs());
		try {
			aLogger.info("Searching");
			Query query = new MathQueryParser(mathmlQuery, null, null, null).getQuery();
			// Query query =  createQuery(mathmlQuery);
			aLogger.info("Created query");
			ScoreDoc[] hits = isearcher.search(query, null, RESULTS_NUMBER).scoreDocs;
			aLogger.info("Number of documents: " + hits.length);
			for (ScoreDoc scoreDoc : hits) {
				if (explainQuery) {
					Explanation explanation = isearcher.explain(query, scoreDoc.doc);
					System.out.println(explanation);
				}
				Document hitDoc = isearcher.doc(scoreDoc.doc);
				EquationBuilder eqBuilder = new EquationBuilder();
				if (entireArticles) {// The indexed elements are whole articles{
					eqBuilder
					.setFilename(hitDoc.getValues(FILENAME)[0])
					.setLineNumber(
							Integer.parseInt(hitDoc.getValues(LINE)[0]))
					.setMathmlExpression(
							hitDoc.getValues(MATH_FIELD.MATH_NOTATIONAL_FIELD.getName())[0]);
				}
				else {//The indexed elements are individual equations {
					eqBuilder
					.setFilename(hitDoc.getValues(FILENAME)[0])
					.setLineNumber(0)
					.setMathmlExpression("");
				}
				result.add(eqBuilder.build());
			}
			return result;
		} catch (Exception e) {
			aLogger.severe("Error!!!");
			e.printStackTrace();
			return result;
		}
	}

	public static void main(String[] args) throws Exception {
		
		MathExplorer m = new MathExplorer(null, false);
		m.testAnalyzer("<math><mn>2</mn><mo>+</mo><mn>3</mn></math>");
		System.out.println("Finished");
		//m.search(Constants.SAMPLE_EQUATION_2, true, INDEX_WHOLE_ARTICLE);
		//testUnicodeNormalization();
		
		//List<EquationResult> a = m.search(Constants.SAMPLE_EQUATION_2.replaceAll(
		//		"\n", ""));
		//for (EquationResult b : a) {
		//	System.out.println(b.mathmlExpression);
		//}

		// System.out.println("-------------");
		// m.testRegex(Constants.SAMPLE_EQUATION_3);
		// // System.out.println(search(SAMPLE_EQUATION_3));
		//m.exploreIndex();
	}
	
	static String getTypeName(int type) {
		Character.getType(type);
		return "";
	}
	
	static void testUnicodeNormalization() throws Exception{
		String [] tests ={ "Å", "A", "Ĳ", "ℕ", "Ⅻ", "ℎ", "ℏ", "Ω", "Ω"};
		for (String a: tests) {
			String b = Normalizer.normalize(a, Form.NFKD);
			Console.print("Original: " + a);
			for (char c: a.toCharArray()){
				Console.print("Char: " + c);
				Console.print("\tType:" + Character.getType(c));
				Console.print("\tCode:" + (int)c +"\t\tHex:"+ Integer.toHexString((int)c));
				Console.print("");
			}
			Console.print("Norm"
					+ "alized: " + b);
			for (char c: b.toCharArray()){
				Console.print("Char: " + c);
				Console.print("\tType:" + Character.getType(c));
				Console.print("\tCode:" + (int)c +"\t\tHex:"+ Integer.toHexString((int)c));
				Console.print("");
			}
			Console.print("");
		}
	}
	
	static void testUnicodeEncodings() throws Exception{
		String a = "\u223c";
		Console.print("a:" + a);
		byte[] a_bytes = a.getBytes();
		
		String b = "\\xe2\\x88\\xbc";
		Console.print("b:" + b);
		byte[] b_bytes = b.getBytes("utf-8");

		
		String c = "∼";
		System.out.println("c:" +c);
		byte[] c_bytes = c.getBytes();

		Console.print(a.equals(b));
		Console.print(a.equals(c));
		
		System.out.println("d:" + new String(new byte[] {
			    (byte)0xe2, (byte)0x88, (byte)0xbc }, "UTF-8"));	}

	void testRegex(String equation) {
		Console.print(Regex.extractElements(equation));
	}

	void testAnalyzer(String equation) throws Exception {
		TokenStream ts1 = analyzer.tokenStream(MATH_FIELD.MATH_NOTATIONAL_FIELD.getName(),
				new StringReader(equation));
		TokenStream ts2 = analyzer2.tokenStream(MATH_FIELD.MATH_STRUCTURAL_FIELD.getName(),
				new StringReader(equation));
		TokenStream ts3 = analyzer3.tokenStream(MATH_FIELD.MATH_NORMALIZED_NOTATIONAL_FIELD.getName(),
				new StringReader(equation));
		
		try {
			ts1.reset();
			while (ts1.incrementToken()) {
				Console.print(ts1.getAttribute(CharTermAttribute.class)+":" + 
						ts1.getAttribute(OffsetAttribute.class).startOffset()+"-"+ts1.getAttribute(OffsetAttribute.class).endOffset());
			}
			ts2.reset();
			while (ts2.incrementToken()) {
				Console.print(ts2.getAttribute(CharTermAttribute.class));
			}
			ts3.reset();
			while (ts3.incrementToken()) {
				Console.print(ts3.getAttribute(CharTermAttribute.class)+":" + 
						ts3.getAttribute(OffsetAttribute.class).startOffset()+"-"+ts3.getAttribute(OffsetAttribute.class).endOffset());
			}
			Console.print("***********************");
			ts1.end();
			ts2.end();
			ts3.end();
		} finally {
			// ts.close();
		}
		
	}

	void exploreIndex() throws Exception {
		DirectoryReader ireader = DirectoryReader.open(indexDirectory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		Fields fields = MultiFields.getFields(ireader);
		Terms terms = fields.terms(MATH_FIELD.MATH_NOTATIONAL_FIELD.getName());
		TermsEnum termsEnum = terms.iterator(null);
		BytesRef text;
		while ((text = termsEnum.next()) != null) {
			Term current = new Term(MATH_FIELD.MATH_NOTATIONAL_FIELD.getName(), text);
			System.out.println(text.utf8ToString() + ":"
					+ ireader.docFreq(current));
		}

		for (int i = 1; i <= 1; i++) {
			Document d = ireader.document(i);
			System.out.println(d);
			System.out.println(ireader.getTermVectors(i));
		}

	}
}
