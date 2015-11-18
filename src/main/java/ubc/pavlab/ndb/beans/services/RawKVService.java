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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;

import ubc.pavlab.ndb.beans.DAOFactoryBean;
import ubc.pavlab.ndb.dao.RawKVDAO;
import ubc.pavlab.ndb.model.dto.RawKVDTO;

/**
 * Service layer on top of AnnovarDAO. Contains methods for fetching information related to annovar from
 * the database and creating Annovar objects.
 * 
 * @author mjacobson
 * @version $Id$
 */
@ManagedBean
@ApplicationScoped
public class RawKVService implements Serializable {

    private static final long serialVersionUID = 7725957614038591228L;

    private static final Logger log = Logger.getLogger( RawKVService.class );

    @ManagedProperty("#{daoFactoryBean}")
    private DAOFactoryBean daoFactoryBean;

    private RawKVDAO rawKVDAO;

    /**
     * 
     */
    public RawKVService() {
        log.info( "RawKVService created" );
    }

    @PostConstruct
    public void init() {
        log.info( "RawKVService init" );
        rawKVDAO = daoFactoryBean.getDAOFactory().getRawKVDAO();

    }

    protected Map<String, String> fetchByPaperAndRaw( Integer paperId, Integer rawId ) {
        if ( paperId == null || rawId == null ) {
            return Maps.newHashMap();
        }
        return map( rawKVDAO.findByPaperAndRaw( paperId, rawId ) );
    }

    private Map<String, String> map( List<RawKVDTO> dtos ) {
        if ( dtos == null || dtos.isEmpty() ) {
            return Maps.newHashMap();
        }
        Builder<String, String> kvBuilder = new ImmutableMap.Builder<String, String>();
        for ( RawKVDTO dto : dtos ) {

            kvBuilder.put( dto.getKey(), dto.getValue() );
        }
        return kvBuilder.build();
    }

    public void setDaoFactoryBean( DAOFactoryBean daoFactoryBean ) {
        this.daoFactoryBean = daoFactoryBean;
    }

}
