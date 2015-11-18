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

import static ubc.pavlab.ndb.dao.DAOUtil.prepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.dto.RawKVDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class RawKVDAOImpl implements RawKVDAO {

    private static final Logger log = Logger.getLogger( RawKVDAOImpl.class );

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_STAR = "id, paper_id, raw_id, `key`, value";
    private static final String SQL_TABLE = "raw_key_value";

    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_PAPER_RAW = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE paper_id = ? and raw_id=?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " ORDER BY id";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Gene DAO for the given DAOFactory. Package private so that it can be constructed inside the DAO
     * package only.
     * 
     * @param daoFactory The DAOFactory to construct this RawKV DAO for.
     */
    RawKVDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public RawKVDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public List<RawKVDTO> findByPaperAndRaw( Integer paperId, Integer rawId ) throws DAOException {
        if ( paperId == null || rawId == null ) {
            return new ArrayList<>();
        }
        return findAll( SQL_FIND_BY_PAPER_RAW, paperId, rawId );
    }

    /**
     * Returns the RawKVDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The RawKVDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private RawKVDTO find( String sql, Object... values ) throws DAOException {
        RawKVDTO dto = null;

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                dto = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return dto;
    }

    /**
     * Returns a list of RawKVDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return A list of RawKVDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private List<RawKVDTO> findAll( String sql, Object... values ) throws DAOException {
        List<RawKVDTO> l = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                l.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return l;
    }

    @Override
    public List<RawKVDTO> list() throws DAOException {
        List<RawKVDTO> l = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LIST_ORDER_BY_ID );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                l.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return l;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a RawKVDTO.
     * 
     * @param resultSet The ResultSet of which the current row is to be mapped to a RawKVDTO.
     * @return The mapped RawKVDTO from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static RawKVDTO map( ResultSet resultSet ) throws SQLException {
        return new RawKVDTO( resultSet.getInt( "id" ), resultSet.getInt( "paper_id" ), resultSet.getInt( "raw_id" ),
                resultSet.getString( "key" ), resultSet.getString( "value" ) );
    }

}
