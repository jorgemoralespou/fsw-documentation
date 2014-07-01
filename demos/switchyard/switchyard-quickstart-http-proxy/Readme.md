# Http proxy service
This demo shows how to create a basic Http Proxy to invoke any backed service, while being able to do any logic in the middle.

## camel-http module
This demo uses camel-http component as reference so we need to install this extension.

Required jars:

- camel-http-2.10.0.redhat-60024.jar  
- commons-codec-1.6.jar  
- commons-httpclient-3.1.jar  
- module.xml

Contents of module.xml

````
<module xmlns="urn:jboss:module:1.0" name="org.apache.camel.http">

    <resources>
        <resource-root path="camel-http-2.10.0.redhat-60024.jar"/>
        <resource-root path="commons-httpclient-3.1.jar"/>
        <resource-root path="commons-codec-1.6.jar"/>
    </resources>

    <dependencies>
        <module name="org.slf4j"/>
        <module name="javax.api"/>
        <module name="javax.servlet.api"/>
        <module name="org.apache.camel.core"/>
        <module name="org.apache.commons.logging"/>
    </dependencies>
</module>
````
