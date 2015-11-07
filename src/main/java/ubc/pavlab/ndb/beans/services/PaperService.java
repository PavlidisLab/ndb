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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.PaperDAO;
import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.dto.PaperDTO;

/**
 * Service layer on top of GeneDAO. Contains methods for fetching information related to genes from the database and
 * creating Gene objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class PaperService implements Serializable {

    private static final long serialVersionUID = 3813762194987799804L;

    private static final Logger log = Logger.getLogger( PaperService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    private PaperDAO paperDAO;

    /**
     * 
     */
    public PaperService() {
        log.info( "PaperService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "PaperService init" );
        paperDAO = daoFactoryBean.getDAOFactory().getPaperDAO();

    }

    /**
     * Returns the paper from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the paper to be returned.
     * @return The paper from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    protected Paper fetchPaper( Integer id ) {
        return map( paperDAO.find( id ) );
    }

    /**
     * Returns the paper from the database matching the given author, otherwise null.
     * 
     * @param The author of the paper to be returned.
     * @return The paper from the database matching the given author, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    protected Paper fetchPaper( String author ) {
        return map( paperDAO.find( author ) );
    }

    /**
     * Returns a list of all papers from the database ordered by paper ID. The list is never null and is empty when the
     * database does not contain any papers.
     * 
     * @return A list of all papers from the database ordered by paper ID.
     * @throws DAOException If something fails at database level.
     */
    protected List<Paper> listPapers() {
        List<Paper> paperList = new ArrayList<>();
        for ( PaperDTO dto : paperDAO.list() ) {
            paperList.add( map( dto ) );
        }
        return paperList;
    }

    private static Paper map( PaperDTO dto ) {
        return new Paper( dto );
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }
}
