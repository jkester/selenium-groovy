package qa.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.By

import qa.test.WebDriverUtils

class RunSeleniumConsole extends WebDriverUtils {

	static main(args) {
		sLogger.info("Starting selenium session with console");
		WebDriverUtils setup = WebDriverUtils.getInstance();

		Actions actions = new Actions( getDriver() );

		ConsoleWaiter waiter = new ConsoleWaiter(setup);

		sLogger.info("Setting bindings for driver,actions,utils,logger");
		waiter.setVar("driver", setup.driver);
		waiter.setVar("actions", actions);
		waiter.setVar("logger", sLogger);
		waiter.setVar("startUrl", setup.startUrl);
		waiter.setVar("By", By);
		waiter.run();
		
		setup.close();
	}
}
