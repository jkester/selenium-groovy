package qa.test

import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.TimeoutException

import static qa.test.Utils.*

/**
 * Class to get an instance of RemoteWebDriver
 */

class WebDriverSetup {

	public static WebDriverSetup getInstance() {
		if (setup == null) {
			setup = new WebDriverSetup()
		}
	}
	private String mainHandle
	private Set<String> handleCache
	private static WebDriverSetup setup
	protected RemoteWebDriver driver
	public String hubIP

	private WebDriverSetup() {
		String browser = System.getProperty("browser.type")
		hubIP = System.getProperty("hub.ip")
		hubPort = System.getProperty("hub.port")
		initializeRemoteBrowser( browser, hubIP, hubPort )
		sLogger.info("Finished WebDriverSetup...")
	}
	
	protected void closeAllBrowserWindows() {
        getDriver().switchTo().defaultContent()
		Set<String> handles = getDriver().getWindowHandles()
		if ( !handles.isEmpty() ) {
			LOGGER.info("Closing " + handles.size() + " window(s).")
			for ( String windowId : handles ) {
				closeWindowByHandle( windowId )
			}
		} else {
			LOGGER.info("There were no window handles to close.")
		}
		getDriver().quit() // this quit is critical, otherwise window will hang open
	}
	
	public String hubPort;
	
	protected void closeWindowByHandle( String windowHandle ) {
		getDriver().switchTo().window( windowHandle )
		LOGGER.info("Closing window with handle \"" + getDriver().getWindowHandle() + "\"." )
		getDriver().close()
	}
	
	protected RemoteWebDriver getDriver() {
		return driver
	}
	
	public WebElement getElementByLocator( By locator ) {
		LOGGER.info( "Get element by locator: " + locator.toString() )
		long startTime = System.currentTimeMillis()
		getDriver().manage().timeouts().implicitlyWait( COMMON_IMPLICIT_WAIT, TimeUnit.SECONDS )
		WebElement we = null
		boolean unfound = true
		int numTries = 0
		int maxTries = 5
		while ( unfound && numTries < maxTries ) {
			numTries += 1
			if ( numTries > 1 ) LOGGER.info("Locating remaining time: "
				 + ( ( maxTries*COMMON_IMPLICIT_WAIT)-(COMMON_IMPLICIT_WAIT*(numTries-1) ) ) + " seconds." )
			try {
				we = getDriver().findElement( locator )
				unfound = false // FOUND IT
		        break;
			} catch ( StaleElementReferenceException ser ) {
				LOGGER.info( "ERROR: Stale element. " + locator.toString() )
				unfound = true
			} catch ( NoSuchElementException nse ) {
				LOGGER.info( "ERROR: No such element. " + locator.toString() )
				unfound = true
			}
		}
		long endTime = System.currentTimeMillis()
		long totalTime = endTime - startTime
		LOGGER.info("Finished click after waiting for " + totalTime + " milliseconds.")
		getDriver().manage().timeouts().implicitlyWait( DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS )
		return we
	}
	
	public void initializeRemoteBrowser( String type, String host, int port ) {
		DesiredCapabilities dc = new DesiredCapabilities()
		dc.setCapability( "takesScreenshot", false )
		dc.setCapability( "webdriver.remote.quietExceptions", true )
		try {
			if ( type.equalsIgnoreCase( "firefox" ) ) {
				dc.setBrowserName( "firefox" )
				setDriver( new RemoteWebDriver( new URL("http://" + host + ":" + port + "/wd/hub"), dc ) )
			} else if ( type.equalsIgnoreCase( "internetExplorer" ) ) {
				dc.setBrowserName( "internet explorer" )
				setDriver( new RemoteWebDriver( new URL("http://" + host + ":" + port + "/wd/hub"), dc ) )
			} else if ( type.equalsIgnoreCase( "chrome" ) ) {
				dc.setBrowserName( "chrome" )
				setDriver( new RemoteWebDriver( new URL("http://" + host + ":" + port + "/wd/hub"), dc ) )
			} else {
				LOGGER.info( "Invalid browser type. Cannot initialize." )
			}
		} catch ( MalformedURLException e ) {
			e.printStackTrace()
		}
		getDriver().manage().timeouts().implicitlyWait( DEFAULT_IMPLICIT_WAIT, TimeUnit.MILLISECONDS )
		positionMainHandle()
	}

