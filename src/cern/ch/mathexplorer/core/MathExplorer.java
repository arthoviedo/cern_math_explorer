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
import java.util.Collection;
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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import cern.ch.mathexplorer.core.EquationResult.EquationBuilder;
import cern.ch.mathexplorer.lucene.MathMLAnalyzer;
import cern.ch.mathexplorer.lucene.Regex;
import cern.ch.mathexplorer.lucene.VecTextField;

public class MathExplorer {
	private static final String EQUATION_EXTENSION = ".eq";
	private static final String LINE = "line";
	private static final String FILENAME = "filename";
	private static final String EQUATION_ELEMENT = "EQUATION";
	private static final String EQUATIONS_TEXT = "EQUATIONS_TEXT";

	//private final static String equationsDataPath = "";
	private final static String equationsDataPath = Constants.getDataSetLocation();
	private static final String LOCAL_PATH = "./WebContent/data";
	
	
	private final static int RESULTS_NUMBER = 20;
	
	private static SnuggleEngine engine = new SnuggleEngine();
	
	private String working_dir;
	static Version matchVersion = Version.LUCENE_46;
	private Directory indexDirectory;
	private IndexWriterConfig indexConfig;
	private Analyzer analyzer = new MathMLAnalyzer(matchVersion);
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private static MathExplorer singleton;
	static java.util.logging.Logger aLogger = java.util.logging.Logger
			.getLogger(MathExplorer.class.getName());

	static Appender myAppender;

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
				index();
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

