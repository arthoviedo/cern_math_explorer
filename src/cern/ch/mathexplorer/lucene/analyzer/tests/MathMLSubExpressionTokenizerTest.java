package cern.ch.mathexplorer.lucene.analyzer.tests;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import cern.ch.mathexplorer.lucene.analyzer.MathMLSubExpressionTokenizer;

@RunWith(JUnit4.class)
	public class MathMLSubExpressionTokenizerTest {
		String testString1 = "<math><mrow><mi>S</mi><mo>∼</mo><mo>∫</mo><mi>d</mi><mi>y</mi><msup><mi>e</mi><mrow><mn>4</mn><mo>⁢</mo><mi>U</mi></mrow></msup><mrow><mo>[</mo><mo>-</mo><mn>6</mn><msup><mover accent=\"true\"><mi>U</mi><mo>˙</mo></mover><mn>2</mn></msup><mo>+</mo><mfrac><mn>1</mn><mn>2</mn></mfrac><msub><mi>g</mi><mrow><mi>A</mi><mo>⁢</mo><mi>B</mi></mrow></msub><msup><mover accent=\"true\"><mi>ϕ</mi><mo>˙</mo></mover><mi>A</mi></msup><msup><mover accent=\"true\"><mi>ϕ</mi><mo>˙</mo></mover><mi>B</mi></msup><mo>+</mo><mi>V</mi><mo>]</mo></mrow><mo></mo></mrow></math>";
		String expectedResultsTest1 []= {};
		
		String testString2 = "<math><mrow><mi>A</mi></mrow></math>";
		String expectedResultsTest2 []= {"<mrow><mi>A</mi></mrow>"};
		
		String testString3 = "<math><mrow><mi>A</mi></mrow><mrow><mi>B</mi></mrow></math>";
		String expectedResultsTest3 []= {"<mrow><mi>A</mi></mrow>", "<mrow><mi>B</mi></mrow>"};
		
		String testString4 = "<math><mrow><mrow><mi>A</mi></mrow></mrow></math>";
		String expectedResultsTest4 []= {"<mrow><<mi>A</mi></mrow>", "<mrow><mrow><mi>A</mi></mrow></mrow>"};
		
		@Test
		public void testMRowEXtractor () {
			ArrayList<String> a = new MathMLSubExpressionTokenizer().findMRowSubExpressions(testString1);
			for (String s: a){
				System.out.println("<math>"+s+"</math>");
			}
		}
	}