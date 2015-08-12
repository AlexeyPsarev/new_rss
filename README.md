# Building and deploying the application

1. You should have the MySQL DBMS and Apache Tomcat 7 installed in your computer.

2. Launch the MySQL client and type:  
```
create database rss_db;
use rss_db;
source RssAggregator/src/main/resources/sql/createTables.sql;
```

Type  
```
quit
```

to quit the MySQL client.

3. Open the *context.xml* file located in the *conf* subdirectory of your Tomcat
 root directory. Create a resource definition for your Context. The Context
 element should look like the following.  
```
<Context>

	<Resource name="jdbc/UsersDB" auth="Container" type="javax.sql.DataSource"
        maxTotal="100" maxIdle="30" maxWaitMillis="10000"
        username="admin" password="pass" driverClassName="com.mysql.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/rss_db"/>

</Context>
```

Save and close that file.

4. Launch the Tomcat Server.  
 Change your current directory to the *RssAggregator* subdirectory and type:  
```
mvn tomcat7:run
```

Open a browser and browse to http://localhost:8080/rssaggregator .

