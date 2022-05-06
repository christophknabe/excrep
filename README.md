The Central Exception Reporting Sample Application Quick-Start FAQ
===========================================

                                                          Christoph Knabe, 2022-05-06

## 1 Introduction

The Central Exception Reporting Sample Application.

- Demonstrates how to use Central Exception Reporting with various interface frameworks.
- It was the companion material to a german article about Central Exception Reporting in the [JavaMagazin 11.07 pages 23-27](doc/JavaMagazin11.07p23-27_KnabeHashemi.pdf).
- It's a little CRUD application for managing clients. You can create, edit, list, and delete clients.
- You can run it in 5 variants:  {Struts|Swing} * {Java|AspectJ} + JavaFX*Java

What platform does this application now run on?

- JDK 8, and
- Maven 3

In the Maven POMs it is configured to use

- Java 8, or AspectJ 8 respectively
- Jetty 9 (Servlet 3.0 container)
- Struts 1.3.10
- MulTEx 8.4 (an exception message text framework)

The file `pom.xml` manages the Struts, Swing, and JavaFX application using simple Java, 
and framework specific mechanisms or the Template Method Pattern for Centralized Exception Reporting.

The file `aspect-pom.xml` manages the Struts and Swing application using AspectJ
and comfortable aspect-oriented mechanisms for Centralized Exception Reporting.

For all examples is assumed that your default JDK is JDK 8 with JavaFX included.
If that is not the case, see below section **Troubleshooting**.

## 2. Usage

### 2.1 How to run the Struts web application using plain Java? 

- type   `mvn clean jetty:run`   at the command prompt for the web app using Struts specific ExceptionHandler.
- Browse  http://localhost:8080/
- Click on "Create a Client"
- Type in and Save some clients, they will be stored in file  `Persistence.ser`  in the working directory
- Observe exception messages after data errors (e.g. empty birth date, birth date with some period characters)
- You can see the stack trace of an error message by clicking on the link "Details".
- Click button List to see the entered clients.
- Provoke a low-level exception by making the file  `Persistence.ser`  read-only and trying to Save a client. You will see the presentation of an exception chain.
- Stop the web app by typing &lt;Ctrl/C&gt; in the command window.

### 2.2 How to run the Swing application using plain Java?

- type   `mvn clean integration-test`   at the command prompt for the Swing app using Template Method Pattern.
- This uses the file `pom.xml` and the main class `swing_ui.ClientSwingApplication`.
- A GUI will appear, where you can do the same things as above.
- You can see the stack trace of an error message by clicking on the button "Show Stack Trace".
- Stop the application by closing all windows of it.

### 2.3 How to run the JavaFX application using plain Java?

- Type   `mvn clean integration-test -Pfx`   at the command prompt for the JavaFX app using Template Method Pattern.
- This uses the file `pom.xml` and the main class `fx_ui.ClientFxApplication`.
- A GUI will appear, where you can do the same things as above.
- You can see the stack trace of an error message by clicking on the button "Details".
- Stop the application by closing all windows of it.

### 2.4 How to run the Struts web application using AspectJ? 

- Type   `mvn -f aspect-pom.xml clean jetty:run`   at the command prompt for the web app using AspectJ for exception reporting.
- In the stack traces you can see, that exceptions are caught by an around advice.


### 2.5 How to run the Swing application using AspectJ? 

- type   `mvn -f aspect-pom.xml clean integration-test`   at the command prompt for the Swing app using AspectJ for exception reporting.
- In the stack traces you can see, that exceptions are caught by an around advice.

## 3 Other Questions

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

## 4 Troubleshooting
### 4.1 The JavaFX classes are not found on Linux: 
Most distributions of JDK 8 on Linux do not contain JavaFX. 
If you have that problem, you should get the Liberica JDK 8 by https://sdkman.io/
Then you should either use it as default or prefix the Maven command by `./javafx8.sh` as in the following example:
`./javafx8.sh mvn clean integration-test`
For that you have to update the JDK version number in this script `jdkVersion=8.0.332.fx-librca` to your currently installed version.

### 4.2 Your Maven is too old
If you have that problem, you should use the [Maven Wrapper](https://maven.apache.org/wrapper/),
which is delivered with this project. Then you should call it e.g.
`./mvnw clean integration-test`
If you want to additionally enforce the specific JavaFX JDK you can combine both modifications:
`./javafx8.sh ./mvnw clean integration-test`
That you can do with all Maven calls shown in this README.

## 5 Repository History
- Originally stored at http://excrep.berlios.de/ by CVS
- From 2017-11-06 at https://app.assembla.com/spaces/excrep/git/source
- From 2022-05-04 at https://github.com/christophknabe/excrep/
