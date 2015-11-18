/*
 * The ndb project
 * 
 * Copyright (c) 2015 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubc.pavlab.ndb.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ubc.pavlab.ndb.exceptions.ConfigurationException;

/**
 * This class represents a DAO factory for an SQL database. You can use {@link #getInstance(String)} to obtain a new
 * instance for the given database name. The specific instance returned depends on the properties file configuration.
 * You can obtain DAO's for the DAO factory instance using the DAO getters.
 * <p>
 * This class requires a properties file accessible by DAOProperties with among others the following properties:
 * 
 * <pre>
 * name.url *
 * name.driver
 * name.username
 * name.password
 * </pre>
 * 
 * Those marked with * are required, others are optional and can be left away or empty. Only the username is required
 * when any password is specified.
 * <ul>
 * <li>The 'name' must represent the database name in {@link #getInstance(String)}.</li>
 * <li>The 'name.url' must represent either the JDBC URL or JNDI name of the database.</li>
 * <li>The 'name.driver' must represent the full qualified class name of the JDBC driver.</li>
 * <li>The 'name.username' must represent the username of the database login.</li>
 * <li>The 'name.password' must represent the password of the database login.</li>
 * </ul>
 * If you specify the driver property, then the url property will be assumed as JDBC URL. If you omit the driver
 * property, then the url property will be assumed as JNDI name. When using JNDI with username/password preconfigured,
 * you can omit the username and password properties as well.
 * <p>
 * Here are basic examples of valid properties for a database with the name 'javabase':
 * 
 * <pre>
 * javabase.jdbc.url = jdbc:mysql://localhost:3306/javabase
 * javabase.jdbc.driver = com.mysql.jdbc.Driver
 * javabase.jdbc.username = java
 * javabase.jdbc.password = d$7hF_r!9Y
 * </pre>
 * 
 * <pre>
 * javabase.jndi.url = jdbc / javabase
 * </pre>
 * 
 * Here is a basic use example:
 * 
 * <pre>
 * DAOFactory javabase = DAOFactory.getInstance( &quot;javabase.jdbc&quot; );
 * UserDAO userDAO = javabase.getUserDAO();
 * </pre>
 */
public abstract class DAOFactory {

    // Constants ----------------------------------------------------------------------------------

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_DRIVER = "driver";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    // Actions ------------------------------------------------------------------------------------

    public static DAOFactory getInstance( String name ) throws ConfigurationException {
        if ( name == null ) {
            throw new ConfigurationException( "Database name is null." );
        }
        DAOProperties properties = new DAOProperties( name );
        String url = properties.getProperty( PROPERTY_URL, true );
        String driverClassName = properties.getProperty( PROPERTY_DRIVER, false );
        String password = properties.getProperty( PROPERTY_PASSWORD, false );
        String username = properties.getProperty( PROPERTY_USERNAME, password != null );
        return getInstance( url, driverClassName, password, username );
    }

    /**
     * Returns a new DAOFactory instance for the given database name.
     * 
     * @param name The database name to return a new DAOFactory instance for.
     * @return A new DAOFactory instance for the given database name.
     * @throws ConfigurationException If the database name is null, or if the properties file is missing in the
     *         classpath or cannot be loaded, or if a required property is missing in the properties file, or if either
     *         the driver cannot be loaded or the datasource cannot be found.
     */
    private static DAOFactory getInstance( String url, String driverClassName, String password, String username )
            throws ConfigurationException {

        DAOFactory instance;

        // If driver is specified, then load it to let it register itself with DriverManager.
        if ( driverClassName != null ) {
            try {
                Class.forName( driverClassName );
            } catch ( ClassNotFoundException e ) {
                throw new ConfigurationException( "Driver class '" + driverClassName + "' is missing in classpath.",
                        e );
            }
            instance = new DriverManagerDAOFactory( url, username, password );
        }

        // Else assume URL as DataSource URL and lookup it in the JNDI.
        else {
            DataSource dataSource;
            try {
                dataSource = ( DataSource ) new InitialContext().lookup( url );
            } catch ( NamingException e ) {
                throw new ConfigurationException( "DataSource '" + url + "' is missing in JNDI.", e );
            }
            if ( username != null ) {
                instance = new DataSourceWithLoginDAOFactory( dataSource, username, password );
            } else {
                instance = new DataSourceDAOFactory( dataSource );
            }
        }

        return instance;
    }

    /**
     * Returns a connection to the database. Package private so that it can be used inside the DAO package only.
     * 
     * @return A connection to the database.
     * @throws SQLException If acquiring the connection fails.
     */
    abstract Connection getConnection() throws SQLException;

    // DAO implementation getters -----------------------------------------------------------------

    public GeneDAO getGeneDAO() {
        return new GeneDAOImpl( this );
    }

    public PaperDAO getPaperDAO() {
        return new PaperDAOImpl( this );
    }

    public AnnovarDAO getAnnovarDAO() {
        return new AnnovarDAOImpl( this );
    }

    public VariantDAO getVariantDAO() {
        return new VariantDAOImpl( this );
    }

    public RawKVDAOImpl getRawKVDAO() {
        return new RawKVDAOImpl( this );
    }

    // You can add more DAO implementation getters here.

}

// Default DAOFactory implementations -------------------------------------------------------------

/**
 * The DriverManager based DAOFactory.
 */
class DriverManagerDAOFactory extends DAOFactory {
    private String url;
    private String username;
    private String password;

    DriverManagerDAOFactory( String url, String username, String password ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection( url, username, password );
    }
}

/**
 * The DataSource based DAOFactory.
 */
class DataSourceDAOFactory extends DAOFactory {
    private DataSource dataSource;

    DataSourceDAOFactory( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    @Override
    Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

/**
 * The DataSource-with-Login based DAOFactory.
 */
class DataSourceWithLoginDAOFactory extends DAOFactory {
    private DataSource dataSource;
    private String username;
    private String password;

    DataSourceWithLoginDAOFactory( DataSource dataSource, String username, String password ) {
        this.dataSource = dataSource;
        this.username = username;
        this.password = password;
    }

    @Override
    Connection getConnection() throws SQLException {
        return dataSource.getConnection( username, password );
    }
}