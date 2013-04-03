selenium-groovy
===============

# Running selenium with groovy console
Getting your test scripts working with Selenium is a tedious job. The development cycles of adapting a test, starting the test, analysis of the failure take a long time. Especially the initialisation of selenium and browser is very slow.
This project shows how instead of running a JUnit test, you can start a groovy console, hookup a running selenium instance to it, and try out your selenium commands on the fly.

## Groovy console
The groovy console is a standard groovy component, and on startup gets variables attached. The most important one is driver. This driver has been setup by selenium webdriver code, and is the same driver you tend to use for pure selenium java. You can now create groovy scripts, and try out things like:

    elem = driver.findElement(By.className('welcomescreen'));
    println elem.text;
    assert elem.text =~ 'hello'

When you make a mistake, you will get an exception. But the good thing is: your console still lives, your selenium too. You can correct it straight away, and save the change to your groovy script. In the groovy console you can either use java or groovy, you can either run the whole script, or only selected lines. This makes it very easy to create a test script.

## Convert your scripts to JUnit tests
You will save your scripts in a directory. Next you will need to create a subclass of TestScriptEngineBase. In here you will specify the directory where the script is. Now you can run this same script as a JUnit test. You can create a separate maven target to execute the test, and also start it from a build server. The TestScriptEngineBase will start a groovy script engine and will get the same bindings as also got applied for the console. So what works in the console, also works as a JUnit test.

## Gradle setup
Gradle and Groovy are closely related. 


## Getting started

    mvn clean test -P firefox,development,run-console

## Link to instruction video
[http://www.youtube.com/watch?v=IlfLfLuceWk](http://www.youtube.com/watch?v=IlfLfLuceWk)

