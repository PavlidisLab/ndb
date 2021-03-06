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

package ubc.pavlab.ndb.beans.services;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.AnnovarDAO;
import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.dto.AnnovarDTO;

/**
 * Service layer on top of AnnovarDAO. Contains methods for fetching information related to annovar from
 * the database and creating Annovar objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class AnnovarService implements Serializable {

    private static final long serialVersionUID = 7725957614038591228L;

    private static final Logger log = Logger.getLogger( AnnovarService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    private AnnovarDAO annovarDAO;

    /**
     * 
     */
    public AnnovarService() {
        log.info( "AnnovarService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "AnnovarService init" );
        annovarDAO = daoFactoryBean.getDAOFactory().getAnnovarDAO();

    }

    protected Annovar fetchById( Integer id ) {
        if ( id == null ) {
            return null;
        }

        return map( annovarDAO.find( id ) );

    }

    protected Annovar fetchByVariantId( Integer vid ) {
        if ( vid == null ) {
            return null;
        }

        return map( annovarDAO.findByVariantId( vid ) );

    }

    private Annovar map( AnnovarDTO dto ) {
        if ( dto == null ) {
            return null;
        }

        return new Annovar( dto );

    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

}
