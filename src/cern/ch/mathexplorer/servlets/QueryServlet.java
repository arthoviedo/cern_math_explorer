package cern.ch.mathexplorer.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cern.ch.mathexplorer.core.EquationResult;
import cern.ch.mathexplorer.core.MathExplorer;

/**
 * Servlet implementation class QueryServlet
 */
public class QueryServlet extends AbstractServlet {
	private static final String XML_EXTENSION = ".xml";
	private static final String INDEX_FILE_EXTENSION = ".eq";
	private static final long serialVersionUID = 1L;
	private static final String URL_PUBLIC_FOLDER = "https://googledrive.com/host/0B3PGNL_gfdnqaEJ0bk03aHBvME0/";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
	    request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String queryString = request.getQueryString();
		String query = URLDecoder.decode(queryString, "UTF-8").replace("inputQuery=", "")
				.replace("&"+FORMAT+"=" + LATEX, "")
				.replace("&"+FORMAT+"=" + MATHML, "");
		
		String format = request.getParameter(FORMAT);
		if (format.equals(LATEX)) {
			query = MathExplorer.getInstance(getServletContext()).texToMathML(query);
		}
		
		String resultText = "";
		try {
			
			List<EquationResult> result = MathExplorer.getInstance(getServletContext()).search(query);
			for (EquationResult eq : result) {
				String fileURL = URL_PUBLIC_FOLDER+eq.getFilename().replace(INDEX_FILE_EXTENSION, XML_EXTENSION); 
				resultText += "<tr><td><br/> Link to file: <a href=\""+ fileURL+ "\" target=\"_blank\">" 
						+ eq.getFilename().replace(INDEX_FILE_EXTENSION, XML_EXTENSION)+"</a> "; 
				resultText += "<br/>"+eq.getMathmlExpression();
				resultText += "</td></tr>";
			}
		}
		catch (Exception e) {
			resultText = "There was a problem retrieving your results, "
					+ "please try again later (Error:  " + e.getMessage()+")";
			e.printStackTrace();
		}
		
		String text = "<!DOCTYPE html>"
				+ "<html><head>"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
				+ getMathjaxScritpHeader()
				+ "<title>Query result</title></head>"
				+ "<body>Your query was:"
				+ "<br/>"
				+ "<table border=1>"
				+ query
				+ "<br>Top results:" + resultText
				+ "</table>"
				+ "<br>"
				+ testUnicodeRendering()
				+"</body>"
				+ "</html>";
		out.println(text);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
