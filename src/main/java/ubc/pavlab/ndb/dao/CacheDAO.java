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
import ubc.pavlab.ndb.utility.Tuples;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
@Deprecated
public interface CacheDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the list of gene IDs and Symbols from the database. The list is never null and
     * is empty when the database does not contain any genes.
     * 
     * @return The list of gene IDs and Symbols from the database.
     * @throws DAOException If something fails at database level.
     */
    public List<Tuples.Tuple2<Integer, String>> listSymbolId() throws DAOException;

    /**
     * Returns the list of paper IDs and authors from the database. The list is never null and
     * is empty when the database does not contain any genes.
     * 
     * @return The list of paper IDs and authors from the database.
     * @throws DAOException If something fails at database level.
     */
    public List<Tuples.Tuple2<Integer, String>> listAuthorId() throws DAOException;

}
