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
import ubc.pavlab.ndb.model.dto.PaperDTO;

/**
 * This interface represents a contract for {@link PaperDAOImpl}. Note that currently all methods are read-only.
 * 
 * @author mbelmadani
 * @version $Id$
 */
public interface PaperDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the paper from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the paper to be returned.
     * @return The paper from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public PaperDTO find( Integer id ) throws DAOException;

    /**
     * Returns the paper from the database matching the given author, otherwise null.
     * 
     * @param author The author of the paper to be returned.
     * @return The paper from the database matching the given author, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public PaperDTO find( String author ) throws DAOException;

    /**
     * Returns a list of all papers from the database ordered by paper ID. The list is never null and is empty when the
     * database does not contain any papers.
     * 
     * @return A list of all papers from the database ordered by paper ID.
     * @throws DAOException If something fails at database level.
     */
    public List<PaperDTO> list() throws DAOException;

}
