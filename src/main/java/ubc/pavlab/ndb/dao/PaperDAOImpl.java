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
import ubc.pavlab.ndb.model.dto.PaperDTO;

/**
 * TODO Document Me
 * 
 * @author mbelmadani
 * @version $Id$
 */
public class PaperDAOImpl implements PaperDAO {

    private static final Logger log = Logger.getLogger( PaperDAOImpl.class );

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_STAR = "id, url, author, paper_table, mut_reporting, scope,parents, cohort, cohort_source, cohort_size, reported_effects";
    private static final String SQL_TABLE = "papers";

    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_AUTHOR = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE author = ?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " ORDER BY id";

    // Stats SQL

    private static final String SQL_STATS_PAPER_CNT = "select COUNT(*) from " + SQL_TABLE;

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Gene DAO for the given DAOFactory. Package private so that it can be constructed inside the DAO
     * package only.
     * 
     * @param daoFactory The DAOFactory to construct this Gene DAO for.
     */
    PaperDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public PaperDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public PaperDTO find( String author ) throws DAOException {
        return find( SQL_FIND_BY_AUTHOR, author );
    }

    /**
     * Returns the paper from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The paper from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private PaperDTO find( String sql, Object... values ) throws DAOException {
        PaperDTO paper = null;

        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                paper = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return paper;
    }

    @Override
    public List<PaperDTO> list() throws DAOException {
        List<PaperDTO> papers = new ArrayList<>();

        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LIST_ORDER_BY_ID );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                papers.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return papers;
    }

    // Stats

    @Override
    public int findTotalPapers() throws DAOException {
        int total = 0;
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_STATS_PAPER_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a PaperDTO.
     * 
     * @param resultSet The ResultSet of which the current row is to be mapped to a PaperDTO.
     * @return The mapped PaperDTO from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static PaperDTO map( ResultSet resultSet ) throws SQLException {
        return new PaperDTO( resultSet.getInt( "id" ), resultSet.getString( "url" ), resultSet.getString( "author" ),
                resultSet.getString( "paper_table" ), resultSet.getString( "mut_reporting" ),
                resultSet.getString( "scope" ), resultSet.getBoolean( "parents" ), resultSet.getString( "cohort" ),
                resultSet.getString( "cohort_source" ), resultSet.getInt( "cohort_size" ),
                resultSet.getString( "reported_effects" ) );
    }

}
