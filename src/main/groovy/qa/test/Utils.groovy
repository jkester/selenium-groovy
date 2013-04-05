package qa.test;

import java.io.File;
import java.text.DecimalFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Utils {
	
	public Utils() {
		sLogger.info("Called Utils constructor...")
	}

	public static Logger sLogger = LoggerFactory.getLogger( "sLogger" )
	public static final String BUTTON_ID="click-id"
	public static final int COMMON_IMPLICIT_WAIT = 10
	public static final int DEFAULT_IMPLICIT_WAIT = 30
	
	public static void waitTimer( int units, int mills ) {
		DecimalFormat df = new DecimalFormat("###.##")
		double totalSeconds = ((double)units*mills)/1000
		sLogger.info("Explicit pause for " + df.format(totalSeconds) + " seconds divided by " + units + " units of time: ")
		try {
			Thread.currentThread()
			int x = 0
			while( x < units ) {
				Thread.sleep( mills )
				sLogger.info(".")
				x = x + 1
			}
		} catch ( InterruptedException ex ) {
			ex.printStackTrace()
		}
	}
	
}
