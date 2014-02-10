package cern.ch.mathexplorer.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cern.ch.mathexplorer.core.MathExplorer;
import cern.ch.mathexplorer.utils.Constants;

/**
 * Servlet implementation class InputServlet
 */
public class InputServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InputServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
	    request.setCharacterEncoding("UTF-8");
	    // MathExplorer.getInstance(getServletContext());
	    
		try {
			MathExplorer.getInstance(getServletContext()).search(Constants.SAMPLE_EQ_1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
		PrintWriter out = response.getWriter();
		String text = "<!DOCTYPE html>"
				+ "<html><head>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
				+ getMathjaxScritpHeader()
				+ "<title>Math Explorer</title>\n"
				+ "</head>\n"
				+ "<body>"
				+ "Enter your query: \n"
				+ "<form action=\"query\">\n"
				+ "<textarea cols=150 rows=12 name=\"inputQuery\">\n"
				+ "</textarea>\n"
				+ getInputFormatOptions()
				+ "<br/>\n"
				+ "<button>Go go go!!!</button>\n<br/>"
				+ "<a href=\"resources/matheditorpage.html\"target=\"_blank\">Open MathML editor</a> (Requires Adobe Flash Player)"
				+ "<br/>"
				+ "</form>"
				+ getSampleEquations()
				+ testUnicodeRendering()
				+ "<br/>"
				+ "</body>"
				+ "</html>";
		out.println(text);
	}
	
	private static String getInputFormatOptions() {
		String result = "<br/> Select desired input format<br/>"
			+ "<input type=\"radio\" name=\""+FORMAT+"\" value=\""+MATHML+"\" checked>"+MATHML+"<br>"
			+ "<input type=\"radio\" name=\""+FORMAT+"\" value=\""+LATEX+"\">"+LATEX+"<br>";
		return result;
	}
	
	private static String getSampleEquations() {
		
		String result = "<br/>Some MathML examples you can use: <table border=1><br/>\n";
		
		for (String s : Constants.SAMPLE_MATHML_EQUATIONS) {
			result += "<tr><td>";
			result += s+"\n";
			result += "<xmp>"+s+"</xmp><br/>\n";
			result += "</td></tr>";
		}
		result += "</table>\n";
		return result;
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

}
