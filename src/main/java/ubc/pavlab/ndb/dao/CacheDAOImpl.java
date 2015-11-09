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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.utility.Tuples;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
@Deprecated
public class CacheDAOImpl implements CacheDAO {
    private static final Logger log = Logger.getLogger( CacheDAOImpl.class );

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_SYMBOL_ID_MAP = "SELECT id, symbol from genes";
    private static final String SQL_AUTHOR_ID_MAP = "SELECT id, author from papers";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Gene DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * 
     * @param daoFactory The DAOFactory to construct this Gene DAO for.
     */
    CacheDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    @Override
    public List<Tuple2<Integer, String>> listSymbolId() throws DAOException {
        List<Tuple2<Integer, String>> idSymbolList = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_SYMBOL_ID_MAP );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                idSymbolList.add( new Tuples.Tuple2<Integer, String>( resultSet.getInt( "id" ),
                        resultSet.getString( "symbol" ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return idSymbolList;
    }

    @Override
    public List<Tuples.Tuple2<Integer, String>> listAuthorId() throws DAOException {
        List<Tuple2<Integer, String>> idSymbolList = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_AUTHOR_ID_MAP );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                idSymbolList.add( new Tuples.Tuple2<Integer, String>( resultSet.getInt( "id" ),
                        resultSet.getString( "author" ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return idSymbolList;
    }
}
