package cern.ch.mathexplorer.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AbstractServlet
 */
public class AbstractServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String FORMAT = "INPUT_FORMAT";
	static String LATEX = "LaTeX";
	static String MATHML = "MathML";
	
	
	public static String getMathjaxScritpHeader() {
		return "<script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>";
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AbstractServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	protected String testUnicodeRendering() {
		return "\n---------------------------------------------------"
				+ "<br>\nTest:(η)(ñ)(μ)(δ)(غ)(କ)(ᇴ)(ᗹ)(𒁦)(𒃅)(🎅)( 	🐙)(😁)(Ѫ)(♘)(⡿)\n";
	}
}