	public void positionMainHandle() {
		setHandleCache( getDriver().getWindowHandles() )
		if ( handleCache.size() == 0 ) {
			mainHandle = ""
			throw new IllegalStateException("No browser window handles are open.\n" +
					"Browser is uninitialized.")
		} else if ( handleCache.size() > 1 ) {
			mainHandle = ""
			throw new IllegalStateException("More than one browser window handle is open.\n" +
					"Please close all browsers and restart test.")
		} else {
			mainHandle = getDriver().switchTo().defaultContent().getWindowHandle()
			setInitialWindowPosition( mainHandle, getTestXOffset() )
		}
	}
	
	public void setHandleCache(Set<String> handleCache) {
		this.handleCache = handleCache
	}
	
	public void setInitialWindowPosition( String handle, int xOffset ) {
		LOGGER.info("Setting initial window position with offset of " + xOffset )
		int xPos = Integer.parseInt( System.getProperty("windowXPosition") )
		if ( xOffset > 0 ) xPos = xPos + xOffset
		setWindowPosition( handle, Integer.parseInt( System.getProperty("windowWidth") ),
				Integer.parseInt( System.getProperty("windowHeight") ), xPos,
				Integer.parseInt( System.getProperty("windowYPosition") )
			);
	}

	public void setWindowPosition(String handle, int width, int height, int fleft, int ftop) {
		getDriver().switchTo().window( handle ).manage().window().setPosition( new Point(fleft, ftop) )
		getDriver().switchTo().window( handle ).manage().window().setSize( new Dimension( width, height) )
	}
	
	public WebElement waitForElementPresent(RemoteWebDriver driver, By by, long timeoutInSeconds) throws Exception {
		boolean wasFailure = false;
		WebElement el = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
			el = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			return el;
		} catch( TimeoutException e ) {
			wasFailure = true;
			throw new TimeoutException("!Element  \"" + by.toString() + "\" could not be found within " + timeoutInSeconds + " seconds.");
		}
		catch(Exception e) {
			wasFailure = true;
			throw e;
		}
	}

	public WebElement waitForElementVisible(RemoteWebDriver driver, By by, long timeoutInSeconds) throws Exception {
		boolean wasFailure = false;
		WebElement el = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
			el = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			return el;
		} catch( TimeoutException e ) {
			wasFailure = true;
			throw new TimeoutException("!Element  \"" + by.toString() + "\" could not be found within " + timeoutInSeconds + " seconds.");
		}
		catch(Exception e) {
			wasFailure = true;
			throw e;
		}
	}

	//will check every second and return true when it can locate the requested element
	public boolean isElementPresent(RemoteWebDriver driver, By by, long timeoutInSeconds) throws Exception {
		boolean returnValue = false;
		int size = 0;
		int ticker = 0;
		while (size == 0 && ticker < timeoutInSeconds) {
			size = driver.findElements(by).size();
			sleep(1000);
			ticker++;
		}

		if (size > 0) {
			returnValue = true;
		}
		return returnValue;
	}

	public void executeJS(RemoteWebDriver driver, String statement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(statement);
	}

	public Object evalJS(RemoteWebDriver driver, String statement) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return js.executeScript("return " + statement);
	}

	private ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
		def c = {driver ->
			WebElement toReturn = driver.findElement(locator);
			if (toReturn.isDisplayed()) {
				return toReturn;
			}
			return null;
		} as ExpectedCondition<WebElement>;
		return c;
	}
	
}
