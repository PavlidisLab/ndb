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

ndb.properties
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
                <!-- ndb production configuration (only required for dumping data & updating the heatmap) -->
                <ndb.db.host>[HOST]</ndb.db.host>
                <ndb.db.port>[PORT]</ndb.db.port>
                <ndb.db.database>[DATABASE]</ndb.db.database>
                <ndb.db.user>[USER]</ndb.db.user>
                <ndb.db.password>[PASSWORD]</ndb.db.password>
                <!-- ndb testing configuration -->
                <ndb.testdb.name>ndbtest</ndb.testdb.name>
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

```mvn
mvn package
```

To update the heatmap and data dumps, you must provide `-Pexport-data-and-visualization` to Maven. Generating the heatmap and VCF output require Python 3, Bokeh and Pandas.

```mvn
mvn package -Pexport-data-and-visualization
```

The heatmap will be updated under `src/main/webapp/resources/static/variant_heatmap_latest.html` and the data export under `target/etc/data/export_latest.tsv` and `target/etc/data/export_latest.vcf`.

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

* Java 8
* Tomcat 8.5
* MySQL 5.7+
* Maven 3.0.5+

## Authors

* **Pavlidis Lab** - *Developers and maintainers* - [GitHub](https://github.com/PavlidisLab/)

See also the list of [contributors](https://github.com/PavlidisLab/ndb) who participated in this project.

## License

The source code for this project is licensed under the Apache 2.0 - see the [LICENSE](LICENSE) file for details
