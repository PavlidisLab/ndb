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

package ubc.pavlab.ndb.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.exceptions.ConfigurationException;

/**
 * Extends java.util.Properties with a loader which looks for files a path order:
 * 1) Given filepath if not null
 * 2) Current user.dir
 * 
 * @author mjacobson
 * @version $Id$
 */
public class PropertiesFile extends Properties {

    private static final long serialVersionUID = -7155218450625381016L;

    private static final Logger log = Logger.getLogger( PropertiesFile.class );

    public PropertiesFile() {
        super();
    }

    public void load( String fileName, String filePath, String backupFilePath ) throws ConfigurationException {
        InputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream( Paths.get( filePath, fileName ).toFile() );
            log.info( "Found PROPERTIES_FILE : (" + fileName + ") in: (" + filePath + ")" );
        } catch ( FileNotFoundException e ) {
            log.warn( "Could not find PROPERTIES_FILE : (" + fileName + ") in: (" + filePath + ")" );
            try {
                propertiesFile = new FileInputStream( Paths.get( backupFilePath, fileName ).toFile() );
                log.info( "Found PROPERTIES_FILE : (" + fileName + ") in: (" + backupFilePath + ")" );
            } catch ( FileNotFoundException e2 ) {
                log.warn( "Could not find PROPERTIES_FILE : (" + fileName + ") in: (" + backupFilePath + ")" );
            }
        }

        if ( propertiesFile == null ) {
            throw new ConfigurationException( "Properties file '" + fileName + "' is missing." );
        }

        try {
            super.load( propertiesFile );
        } catch ( IOException e ) {
            throw new ConfigurationException( "Cannot load properties file '" + fileName + "'.", e );
        }

        try {
            propertiesFile.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
