package cern.ch.mathexplorer.mathematica;

import cern.ch.mathexplorer.core.Constants;
import cern.ch.mathexplorer.utils.OSUtils;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathematicaIntegrator {
	
	private static MathematicaIntegrator instance;
	private KernelLink ml = null;
	
	public static MathematicaIntegrator getInstance() {
		if (instance == null) {
			instance = new MathematicaIntegrator();
		}
		return instance;
	}
	
	private MathematicaIntegrator() {
		String command = "";
		if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
			command = "C:/Program Files/Wolfram Research/Mathematica/9.0/MathKernel.exe";
		}
		if (OSUtils.getOS().equals(OSUtils.OS.LINUX)) {
			command = "math";
		}
		String [] extraArgs = {"-linkmode", "launch", "-linkname", command + " -mathlink"};
		String jLinkDir = Constants.getMathematicaLocation();
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);
		try {
			ml = MathLinkFactory.createKernelLink(extraArgs);
		} catch (MathLinkException e) {
			System.out.println("Fatal error opening link: " + e.getMessage());
			return;
		}
	}
	
	public void test () {
		try {
			// Get rid of the initial InputNamePacket the kernel will send
			// when it is launched.
			ml.discardAnswer();
			ml.evaluate("<<MyPackage.m");
			ml.discardAnswer();
			ml.evaluate("2+2");
			ml.waitForAnswer();
			int result = ml.getInteger();
			System.out.println("2 + 2 = " + result);
			// Here's how to send the same input, but not as a string:
			ml.putFunction("EvaluatePacket", 1);
			ml.putFunction("Plus", 2);
			ml.put(3);
			ml.put(3);
			ml.endPacket();
			ml.waitForAnswer();
			result = ml.getInteger();
			System.out.println("3 + 3 = " + result);
			// If you want the result back as a string, use
			// evaluateToInputForm
			// or evaluateToOutputForm. The second arg for either is the
			// requested page width for formatting the string. Pass 0 for
			// PageWidth->Infinity. These methods get the result in one
			// step--no need to call waitForAnswer.
			String strResult = ml.evaluateToOutputForm("4+4", 0);
			System.out.println("4 + 4 = " + strResult);
			
			String expression1 = "<math><mrow><mrow><mrow><msub><mi>D</mi><mi>i</mi></msub><mo>⁢</mo><msub><mi>D</mi><mi>j</mi></msub><mo>⁢</mo><msub><mi>F</mi><mrow><mi>l</mi><mo>⁢</mo><mi>k</mi></mrow></msub></mrow><mo>=</mo><mrow><mrow><mfrac><mn>1</mn><mn>2</mn></mfrac><mo>⁢</mo><mrow><mo>{</mo><mrow><msub><mi>D</mi><mi>i</mi></msub><mo>,</mo><msub><mi>D</mi><mi>j</mi></msub></mrow><mo>}</mo></mrow><mo>⁢</mo><msub><mi>F</mi><mrow><mi>k</mi><mo>⁢</mo><mi>l</mi></mrow></msub></mrow><mo>-</mo><mrow><mfrac><mi>i</mi><mn>2</mn></mfrac><mo>⁢</mo><mrow><mo>[</mo><mrow><msub><mi>F</mi><mrow><mi>i</mi><mo>⁢</mo><mi>j</mi></mrow></msub><mo>,</mo><msub><mi>F</mi><mrow><mi>k</mi><mo>⁢</mo><mi>l</mi></mrow></msub></mrow><mo>]</mo></mrow></mrow></mrow></mrow><mo>,</mo></mrow></math>";
			String expression2 = "<math><mi>C</mi><mo>+</mo><mi>C</mi><mo>+</mo><mi>B</mi></math>";
			String expression3 = "<math><mrow><msub><mi>ℱ</mi><mn>01</mn></msub><mo>=</mo><mrow><mrow><msup><mi>e</mi><mrow><mo>(</mo><mn>0</mn><mo>)</mo></mrow></msup><mo>⁢</mo><msub><mi>σ</mi><mn>0</mn></msub></mrow><mo>+</mo><mrow><msup><mi>e</mi><mrow><mo>(</mo><mn>3</mn><mo>)</mo></mrow></msup><mo>⁢</mo><msub><mi>σ</mi><mn>3</mn></msub></mrow></mrow></mrow></math>";
			
			System.out.println("---");
			String myExpression = "DisplayForm[ImportString[\""+ expression2
					+ "\", \"MathML\"]]";	
			strResult = ml.evaluateToOutputForm(myExpression, 0);
			System.out.println("Imported expression: " + strResult);
			
			strResult = ml.evaluateToOutputForm("Simplify["+strResult+"]", 0);
			System.out.println("Simplified result: " + strResult);
			
			strResult = ml.evaluateToOutputForm(strResult, 0);
			System.out.println("Result: " + strResult);
			
			
			
		} catch (MathLinkException e) {
			System.out.println("MathLinkException occurred: " + e.getMessage());
		} finally {
			ml.close();
		}
	}
	
	public static void main(String[] args) {
		MathematicaIntegrator mi = getInstance();
		mi.test();
	}
}

