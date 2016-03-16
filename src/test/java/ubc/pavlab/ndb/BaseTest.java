package ubc.pavlab.ndb;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.ApplicationProperties;
import ubc.pavlab.ndb.dao.DAOFactory;
import ubc.pavlab.ndb.exceptions.ConfigurationException;

/*
 * The ndb project
 * 
 * Copyright (c) 2016 University of British Columbia
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

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class BaseTest {

    private static final Logger log = Logger.getLogger( BaseTest.class );

    private static final String PROPERTY_TESTDB = "ndb.testdb";

    protected static final ApplicationProperties applicationProperties;
    protected static final DAOFactory daoFactory;

    static {

        applicationProperties = new ApplicationProperties();
        applicationProperties.init();
        applicationProperties.getBasePropertiesFile().mute();

        String testdbKey = applicationProperties.getProperty( PROPERTY_TESTDB );
        if ( testdbKey == null ) {
            throw new ConfigurationException( "Required Property '" + PROPERTY_TESTDB + "'"
                    + " is missing in properties file '" + applicationProperties.getPropertiesFile() + "'." );
        }

        daoFactory = DAOFactory.getInstance( testdbKey );
        log.info( "TestDAOFactory successfully obtained: " + daoFactory );
    }

    public BaseTest() {

    }

}
