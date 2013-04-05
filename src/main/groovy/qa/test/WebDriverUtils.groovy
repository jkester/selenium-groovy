package qa.test

import java.io.File
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import java.util.concurrent.TimeUnit
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

/**
 * Class to get an instance of RemoteWebDriver
 */

abstract class WebDriverUtils extends Utils {
	
	public String mainHandle
	public Set<String> handleCache
	private RemoteWebDriver driver
	public int testXOffset = 0; //default value

	public WebDriverUtils() {
		sLogger.info("Called WebDriverUtils constructor...")
	}
	
	public void closeAllBrowserWindows() {
        driver.switchTo().defaultContent()
		Set<String> handles = driver.getWindowHandles()
		if ( !handles.isEmpty() ) {
			sLogger.info("Closing " + handles.size() + " window(s).")
			for ( String windowId : handles ) {
				closeWindowByHandle( windowId )
			}
		} else {
			sLogger.info("There were no window handles to close.")
		}
		driver.quit() // this quit is critical, otherwise window will hang open
	}
	
	public void closeWindowByHandle( String windowHandle ) {
		driver.switchTo().window( windowHandle )
		sLogger.info("Closing window with handle \"" + driver.getWindowHandle() + "\"." )
		driver.close()
	}
	
	public RemoteWebDriver getDriver() {
		return this.driver
	}
	
	public RemoteWebDriver setDriver( RemoteWebDriver drv ) {
		this.driver = drv
	}
	
	public WebElement getElementByLocator( By locator ) {
		sLogger.info( "Get element by locator: " + locator.toString() )
		long startTime = System.currentTimeMillis()
		driver.manage().timeouts().implicitlyWait( COMMON_IMPLICIT_WAIT, TimeUnit.SECONDS )
		WebElement we = null
		boolean unfound = true
		int numTries = 0
		int maxTries = 5
		while ( unfound && numTries < maxTries ) {
			numTries += 1
			if ( numTries > 1 ) sLogger.info("Locating remaining time: "
				 + ( ( maxTries*COMMON_IMPLICIT_WAIT)-(COMMON_IMPLICIT_WAIT*(numTries-1) ) ) + " seconds." )
			try {
				we = driver.findElement( locator )
				unfound = false // FOUND IT
		        break;
			} catch ( StaleElementReferenceException ser ) {
				sLogger.info( "ERROR: Stale element. " + locator.toString() )
				unfound = true
			} catch ( NoSuchElementException nse ) {
				sLogger.info( "ERROR: No such element. " + locator.toString() )
				unfound = true
			}
		}
		long endTime = System.currentTimeMillis()
		long totalTime = endTime - startTime
		sLogger.info("Finished click after waiting for " + totalTime + " milliseconds.")
		driver.manage().timeouts().implicitlyWait( DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS )
		return we
	}
	
	public void initializeRemoteBrowser( String type, String ip, int port ) {
		DesiredCapabilities dc = new DesiredCapabilities()
		dc.setCapability( "takesScreenshot", false )
		dc.setCapability( "webdriver.remote.quietExceptions", true )
		try {
			if ( type.equalsIgnoreCase( "firefox" ) ) {
				dc.setBrowserName( "firefox" )
				setDriver( new RemoteWebDriver( new URL("http://" + ip + ":" + port + "/wd/hub"), dc ) )
			} else if ( type.equalsIgnoreCase( "internetExplorer" ) ) {
				dc.setBrowserName( "internet explorer" )
				setDriver( new RemoteWebDriver( new URL("http://" + ip + ":" + port + "/wd/hub"), dc ) )
			} else if ( type.equalsIgnoreCase( "chrome" ) ) {
				dc.setBrowserName( "chrome" )
				setDriver( new RemoteWebDriver( new URL("http://" + ip + ":" + port + "/wd/hub"), dc ) )
			} else {
				sLogger.info( "Invalid browser type. Cannot initialize." )
			}
		} catch ( MalformedURLException e ) {
			e.printStackTrace()
		}
		driver.manage().timeouts().implicitlyWait( DEFAULT_IMPLICIT_WAIT, TimeUnit.MILLISECONDS )
		positionMainHandle()
	}

	public void positionMainHandle() {
		setHandleCache( driver.getWindowHandles() )
		if ( handleCache.size() == 0 ) {
			mainHandle = ""
			throw new IllegalStateException("No browser window handles are open.\n" +
					"Browser is uninitialized.")
		} else if ( handleCache.size() > 1 ) {
			mainHandle = ""
			throw new IllegalStateException("More than one browser window handle is open.\n" +
					"Please close all browsers and restart test.")
		} else {
			mainHandle = driver.switchTo().defaultContent().getWindowHandle()
			setInitialWindowPosition( mainHandle, testXOffset )
		}
	}
	
	public void setHandleCache(Set<String> handleCache) {
		this.handleCache = handleCache
	}
	
	public void setInitialWindowPosition( String handle, int xOffset ) {
		sLogger.info("Setting initial window position with offset of " + xOffset )
		int xPos = Integer.parseInt( System.getProperty("windowXPosition") )
		if ( xOffset > 0 ) xPos = xPos + xOffset
		setWindowPosition( handle, Integer.parseInt( System.getProperty("windowWidth") ),
				Integer.parseInt( System.getProperty("windowHeight") ), xPos,
				Integer.parseInt( System.getProperty("windowYPosition") )
			);
	}

	public void setWindowPosition(String handle, int width, int height, int fleft, int ftop) {
		driver.switchTo().window( handle ).manage().window().setPosition( new Point(fleft, ftop) )
		driver.switchTo().window( handle ).manage().window().setSize( new Dimension( width, height) )
	}
	
}
