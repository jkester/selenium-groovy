package qa.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import au.com.bytecode.opencsv.CSVReader
import java.util.List
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import qa.test.Utils

@RunWith(Parameterized.class)
class GroovyTest1 extends WebDriverUtils {

	protected String fileName;
	protected String testNum;
	protected String messAge;
	
	@Before
	public void startUpSelenium() {
		initializeRemoteBrowser( System.getProperty("browser.type"), System.getProperty("hubIP"), System.getProperty("hubPort") )
	}
	
	/** 
	 * Because test class is parameterized it needs to have args equal
	 * to the column size of the input .csv file.
	 * @param fileName
	 * @param testNum
	 * @param messAge
	 */
	GroovyTest1( String fileName, String testNum, String messAge ) {
		super();
		this.fileName = fileName;
		this.testNum = testNum;
		this.messAge = messAge;	
    }
		
	@Parameters(name = "file: {0}: testnum:{1}: message:{2}")
	public static List<String[]> loadParams() {
		sLogger.info("Loading file from : " + System.getProperty("user.dir") );
		File tFile = new File( "build/resources/params.csv" );
		List<String[]> rows = null;
		if ( tFile.exists() ) {
			CSVReader reader = null;
			try {
				reader = new CSVReader( new FileReader( tFile ), ',' );
				rows = reader.readAll();
			} catch ( FileNotFoundException e ) {
				e.printStackTrace();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		sLogger.info("Finished loadParams()");
		return rows;
	}
	
	@Test
	public void runTest1() {
		sLogger.info "Testing script " + testNum;
		try {
			//start engine with bindings
			File scriptFile = new File( fileName )
			GroovyScriptEngine gse = new GroovyScriptEngine( scriptFile );
			Binding binding = new Binding();
			
			Actions actions = new Actions( getDriver() );
	
			sLogger.info("Setting bindings for driver,actions,utils,logger");
			binding.setVariable("number", testNum);
			binding.setVariable("message", messAge);
			gse.run( scriptFile , binding);
		} catch ( AssertionError assertionError ) {
			sLogger.info( assertionError.getMessage() );
			throw assertionError;
		}
	}
	
	@After
	public void closeDriver() {
		closeAllBrowserWindows(); //TODO might move to After instead of AfterClass
	}
	
}
