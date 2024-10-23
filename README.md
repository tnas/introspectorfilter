# Introspector Filter
A simple tool for filtering collections through annotation-driven introspection.

The [introspectorfilter-jsf](https://github.com/tnas/introspectorfilter/tree/main/introspectorfilter-jsf) 
module is an example of how to use the filter. Run the commands to see 
the JSF project working (all commands are executed from the project root directory:
- `cd introspectorfilter-jsf`
- `mvn tomcat7:run-war`

If any dependency errors for the introspectorfilter-core module are triggered,
go back to the project root directory and run:
- `mvn clean install`
- run the previous two commands again

The JSF (Primefaces) project can be accessed via the URL
[http://localhost:8080/posts.xhtml](http://localhost:8080/posts.xhtml)

## JDK
The tool requires at least **Java 21 version** to run.

