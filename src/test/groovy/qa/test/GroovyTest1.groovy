package qa.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import au.com.bytecode.opencsv.*
import java.util.List
import java.lang.Iterable
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.RemoteWebDriver
import qa.test.Utils
import static org.junit.Assert.*

@RunWith( Parameterized.class )
class GroovyTest1 extends WebDriverUtils {

	private String testNum
	private String offSet
	private String expectedRes
	private String addrA
	private String cityA
	private String stateA
	private String addrB
	private String cityB
	private String stateB
	private RemoteWebDriver driver

	@Before
	public void startUpSelenium() {
		initializeRemoteBrowser( System.getProperty("browser.type"), System.getProperty("hub.ip"), 
			   Integer.parseInt(System.getProperty("hub.port")) )
		this.driver = getDriver()
	}

	/** 
	 * Because test class is parameterized it needs to have args equal
	 * to the column size of the input .csv file.
	 * @param fileName
	 * @param testNum
	 * @param messAge
	 */
	GroovyTest1( String testNum, String offSet, String expectedRes, String addrA, String cityA, String stateA, 
		                String addrB, String cityB, String stateB ) {
		this.testNum = Integer.parseInt( testNum )
		this.testXOffset = Integer.parseInt( offSet )	
		this.expectedRes = expectedRes
		this.addrA = addrA
		this.cityA = cityA
		this.stateA = stateA
		this.addrB = addrB
		this.cityB = cityB
		this.stateB = stateB
	}

	@Parameters(name = "testNum: {0}: expectedRes:{2}")
	public static Iterable<String[]> loadParams() {
		sLogger.info( "Loading parameters..." );
		File tFile = new File( "build/resources/test/params.csv" )
		List<String[]> rows = null
		if ( tFile.exists() ) {
			CSVReader reader = null
			try {
				reader = new CSVReader( new FileReader( tFile ) )
				rows = reader.readAll()
			} catch ( FileNotFoundException e ) {
				e.printStackTrace()
			} catch ( IOException e ) {
				e.printStackTrace()
			}
		}
		sLogger.info("Finished loadParams()")
		return rows
	}

	@Test
	public void runTest1() {
		sLogger.info("Testing script " + testNum)

		driver.get( System.getProperty("start.url") )
        Actions actions = new Actions( driver )
		
		//make more space on map
		WebElement elem = driver.findElement(By.id("slider"))
		actions.moveToElement(elem).click().perform()

		// search for a B city
		elem = driver.findElement(By.id('sb_form_q'))
		elem.sendKeys( addrA + ", " + cityA + ", " + stateA )
		
		// click the search button
		elem = driver.findElement(By.id('sb_form_go'))
		actions.moveToElement(elem).click().perform()

		waitTimer(2, 1000)
		
		// click Directions link
		elem = driver.findElement( By.cssSelector("div#msve_taskArea_actionBar span.left a#taskBar_directionsBtn") )
		actions.moveToElement(elem).click().perform()

		//setup A address
		elem = getElementByLocator( By.cssSelector( "div.waypointContainer input.sw_qbox" ) )
		elem.click()
		elem.sendKeys( addrB + ", " + cityB + ", " + stateB )

		//click go
		elem = driver.findElement(By.id('TaskHost_DrivingDirectionsShowDirections'))
		actions.moveToElement(elem).click().perform()
		
		//assert time
		elem = getElementByLocator(By.id('dd_tripSummary'))
		assertTrue( "We expected a routing result of " + expectedRes + " miles but we got " 
			    + elem.getText().substring(0,4), elem.getText().contains( expectedRes ) )

	}

	@After
	public void closeDriver() {
		closeAllBrowserWindows()
	}

}
