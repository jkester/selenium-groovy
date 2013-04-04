package qa.test

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.By

import static qa.test.Utils.*

class RunSeleniumConsole {

	static main(args) {
		sLogger.info("Starting selenium session with console");
		WebDriverSetup setup = WebDriverSetup.getInstance();

		//set bindings
		Utils utils = new Utils(setup.driver,setup.startUrl);
		utils.setUserName(setup.username);
		utils.setPassWord(setup.password);
		Actions actions = new Actions(setup.driver);

		ConsoleWaiter waiter = new ConsoleWaiter(setup);

		sLogger.info("Setting bindings for driver,actions,utils,logger");
		waiter.setVar("driver", setup.driver);
		waiter.setVar("actions", actions);
		waiter.setVar("utils", utils);
		waiter.setVar("logger", sLogger);
		waiter.setVar("startUrl", setup.startUrl);
		waiter.setVar("By", By);
		waiter.run();
		
		setup.close();
	}
}
