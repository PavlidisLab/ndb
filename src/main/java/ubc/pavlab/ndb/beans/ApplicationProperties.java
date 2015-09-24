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

package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.utility.PropertiesFile;

/**
 * Holds settings from the properties file. Alter the static fields here to meet your requirements if necessary.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class ApplicationProperties implements Serializable {

    private static final long serialVersionUID = 3703327169558285473L;

    private static final Logger log = Logger.getLogger( ApplicationProperties.class );

    private static final String PROPERTIES_PATH = "/usr/local/tomcat/";
    private static final String PROPERTIES_BACKUP_PATH = System.getProperty( "user.dir" );
    private static final String PROPERTIES_FILE = "ndb.properties";

    private PropertiesFile prop = new PropertiesFile();

    @PostConstruct
    public void init() {
        log.info( "ApplicationProperties init" );
        prop.load( PROPERTIES_FILE, PROPERTIES_PATH, PROPERTIES_BACKUP_PATH );
        for ( Entry<Object, Object> e : prop.entrySet() ) {
            log.info( e.getKey().toString() + " : " + e.getValue().toString() );
        }
    }

    public String getProperty( String key ) {
        return prop.getProperty( key );
    }

    public boolean contains( String key ) {
        return prop.contains( key );
    }

    public void reload() {
        init();
    }

    public String getPropertiesFile() {
        return PROPERTIES_FILE;
    }
}
