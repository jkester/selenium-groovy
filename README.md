selenium-groovy
===============

Versions
===============
Version 1.0 - Completed and checked in on April 5th, 2013

Gradle setup
===============
#### Eclipse
To get it working on a regular Eclipse 4.2.1 or later, follow these steps:
 
    1. Using the "Eclipse Marketplace" settings panel under the 
       Eclipse "Help" menu, install the Gradle tooling 
       functionality.  You can do it through the "Install New
       Software" menu, but it isn't recommended.  If Market is
       missing from your Eclipse, then add the repo:
       http://download.eclipse.org/releases/juno
       and then install the "market" and restart Eclipse.
    2. Download the .zip archive of this GitHub project 
       distribution and unzip it to your workspace.  An example:
       "C:\Eclipse32\workspace\WebDriverTestingTemplate\" .
    3. Use the Eclipse "Import" function from the Eclipse "File
       menu" to import a "Project" of type "Gradle".
    4. Browse using the import wizard to your projects "root" 
       directory.  Then click the "Build model" button.
    5. Check all checkboxes .  You could also choose to add all 
       to your "working set" if you like but it isn't required.
    6. Rebuild the dependencies by right clicking on the project
       and then choose Gradle-->Refresh All Dependencies
    7. Right click on your project and choose "Run As-->External
       Tools Configuration".  Configure a new run configuration
       to run the Gradle task "clean runAllTestsInFirefox" with
       the "-info" program argument.
    8. Optionally, you can run this project on the command line
       with "gradle.bat clean runAllTestsInFirefox --info" 
       and it will execute the project unit tests.

#### IntelliJ-IDEA
The required Gradle functionality is already built into IntelliJ-IDEA 12.1+ .  I think using IDEA is more difficult
but go ahead if you are familiar with it. 


Getting started
===============

