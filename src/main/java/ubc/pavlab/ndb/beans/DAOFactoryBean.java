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

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.dao.DAOFactory;
import ubc.pavlab.ndb.exceptions.ConfigurationException;

/**
 * Bean injected into services in order to give access to the DAO Factory
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean(name = "daoFactoryBean")
@ApplicationScoped
public class DAOFactoryBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8655251522916437925L;

    private static final Logger log = Logger.getLogger( DAOFactoryBean.class );

    private static final String PROPERTY_DB = "ndb.db";

    private static DAOFactory daoFactory;

    @ManagedProperty("#{applicationProperties}")
    private ApplicationProperties applicationProperties;

    /**
     * 
     */
    public DAOFactoryBean() {
        log.info( "DAOFactoryBean created" );
    }

    // TODO: PostConstruct is deprecated
    @PostConstruct
    public void init() {
        log.info( "DAOFactoryBean init" );
        // Obtain DAOFactory.
        String dbKey = applicationProperties.getProperty( PROPERTY_DB );
        if ( dbKey == null ) {
            throw new ConfigurationException( "Required property '" + PROPERTY_DB + "'"
                    + " is missing in properties file '" + applicationProperties.getPropertiesFile() + "'." );
        }

        daoFactory = DAOFactory.getInstance( dbKey );

        log.info( "DAOFactory successfully obtained: " + daoFactory );
    }

    public DAOFactory getDAOFactory() {
        return daoFactory;
    }

    public void setApplicationProperties( ApplicationProperties applicationProperties ) {
        this.applicationProperties = applicationProperties;
    }

}
