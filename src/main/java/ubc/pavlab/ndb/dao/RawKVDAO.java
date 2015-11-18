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
import ubc.pavlab.ndb.model.dto.RawKVDTO;

/**
 * This interface represents a contract for {@link RawKVDAOImpl}. Note that currently all methods are
 * read-only.
 * 
 * @author mjacobson
 * @version $Id$
 */
public interface RawKVDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the Key Value Pair from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the Key Value Pair to be returned.
     * @return The Key Value Pair from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public RawKVDTO find( Integer id ) throws DAOException;

    /**
     * Returns a list of Key Value Pairs from the database matching the given paper Id and raw Id. The list is never
     * null and is empty when the database does not contain any Key Value Pairs.
     * 
     * @param paperId The paper Id of the Key Value Pair to be returned.
     * @param rawId The raw Id of the Key Value Pair to be returned.
     * @return The list of Key Value Pairs from the database matching the given paper Id and raw Id.
     * @throws DAOException If something fails at database level.
     */
    public List<RawKVDTO> findByPaperAndRaw( Integer paperId, Integer rawId ) throws DAOException;

    /**
     * Returns a list of all Key Value Pairs from the database ordered by ID. The list is never null and is empty when
     * the database does not contain any Key Value Pairs.
     * 
     * @return A list of all Key Value Pairs from the database ordered by ID.
     * @throws DAOException If something fails at database level.
     */
    public List<RawKVDTO> list() throws DAOException;

}
