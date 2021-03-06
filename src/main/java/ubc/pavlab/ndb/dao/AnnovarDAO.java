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

import java.util.List;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.dto.AnnovarDTO;

/**
 * This interface represents a contract for {@link AnnovarDAOImpl}. Note that currently all methods are
 * read-only.
 * 
 * @author mjacobson
 * @version $Id$
 */
public interface AnnovarDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the AnnovarDTO from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the annovar to be returned.
     * @return The AnnovarDTO from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public AnnovarDTO find( Integer id ) throws DAOException;

    /**
     * Returns the AnnovarDTO from the database matching the given Variant ID, otherwise null.
     * 
     * @param id The Variant ID of the annovar to be returned.
     * @return The AnnovarDTO from the database matching the given Variant ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public AnnovarDTO findByVariantId( Integer id ) throws DAOException;

    /**
     * Returns a list of AnnovarDTO from the database matching any of the given Variant IDs. The list is never null and
     * is empty when the database does not contain any annovars matching these Variant IDs.
     * 
     * @param ids The list of Variant IDs of the annovars to be returned.
     * @return The list of AnnovarDTO from the database matching the given Variant IDs.
     * @throws DAOException If something fails at database level.
     */
    public List<AnnovarDTO> findByVariantId( List<Integer> ids ) throws DAOException;

    /**
     * Returns a list of all AnnovarDTO from the database ordered by ID. The list is never null and
     * is empty when the database does not contain any annovars.
     * 
     * @return A list of all AnnovarDTO from the database ordered by ID.
     * @throws DAOException If something fails at database level.
     */
    public List<AnnovarDTO> list() throws DAOException;

}
