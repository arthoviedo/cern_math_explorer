package cern.ch.mathexplorer.mathematica;

import java.util.concurrent.TimeUnit;

public class MathematicaConfig {
	public final static boolean USE_MATHEMATICA = true;
	
	public final static int NO_SIMPLIFICATION = 0;
	public final static int SINGLE_SIMPLIFICATION = 1;
	public final static int FULL_SIMPLIFICATION = 2;
	
	public final static int SIMPLIFY = NO_SIMPLIFICATION;
	
	public final static int TIMEOUT_TIME = 10;
	public final static TimeUnit TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;
	
	public enum NORMALIZATION_MODE{
		NO_SIMPLIFICATION,
		SIMPLE_SIMPLIFICATION,
		FULL_SIMPLIFICATION;
	}

}