	void indexArticles() throws Exception {

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
				String text = new String();
				int lineNumber = 0;
				StringBuffer textBuffer = new StringBuffer(); 
				while ((text = br.readLine()) != null) {
					textBuffer.append(text);
				}
				Document article = new Document();
				Field equationField = new VecTextField(EQUATIONS_TEXT,
						textBuffer.toString(), Store.YES);
				article.add(equationField);
				article.add(new Field(FILENAME, currentFile.getName(),
						TextField.TYPE_STORED));
				iwriter.addDocument(article);
				br.close();
			}
			iwriter.close();
			aLogger.info("Finished indexing");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		/**
		 * finally { iwriter.close(); }
		 */
	}
	
	void index() throws Exception {

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
				String text = new String();
				int lineNumber = 0;
				Set<String> equationsInFile = new HashSet<String>();
				while ((text = br.readLine()) != null) {
					lineNumber++;
					if (equationsInFile.contains(text)) {
						continue;
					}
					equationsInFile.add(text);
					Document doc = new Document();
					Field equationField = new VecTextField(EQUATION_ELEMENT,
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
			iwriter.close();
			aLogger.info("Finished indexing");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		/**
		 * finally { iwriter.close(); }
		 */
	}

	Query createQuery(String queryString) {
		aLogger.info("Query before: " + queryString);
		queryString = Regex.cleanQuery(queryString);
		aLogger.info("Query after: " + queryString);

		// QueryParser parser = new QueryParser(matchVersion, DUMMY_FIELDNAME,
		// analyzer);
		// Query query = parser.parse(eq2);
		// SpanTermQuery query = new SpanTermQuery(new Term(DUMMY_FIELDNAME,
		// "39"));

		/**
		 * PhraseQuery query = new PhraseQuery(); query.setSlop(100);
		 * Collection<String> termsInQuery = Utils.extractElements(queryString);
		 * System.out.println("New query:"); for (String s: termsInQuery) {
		 * System.out.println("Query term: " + s); query.add(new
		 * Term(EQUATION_ELEMENT, s)); }
		 */

		BooleanQuery query = new BooleanQuery();
		Collection<String> termsInQuery = Regex.extractElements(queryString);

		for (String s : termsInQuery) {
			query.add(new BooleanClause(new TermQuery(new Term(
					EQUATION_ELEMENT, s)), Occur.SHOULD));
		}

		/**
		 * Collection<String> termsInQuery = Utils.extractElements(eq2);
		 * ArrayList<SpanQuery> spanQueries = Lists.newArrayList();
		 * 
		 * for (String s: termsInQuery) { spanQueries.add(new SpanTermQuery(new
		 * Term(DUMMY_FIELDNAME, s))); } SpanQuery [] a =
		 * spanQueries.toArray(new SpanQuery [1]); SpanNearQuery query = new
		 * SpanNearQuery(a, 5, false); System.out.println(query.toString());
		 */
		/**
		 * MoreLikeThis mlt = new MoreLikeThis(ireader);
		 * mlt.setAnalyzer(analyzer); Query query = mlt.like(new
		 * StringReader(eq2), EQUATION_ELEMENT);
		 */

		return query;
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

	public List<EquationResult> searchArticles(String mathmlQuery) throws Exception {
		List<EquationResult> result = new ArrayList<EquationResult>();
		aLogger.info("Size of index: " + isearcher.getIndexReader().numDocs());
		try {
			aLogger.info("Searching");

			Query query = createQuery(mathmlQuery);
			aLogger.info("Created query");
			ScoreDoc[] hits = isearcher.search(query, null, RESULTS_NUMBER).scoreDocs;
			aLogger.info("Number of documents: " + hits.length);
			for (ScoreDoc scoreDoc : hits) {
				Document hitDoc = isearcher.doc(scoreDoc.doc);
				EquationBuilder eqBuilder = new EquationBuilder();
				eqBuilder
						.setFilename(hitDoc.getValues(FILENAME)[0])
						.setLineNumber(0)
						.setMathmlExpression("");
				result.add(eqBuilder.build());
			}
			return result;
		} catch (Exception e) {
			aLogger.severe("Error!!!");
			e.printStackTrace();
			return result;
		}
	}
	
	public List<EquationResult> search(String mathmlQuery) throws Exception {
		List<EquationResult> result = new ArrayList<EquationResult>();
		aLogger.info("Size of index: " + isearcher.getIndexReader().numDocs());
		try {
			aLogger.info("Searching");

			Query query = createQuery(mathmlQuery);
			aLogger.info("Created query");
			ScoreDoc[] hits = isearcher.search(query, null, RESULTS_NUMBER).scoreDocs;
			aLogger.info("Number of documents: " + hits.length);
			for (ScoreDoc scoreDoc : hits) {
				Document hitDoc = isearcher.doc(scoreDoc.doc);
				EquationBuilder eqBuilder = new EquationBuilder();
				eqBuilder
						.setFilename(hitDoc.getValues(FILENAME)[0])
						.setLineNumber(
								Integer.parseInt(hitDoc.getValues(LINE)[0]))
						.setMathmlExpression(
								hitDoc.getValues(EQUATION_ELEMENT)[0]);
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
		
		MathExplorer m = new MathExplorer(null, true);
		//testUnicodeNormalization();
		 //m.testAnalyzer(Constants.SAMPLE_EQUATION_7);
		
		//List<EquationResult> a = m.search(Constants.SAMPLE_EQUATION_2.replaceAll(
		//		"\n", ""));
		//for (EquationResult b : a) {
		//	System.out.println(b.mathmlExpression);
		//}

		// System.out.println("-------------");
		// m.testRegex(Constants.SAMPLE_EQUATION_3);
		// // System.out.println(search(SAMPLE_EQUATION_3));
		m.exploreIndex();
	}
	
	static String getTypeName(int type) {
		Character.getType(type);
		return "";
	}
	
	static void testUnicodeNormalization() throws Exception{
		String [] tests ={ "Å", "A", "Ĳ", "ℕ", "Ⅻ", "ℎ", "ℏ", "Ω", "Ω"};
		for (String a: tests) {
			String b = Normalizer.normalize(a, Form.NFKD);
			System.out.println("Original: " + a);
			for (char c: a.toCharArray()){
				System.out.print("Char: " + c);
				System.out.print("\tType:" + Character.getType(c));
				System.out.print("\tCode:" + (int)c +"\t\tHex:"+ Integer.toHexString((int)c));
				System.out.println("");
			}
			System.out.println("Normalized: " + b);
			for (char c: b.toCharArray()){
				System.out.print("Char: " + c);
				System.out.print("\tType:" + Character.getType(c));
				System.out.print("\tCode:" + (int)c +"\t\tHex:"+ Integer.toHexString((int)c));
				System.out.println("");
			}
			System.out.println("");
		}
	}
	
	static void testUnicodeEncodings() throws Exception{
		String a = "\u223c";
		System.out.println("a:" + a);
		byte[] a_bytes = a.getBytes();
		
		String b = "\\xe2\\x88\\xbc";
		System.out.println("b:" + b);
		byte[] b_bytes = b.getBytes("utf-8");

		
		String c = "∼";
		System.out.println("c:" +c);
		byte[] c_bytes = c.getBytes();

		System.out.println(a.equals(b));
		System.out.println(a.equals(c));
		
		System.out.println("d:" + new String(new byte[] {
			    (byte)0xe2, (byte)0x88, (byte)0xbc }, "UTF-8"));	}

	void testRegex(String equation) {
		System.out.println(Regex.extractElements(equation));
	}

	void testAnalyzer(String equation) throws Exception {
		TokenStream ts = analyzer.tokenStream(EQUATION_ELEMENT,
				new StringReader(equation));
		OffsetAttribute offsetAttribute = ts
				.addAttribute(OffsetAttribute.class);

		try {
			ts.reset();
			while (ts.incrementToken()) {
				// System.out.println("Token: " + ts.reflectAsString(true));
				
				System.out.println(ts.getAttribute(CharTermAttribute.class)+":" + 
						ts.getAttribute(OffsetAttribute.class).startOffset()+"-"+ts.getAttribute(OffsetAttribute.class).endOffset());
			}
			System.out.println("***********************");
			ts.end();
		} finally {
			// ts.close();
		}
	}

	void exploreIndex() throws Exception {
		DirectoryReader ireader = DirectoryReader.open(indexDirectory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		Fields fields = MultiFields.getFields(ireader);
		Terms terms = fields.terms(EQUATION_ELEMENT);
		TermsEnum termsEnum = terms.iterator(null);
		BytesRef text;
		while ((text = termsEnum.next()) != null) {
			Term current = new Term(EQUATION_ELEMENT, text);
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
