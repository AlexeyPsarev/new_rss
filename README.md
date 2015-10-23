# Building and deploying the application

1. You should have the Apache Tomcat 7 installed in your computer.

2. To build the application change your current directory to the *RssAggregator*
 subdirectory and type:  
```
mvn package
```

The RSS Aggregator application uses the H2 database. All necessary tables will
 be created during application building.

3. Copy the *rssaggregator.war* file to the *webapps* subdirectory of your Tomcat
 root directory.

4. Open the *context.xml* file located in the *conf* subdirectory of your Tomcat
 root directory. Create a resource definition for your Context. The Context
 element should look like the following.  
```
<Context>

	<Resource name="jdbc/UsersDB" auth="Container" type="javax.sql.DataSource"
        maxTotal="100" maxIdle="30" maxWaitMillis="10000"
        username="admin" password="admin" driverClassName="org.h2.Driver"
		url="jdbc:h2:~/PUBLIC;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=8082;SCHEMA=PUBLIC;DB_CLOSE_ON_EXIT=FALSE"/>

</Context>
```

Save and close that file.

5. Launch the Tomcat Server.  

Open a browser and browse to http://localhost:8080/rssaggregator .

