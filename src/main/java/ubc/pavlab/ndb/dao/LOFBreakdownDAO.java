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
import ubc.pavlab.ndb.model.dto.LOFBreakdownDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public interface LOFBreakdownDAO {
    // Actions ------------------------------------------------------------------------------------
    /**
     * Returns the LOFBreakdownDTO from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the LOFBreakdownDTO to be returned.
     * @return The LOFBreakdownDTO from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public LOFBreakdownDTO find( Integer id ) throws DAOException;

    /**
     * Returns the LOFBreakdownDTO from the database matching the given gene ID, otherwise null.
     * 
     * @param geneId The gene ID of the LOFBreakdownDTO to be returned.
     * @return The LOFBreakdownDTO from the database matching the given gene ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public LOFBreakdownDTO findByGeneId( Integer geneId ) throws DAOException;

    /**
     * Returns a list of all LOFBreakdownDTO from the database ordered by ID. The list is never null and is empty when
     * the database does not contain any LOFBreakdownDTO.
     * 
     * @return A list of all LOFBreakdownDTO from the database ordered by ID.
     * @throws DAOException If something fails at database level.
     */
    public List<LOFBreakdownDTO> list() throws DAOException;

}