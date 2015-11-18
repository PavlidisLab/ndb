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

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_STAR = "id, variant_id, genomicSuperDups, esp6500siv2_all, 1000g2014oct_all, 1000g2014oct_afr, 1000g2014oct_eas, 1000g2014oct_eur, snp138, SIFT_score, SIFT_pred, Polyphen2_HDIV_score, Polyphen2_HDIV_pred, Polyphen2_HVAR_score, Polyphen2_HVAR_pred, LRT_score, LRT_pred, MutationTaster_score, MutationTaster_pred, MutationAssessor_score, MutationAssessor_pred, FATHMM_score, FATHMM_pred, RadialSVM_score, RadialSVM_pred, LR_score, LR_pred, VEST3_score, CADD_raw, CADD_phred, GERP_RS, phyloP46way_placental, phyloP100way_vertebrate, SiPhy_29way_logOdds, exac03, clinvar_20150629";
    private static final String SQL_TABLE = "annovar_scores";
    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_VARIANT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE variant_id = ?";
    private static final String SQL_FIND_BY_MULTIPLE_VARIANT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE variant_id in (%s)";
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
    AnnovarDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public AnnovarDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public AnnovarDTO findByVariantId( Integer variantId ) throws DAOException {
        return find( SQL_FIND_BY_VARIANT_ID, variantId );
    }

    @Override
    public List<AnnovarDTO> findByVariantId( List<Integer> variantIds ) throws DAOException {
        if ( variantIds == null || variantIds.isEmpty() ) {
            return new ArrayList<>();
        }
        String sql = String.format( SQL_FIND_BY_MULTIPLE_VARIANT_ID, DAOUtil.preparePlaceHolders( variantIds.size() ) );
        return findAll( sql, variantIds.toArray() );
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
            while ( resultSet.next() ) {
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
        return new AnnovarDTO( resultSet.getInt( "id" ), resultSet.getInt( "variant_id" ),
                resultSet.getString( "genomicSuperDups" ),
                resultSet.getDouble( "esp6500siv2_all" ), resultSet.getDouble( "1000g2014oct_all" ),
                resultSet.getDouble( "1000g2014oct_afr" ), resultSet.getDouble( "1000g2014oct_eas" ),
                resultSet.getDouble( "1000g2014oct_eur" ), resultSet.getString( "snp138" ),
                resultSet.getDouble( "SIFT_score" ), resultSet.getString( "SIFT_pred" ),
                resultSet.getDouble( "Polyphen2_HDIV_score" ), resultSet.getString( "Polyphen2_HDIV_pred" ),
                resultSet.getDouble( "Polyphen2_HVAR_score" ), resultSet.getString( "Polyphen2_HVAR_pred" ),
                resultSet.getDouble( "LRT_score" ), resultSet.getString( "LRT_pred" ),
                resultSet.getDouble( "MutationTaster_score" ), resultSet.getString( "MutationTaster_pred" ),
                resultSet.getDouble( "MutationAssessor_score" ), resultSet.getString( "MutationAssessor_pred" ),
                resultSet.getDouble( "FATHMM_score" ), resultSet.getString( "FATHMM_pred" ),
                resultSet.getDouble( "RadialSVM_score" ), resultSet.getString( "RadialSVM_pred" ),
                resultSet.getDouble( "LR_score" ), resultSet.getString( "LR_pred" ),
                resultSet.getDouble( "VEST3_score" ), resultSet.getDouble( "CADD_raw" ),
                resultSet.getDouble( "CADD_phred" ), resultSet.getDouble( "GERP_RS" ),
                resultSet.getDouble( "phyloP46way_placental" ), resultSet.getDouble( "phyloP100way_vertebrate" ),
                resultSet.getDouble( "SiPhy_29way_logOdds" ), resultSet.getDouble( "exac03" ),
                resultSet.getString( "clinvar_20150629" ) );
    }

}
