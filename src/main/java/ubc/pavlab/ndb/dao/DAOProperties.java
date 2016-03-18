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

import ubc.pavlab.ndb.exceptions.ConfigurationException;
import ubc.pavlab.ndb.utility.PropertiesFile;

/**
 * This class immediately loads the DAO properties file 'X.properties' once in memory and provides a constructor
 * which takes the specific key which is to be used as property key prefix of the DAO properties file. There is a
 * property getter which only returns the property prefixed with 'specificKey.' and provides the option to indicate
 * whether the property is mandatory or not.
 */
public class DAOProperties {

    // Constants ----------------------------------------------------------------------------------

    // private static final String PROPERTIES_FILE = "database.properties";
    private static final String PROPERTIES_BACKUP_PATH = "/usr/local/tomcat/";
    private static final String PROPERTIES_PATH = System.getProperty( "user.home" );
    private static final String PROPERTIES_FILE = "db.properties";
    private static final PropertiesFile PROPERTIES = new PropertiesFile();

    static {

        PROPERTIES.load( PROPERTIES_FILE, PROPERTIES_PATH, PROPERTIES_BACKUP_PATH );

    }

    // Vars ---------------------------------------------------------------------------------------

    private String specificKey;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct a DAOProperties instance for the given specific key which is to be used as property key prefix of the
     * DAO properties file.
     * 
     * @param specificKey The specific key which is to be used as property key prefix.
     * @throws ConfigurationException During class initialization if the DAO properties file is missing in the
     *         classpath or cannot be loaded.
     */
    public DAOProperties( String specificKey ) throws ConfigurationException {
        this.specificKey = specificKey;
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the DAOProperties instance specific property value associated with the given key with the option to
     * indicate whether the property is mandatory or not.
     * 
     * @param key The key to be associated with a DAOProperties instance specific value.
     * @param mandatory Sets whether the returned property value should not be null nor empty.
     * @return The DAOProperties instance specific property value associated with the given key.
     * @throws ConfigurationException If the returned property value is null or empty while it is mandatory.
     */
    public String getProperty( String key, boolean mandatory ) throws ConfigurationException {
        String fullKey = specificKey + "." + key;
        String property = PROPERTIES.getProperty( fullKey );

        if ( property == null || property.trim().length() == 0 ) {
            if ( mandatory ) {
                throw new ConfigurationException( "Required property '" + fullKey + "'"
                        + " is missing in properties file '" + PROPERTIES_FILE + "'." );
            } else {
                // Make empty value null. Empty Strings are evil.
                property = null;
            }
        }

        return property;
    }

}
