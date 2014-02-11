package cern.ch.mathexplorer.mathematica;

import cern.ch.mathexplorer.utils.Console;
import cern.ch.mathexplorer.utils.Constants;
import cern.ch.mathexplorer.utils.OSUtils;

import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MathKeyListener;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

public class MathematicaUtils {
	public static KernelLink initLink() throws MathLinkException {
		String command = "";
		command = Constants.getMathematicaCommand();
		String[] extraArgs = { "-linkmode", "launch", "-linkname",
				command + " -mathlink" };

		String jLinkDir = Constants.getMathematicaLinkLocation();
		System.setProperty("com.wolfram.jlink.libdir", jLinkDir);

		try {
			KernelLink newLink = MathLinkFactory.createKernelLink(extraArgs);
			// ml.clearError();

			newLink.discardAnswer();
			//ml = newLink;
			return newLink;
		} catch (MathLinkException e) {
			e.printStackTrace();
			Console.print("Fatal error opening link: " + e.getMessage());
			throw e;
		}
	}
	
	static void killMathKernel(KernelLink link) {
		if (link !=null) {
			link.close();
		}
		try {
			if (OSUtils.getOS().equals(OSUtils.OS.LINUX) ||
					OSUtils.getOS().equals(OSUtils.OS.MAC)) {
				Console.print("Killing processes in linux!!!!!!!");
				String command1 = "killall -s 9 Mathematica";
				String command2 = "killall -s 9 MathKernel";
				Runtime.getRuntime().exec(command1);
				Runtime.getRuntime().exec(command2);
			}
			
			if (OSUtils.getOS().equals(OSUtils.OS.WINDOWS)) {
				String command1 = "taskkill /im MathKernel.exe";
				String command2 = "taskkill /im Mathematica.exe";
				
				Runtime.getRuntime().exec(command1);
				Runtime.getRuntime().exec(command2);
			}

		} catch (Exception e) {

		}
	}
	public static void main(String[] args) {
		killMathKernel(null);
	}
}


