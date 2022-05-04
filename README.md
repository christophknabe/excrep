The Central Exception Reporting Sample Application Quick-Start FAQ
===========================================

                                                          Christoph Knabe, 2016-09-21

The Central Exception Reporting Sample Application.

- It's a little CRUD application for managing clients. You can create, edit, list, and delete clients.
- You can run it in 5 variants:  {Struts|Swing} * {Java|AspectJ} + JavaFX*Java
- The Struts UI is derived from the Struts Blank application.
- It was the companion material to a german article about Central Expection Reporting in the JavaMagazin 11.07 pages 23-27.

What platform does this application now run on?

- JDK 8, and
- Maven 3

In the Maven POMs it is configured to use

- Java 8, or AspectJ 8 respectively
- Jetty 9 (Servlet 3.0 container)
- Struts 1.3.10
- MulTEx 8.3 (an exception message text framework)

The file `pom.xml` manages the Struts, Swing, and JavaFX application using simple Java, 
and framework specific mechanisms or the Template Method Pattern for centralized exception reporting. 

The file `aspect-pom.xml` manages the Struts and Swing application using AspectJ
and comfortable aspect-oriented mechanisms for centralized exception reporting. 

How do I run and test the Struts web application using plain Java? 

- type   _mvn clean jetty:run_   at the command prompt for the web app using Struts specific ExceptionHandler.
- Browse  http://localhost:8080/
- Elect "Create Client"
- Type in and Save some clients, they will be stored in file  `Persistence.ser`  in the working directory
- Observe exception messages after data errors (e.g. empty birth date, birth date with some period characters)
- You can see the stack trace of an error message by clicking on the link "Details".
- Click button List to see the entered clients.
- Provoke a low-level exception by making the file  `Persistence.ser`  read-only and trying to Save a client.
- Stop the web app by typing &lt;Ctrl/C&gt; in the command window.

How do I run and test the Swing application using plain Java?

- type   `mvn clean test`   at the command prompt for the Swing app using Template Method Pattern.
- This uses the file `pom.xml` and the main class `swing_ui.ClientSwingApplication`.
- A GUI will appear, where you can do the same things as above.
- You can see the stack trace of an error message by clicking on the button "Show Stack Trace".
- Stop the application by closing all windows of it.

How do I run and test the JavaFX application using plain Java?

- type   `mvn clean test -Pfx`   at the command prompt for the JavaFX app using Template Method Pattern.
- This uses the file `pom.xml` and the main class `fx_ui.ClientFxApplication`.
- A GUI will appear, where you can do the same things as above.
- You can see the stack trace of an error message by clicking on the button "Details".
- Stop the application by closing all windows of it.

How do I run and test the Struts web application using AspectJ? 

- type   `mvn -f aspect-pom.xml clean jetty:run`   at the command prompt for the web app using AspectJ for exception reporting.
- In the stack traces you can see, that exceptions are caught by an around advice.


How do I run and test the Swing application using AspectJ? 

- type   `mvn -f aspect-pom.xml clean test`   at the command prompt for the Swing app using AspectJ for exception reporting.
- In the stack traces you can see, that exceptions are caught by an around advice.


Where are the Message Resources?

- In the file `MessageResources.properties`. The original, which you may edit, is under `src/main/resources/`

Why did the changes to my  `MessageResources.properties`  or other resource file disappear?
Why didn't the changes to my  `MessageResources.properties`  or other resource file appear?

- The original resource files are under  `src/main/resources/`  and copied to  `target/classes/`  during a build. 
  Change the content of `src/main/resources/MessageResources.properties` and rebuild before running it again.
  The exception message texts extracted from the JavaDoc comments of all exceptions under `src/main/java/`
  are collected during the `process-classes` phase by the `ExceptionMessagesDoclet` as configured for the `maven-javadoc-plugin`.

Where is the article?

It is in subdirectory `doc` in various formats.

## Repository History
- Originally stored at http://excrep.berlios.de/ by CVS
- From 2017-11-06 at https://app.assembla.com/spaces/excrep/git/source
- From 2022-05-04 at https://github.com/christophknabe/excrep/
