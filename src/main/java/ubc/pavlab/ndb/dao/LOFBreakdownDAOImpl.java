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
import ubc.pavlab.ndb.model.dto.LOFBreakdownDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class LOFBreakdownDAOImpl implements LOFBreakdownDAO {

    private static final Logger log = Logger.getLogger( LOFBreakdownDAOImpl.class );

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_STAR = "id, gene_id, score, count_denovo, count_other, count_lof, denovo_miss_inframe_deletion, other_miss_inframe_deletion, miss_inframe_cadd_total, miss_inframe_in_SSC, exac_frequency_gt_point01, exac_frequency_gt_point001, exac_frequency_gt_point0001, postsynaptic_density_associated, interaction_with_FMRPL, count_lof_in_exac_genename, count_lof_in_exac_annovarname, SFARI_ASD_gene_score_genename, SFARI_ASD_gene_score_annovarname, interaction_PTEN, interaction_NLGN3";
    private static final String SQL_TABLE = "loss_of_function";
    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_GENE_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE gene_id = ?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " ORDER BY id";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Mutation DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * 
     * @param daoFactory The DAOFactory to construct this Mutation DAO for.
     */
    LOFBreakdownDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public LOFBreakdownDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public LOFBreakdownDTO findByGeneId( Integer geneId ) throws DAOException {
        return find( SQL_FIND_BY_GENE_ID, geneId );
    }

    /**
     * Returns the LOFBreakdown from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The LOFBreakdown from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private LOFBreakdownDTO find( String sql, Object... values ) throws DAOException {
        LOFBreakdownDTO lof = null;

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                lof = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return lof;
    }

    @Override
    public List<LOFBreakdownDTO> list() throws DAOException {
        List<LOFBreakdownDTO> lofs = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LIST_ORDER_BY_ID );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                lofs.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return lofs;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a MutationDTO.
     * 
     * @param resultSet The ResultSet of which the current row is to be mapped to a MutationDTO.
     * @return The mapped MutationDTO from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static LOFBreakdownDTO map( ResultSet resultSet ) throws SQLException {
        return new LOFBreakdownDTO( resultSet.getInt( "id" ), resultSet.getInt( "gene_id" ),
                resultSet.getDouble( "score" ), resultSet.getInt( "count_denovo" ), resultSet.getInt( "count_other" ),
                resultSet.getInt( "count_lof" ), resultSet.getInt( "denovo_miss_inframe_deletion" ),
                resultSet.getInt( "other_miss_inframe_deletion" ), resultSet.getDouble( "miss_inframe_cadd_total" ),
                resultSet.getBoolean( "miss_inframe_in_SSC" ), resultSet.getInt( "exac_frequency_gt_point01" ),
                resultSet.getInt( "exac_frequency_gt_point001" ), resultSet.getInt( "exac_frequency_gt_point0001" ),
                resultSet.getBoolean( "postsynaptic_density_associated" ),
                resultSet.getBoolean( "interaction_with_FMRPL" ), resultSet.getInt( "count_lof_in_exac_genename" ),
                resultSet.getInt( "count_lof_in_exac_annovarname" ),
                resultSet.getString( "SFARI_ASD_gene_score_genename" ),
                resultSet.getString( "SFARI_ASD_gene_score_annovarname" ), resultSet.getBoolean( "interaction_PTEN" ),
                resultSet.getBoolean( "interaction_NLGN3" ) );
    }
}
