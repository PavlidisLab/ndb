# ndb
Web application and database providing a harmonized and comprehensive collection of genetic variants published in peer-reviewed literature.

For latest application code, see the [development branch](https://github.com/PavlidisLab/ndb/tree/development).

## Getting Started

### Local installation

* Clone the git repository. 
* Create these three files (content is an example only) in your home and create symlinks to them in your tomcat directory:

db.properties
```
ndb_staging.url=java:comp/env/jdbc/ndb_staging
ndb_staging.username=[USER]
ndb_staging.password=[PASSWORD]

ndb_production.url=java:comp/env/jdbc/ndb_production
ndb_production.username=[USER]
ndb_production.password=[PASSWORD]
```

db.properties
```
ndb.db=ndb_staging
ndb.email=hi@example.com
ndb.admin.email=admin@example.com
```

In your ~/.m2/ directory, add or edit your `settings.xml` file.
```
<?xml version="1.0"?>
<settings>
    <!-- see quimby /local/bamboo/.m2/settings.xml -->
    <localRepository>/home/[LINUXUSER]/.m2/repository</localRepository>
    <profiles>
        <profile>
            <id>dev</id>
            <properties>             
                <!-- ndb -->
                <ndb.db.name>ndb</ndb.db.name>
                <ndb.testdb.name>ndbtest</ndb.testdb.name>
                <ndb.db.build.user>[USER]</ndb.db.build.user>
                <ndb.db.build.password>[PASSWORD]</ndb.db.build.password>
                <ndb.db.build.url>jdbc:mysql://127.0.0.1:3306/ndb</ndb.db.build.url>
                <ndb.testdb.build.user>[USER]</ndb.testdb.build.user>
                <ndb.testdb.build.password>[PASSWORD]</ndb.testdb.build.password>
                <ndb.testdb.build.url>jdbc:mysql://127.0.0.1:3306/ndbtest</ndb.testdb.build.url>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>dev</activeProfile>
        <activeProfile>local-deploy</activeProfile>
        <!--activeProfile>eclipse build</activeProfile-->
    </activeProfiles>
</settings>
```

Finally, edit username and password fields in ndb/src/main/webapp/META-INF/context.xml

### Live deployment 
Contact pavlab-support@msl.ubc.ca for further information.

### Troubleshooting
#### com.mysql.jdbc.driver problem
If you get errors with com.mysql.jdbc.driver during deployment, download "mysql-connector-java" (https://dev.mysql.com/downloads/connector/j/) and put the jar (unpacked from the tar.gz) to your tomcat lib directory.

#### sql_mode=only_full_group_by problem
If the project is deployed but instead of homepage you see an error like this:
> Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'gene_id' which is not functionally dependent on columns in GROUP BY clause; this is incompatible with sql_mode=only_full_group_by
This can be fixed by configuration via the instructions here for creating a local.cnf file: Developer Workstation Setup#MySQL

Alternatively, the manual instructions:
* Then run a mysql terminal and remove the 'ONLY_FULL_GROUP_BY' flag from global.sql_mode variable.
* Useful commands for that:
```
#returns current content of the sql_mode variable
mysql> select @@global.sql_mode;
  
#Copy-pasted the output of the first command and removed the ONLY_FULL_GROUP_BY part
mysql> set GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION'
```



### Prerequisites

* Java EE 8 
* Tomcat7
* MySQL 5.6
* Maven 3.3.9

## Authors

* **Pavlidis Lab** - *Developers and maintainers* - [GitHub](https://github.com/PavlidisLab/)

See also the list of [contributors](https://github.com/PavlidisLab/ndb) who participated in this project.

## License

This project is licensed under the ___ - see the [LICENSE.md](LICENSE.md) file for details
