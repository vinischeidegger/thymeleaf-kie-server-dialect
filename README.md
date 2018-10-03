# KIE Servier Dialect (for use with kie business apps + thymeleaf)

This is a KIE Server dialect for jBPM business applications (start.jbpm.org) used 
with integration with Thymeleaf. 

It allows you to interact with many services exposed in your jBPM business application
straight in your Thymeleaf html templates, without writing any integration code at all.

# Installing the dialect
Once you have created your business appliation on start.jbpm.org you can easily install this dialect
in just two steps:

1. in your service module pom.xml add dependecies:
```xml
<dependency>
  <groupId>org.jbpm.addons</groupId>
  <artifactId>thymeleaf-kie-server-dialect</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

The Kie Servier Dialect dependency will pull in Thymeleaf integration for you automatically.

2. in your service module create or edit existing /src/main/resources/META-INF/spring.factories file to add:

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  org.jbpm.addons.config.KieServerDialectConfig
```

this will enable automatic config of the kie server dialect beans and properties

And that's it! You are now ready to start using the dialect in your Thymeleaf templates!

# Using the dialect
Once registered you can start using the dialect markup in your Thymeleaf templates.
Regardless of the template markup used, you should add in your html node of the template:

1. Starting a process from Thymeleaf template:
In your template html node add:

```html
xmlns:kieserver="http://jbpm.org/"
```

# Display all process definitions
In the body section of your template add:
```html
<kieserver:showprocesses/>
```

This will generate a table on your page displaying all processes that are defined and registered, for example:

![Sample process definitions](sampleprocessdefs.png?raw=true)

Once more processes are registered or unregistered a simple page-refresh will update 
this table for you. 

# Starting a business process
In the body section of your page add:
```html
<kieserver:startprocess processid="${processid}" 
                        containerid="${containerid}" 
                        processinputs="${processinputs}"/>
```

where ${processid} ${containerid} and processinputs are your Model attributes added in the 
@Get mapping of your template. 

Notes:
* processid and containerid attributes can also accept hard-coded string values
where as processinput has to be an expression mapping to a Map<String, Object> model object.

* processid and containerid are required attributes,
where processinputs is optional (in case your process started does not take any input).

When your page is being parsed by Thymeleaf the business process will be started. 
and the output will be an alert, for example:

![Sample process start result](sampleprocessstartresult.png?raw=true)
