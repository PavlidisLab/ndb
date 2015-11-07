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

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.dto.VariantDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class VariantDAOImpl implements VariantDAO {

    private static final Logger log = Logger.getLogger( VariantDAOImpl.class );

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_STAR = "id, paper_id, raw_variant_id, event_id, subject_id, sample_id, chromosome, start_hg19, stop_hg19, ref, alt";
    private static final String SQL_TABLE = "variant";

    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_MULTIPLE_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE id in (%s)";
    private static final String SQL_FIND_BY_PAPER_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE paper_id = ?";
    private static final String SQL_FIND_BY_EVENT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE event_id = ?";
    private static final String SQL_FIND_BY_SUBJECT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE subject_id = ?";
    private static final String SQL_FIND_BY_POSITION = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE chromosome = ? and start_hg19 >= ? and stop_hg19 <= ?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " ORDER BY id";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Annovar DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * 
     * @param daoFactory The DAOFactory to construct this Annovar DAO for.
     */
    VariantDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public VariantDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public List<VariantDTO> find( List<Integer> ids ) throws DAOException {
        if ( ids == null || ids.isEmpty() ) {
            return Lists.newArrayList();
        }
        String sql = String.format( SQL_FIND_BY_MULTIPLE_ID, DAOUtil.preparePlaceHolders( ids.size() ) );
        return findAll( sql, ids.toArray() );
    }

    @Override
    public List<VariantDTO> findByPaperId( Integer paperId ) throws DAOException {
        if ( paperId == null ) {
            return Lists.newArrayList();
        }
        return findAll( SQL_FIND_BY_PAPER_ID, paperId );
    }

    @Override
    public List<VariantDTO> findByEventId( Integer eventId ) throws DAOException {
        if ( eventId == null ) {
            return Lists.newArrayList();
        }
        return findAll( SQL_FIND_BY_EVENT_ID, eventId );
    }

    @Override
    public List<VariantDTO> findBySubjectId( Integer subjectId ) throws DAOException {
        if ( subjectId == null ) {
            return Lists.newArrayList();
        }
        return findAll( SQL_FIND_BY_SUBJECT_ID, subjectId );
    }

    @Override
    public List<VariantDTO> findByPosition( String chr, Integer start, Integer stop ) throws DAOException {
        if ( chr == null || start == null || stop == null ) {
            return Lists.newArrayList();
        }
        return findAll( SQL_FIND_BY_POSITION, chr, start, stop );
    }

    /**
     * Returns the VariantDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The VariantDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private VariantDTO find( String sql, Object... values ) throws DAOException {
        VariantDTO annovar = null;

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                annovar = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return annovar;
    }

    /**
     * Returns a list of VariantDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return A list of VariantDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private List<VariantDTO> findAll( String sql, Object... values ) throws DAOException {
        List<VariantDTO> annovars = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                annovars.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return annovars;
    }

    @Override
    public List<VariantDTO> list() throws DAOException {
        List<VariantDTO> annovars = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LIST_ORDER_BY_ID );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                annovars.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return annovars;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a GeneDTO.
     * 
     * @param resultSet The ResultSet of which the current row is to be mapped to a GeneDTO.
     * @return The mapped GeneDTO from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static VariantDTO map( ResultSet resultSet ) throws SQLException {
        return new VariantDTO( resultSet.getInt( "id" ), resultSet.getInt( "paper_id" ),
                resultSet.getInt( "raw_variant_id" ), resultSet.getInt( "event_id" ), resultSet.getInt( "subject_id" ),
                resultSet.getString( "sample_id" ), resultSet.getString( "chromosome" ),
                resultSet.getInt( "start_hg19" ), resultSet.getInt( "stop_hg19" ), resultSet.getString( "ref" ),
                resultSet.getString( "alt" ) );
    }

}
