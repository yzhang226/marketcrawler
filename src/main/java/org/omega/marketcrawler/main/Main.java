package org.omega.marketcrawler.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.tanukisoftware.wrapper.WrapperListener;
//import org.tanukisoftware.wrapper.WrapperManager;

public final class Main 
//implements WrapperListener 
{
	
	private static final Log log = LogFactory.getLog(Main.class);
	
	private Main() { }
	
	/**
     * Called whenever the native Wrapper code traps a system control signal
     *  against the Java process.  <br>
     *  It is up to the callback to take any actions necessary. <br>
     *  Possible values are: <br>
     *    WrapperManager.WRAPPER_CTRL_C_EVENT, <br>
     *    WRAPPER_CTRL_CLOSE_EVENT, <br>
     *    WRAPPER_CTRL_LOGOFF_EVENT, or <br>
     *    WRAPPER_CTRL_SHUTDOWN_EVENT <br>
     *
     * @param event The system control signal.
     */
//	public void controlEvent(int event) {
//		if ( event == WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT 
//				&& WrapperManager.isLaunchedAsService() ) {
//			// Ignore
//        } else {
//        	stop( 0 );
//            // Will not get here.
//        }
//	}

	public Integer start(String[] args) {
		log.info("Start SystemLauncher...");
		System.out.println("Start SystemLauncher...");
		
		int exitCode = SystemLauncher.getLauncher().startup();
		if (exitCode > 0) {
			return null;// indicates a successful startup
		}
		System.out.println( "SystemLauncher startup failed." );
		return 1;
	}

	public int stop(int exitCode) {
		log.info("Stop SystemLauncher...");
		System.out.println("Stop SystemLauncher...");
		
		SystemLauncher.getLauncher().shutdown();
		
		return exitCode;
	}
	

	public static void main(String[] args) {
		System.out.println("Main.args is [" + args + "].");
//		WrapperManager.start(new Main(), args);
	}

	
}
