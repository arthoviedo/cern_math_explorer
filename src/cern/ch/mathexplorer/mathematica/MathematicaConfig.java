package cern.ch.mathexplorer.mathematica;

import java.util.concurrent.TimeUnit;

public class MathematicaConfig {
	public final static boolean USE_MATHEMATICA = true;
	
		
	public final static int TIMEOUT_TIME = 10;
	public final static TimeUnit TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;
	
	public final static NORMALIZATION_MODES NROMALIZATION_MODE = NORMALIZATION_MODES.FULL_SIMPLIFICATION;
	
	public enum NORMALIZATION_MODES{
		NO_SIMPLIFICATION,
		STANDARD_SIMPLIFICATION,
		FULL_SIMPLIFICATION;
	}

}
