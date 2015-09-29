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
import ubc.pavlab.ndb.model.dto.AnnovarDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class AnnovarDAOImpl implements AnnovarDAO {

    private static final Logger log = Logger.getLogger( AnnovarDAOImpl.class );

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID = "SELECT id, gene_id, annovar_symbol FROM annovar_map WHERE id = ?";
    private static final String SQL_FIND_BY_GENE_ID = "SELECT id, gene_id, annovar_symbol FROM annovar_map WHERE gene_id = ?";
    private static final String SQL_FIND_BY_GENE_IDS = "SELECT id, gene_id, annovar_symbol FROM annovar_map WHERE gene_id in (%s)";
    private static final String SQL_FIND_BY_SYMBOL = "SELECT id, gene_id, annovar_symbol FROM annovar_map WHERE annovar_symbol = ?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT id, gene_id, annovar_symbol FROM annovar_map ORDER BY id";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Annovar DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * 
     * @param daoFactory The DAOFactory to construct this Annovar DAO for.
     */
    AnnovarDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public AnnovarDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public AnnovarDTO find( String symbol ) throws DAOException {
        return find( SQL_FIND_BY_SYMBOL, symbol );
    }

    @Override
    public List<AnnovarDTO> findByGeneId( Integer geneId ) throws DAOException {
        return findAll( SQL_FIND_BY_GENE_ID, geneId );
    }

    @Override
    public List<AnnovarDTO> findByGeneId( List<Integer> geneIds ) throws DAOException {
        if ( geneIds == null || geneIds.isEmpty() ) {
            return new ArrayList<>();
        }
        String sql = String.format( SQL_FIND_BY_GENE_IDS, DAOUtil.preparePlaceHolders( geneIds.size() ) );
        return findAll( sql, geneIds.toArray() );
    }

    /**
     * Returns the AnnovarDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The AnnovarDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private AnnovarDTO find( String sql, Object... values ) throws DAOException {
        AnnovarDTO annovar = null;

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
     * Returns a list of AnnovarDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return A list of AnnovarDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private List<AnnovarDTO> findAll( String sql, Object... values ) throws DAOException {
        List<AnnovarDTO> annovars = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                annovars.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return annovars;
    }

    @Override
    public List<AnnovarDTO> list() throws DAOException {
        List<AnnovarDTO> annovars = new ArrayList<>();

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
    private static AnnovarDTO map( ResultSet resultSet ) throws SQLException {
        return new AnnovarDTO( resultSet.getInt( "id" ), resultSet.getInt( "gene_id" ),
                resultSet.getString( "annovar_symbol" ) );
    }

}
