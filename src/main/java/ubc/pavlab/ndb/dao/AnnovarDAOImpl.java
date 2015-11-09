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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String SQL_STAR = "id, variant_id, Chr, Start, End, Ref, Alt, Func_refGene, Gene_refGene, GeneDetail_refGene, ExonicFunc_refGene, AAChange_refGene, cytoBand, genomicSuperDups, esp6500siv2_all, 1000g2014oct_all, 1000g2014oct_afr, 1000g2014oct_eas, 1000g2014oct_eur, snp138, SIFT_score, SIFT_pred, Polyphen2_HDIV_score, Polyphen2_HDIV_pred, Polyphen2_HVAR_score, Polyphen2_HVAR_pred, LRT_score, LRT_pred, MutationTaster_score, MutationTaster_pred, MutationAssessor_score, MutationAssessor_pred, FATHMM_score, FATHMM_pred, RadialSVM_score, RadialSVM_pred, LR_score, LR_pred, VEST3_score, CADD_raw, CADD_phred, GERP_RS, phyloP46way_placental, phyloP100way_vertebrate, SiPhy_29way_logOdds, exac03, clinvar_20150629";
    private static final String SQL_TABLE = "annovar";
    private static final String SQL_MAP_TABLE = "annovar_gene";
    // SQL Statements

    private static final String SQL_FIND_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " WHERE id = ?";
    private static final String SQL_FIND_BY_VARIANT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE variant_id = ?";;
    private static final String SQL_FIND_BY_MULTIPLE_VARIANT_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE
            + " WHERE variant_id in (%s)";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT " + SQL_STAR + " FROM " + SQL_TABLE + " ORDER BY id";

    private static final String SQL_MAP_FIND_VARIANT_BY_GENE_ID = "select variant_id from " + SQL_MAP_TABLE
            + " ag inner join " + SQL_TABLE + " anno on ag.annovar_id = anno.id where gene_id = ?";

    private static final String SQL_MAP_GENE_BY_ANNOVAR_ID = "SELECT gene_id FROM " + SQL_MAP_TABLE
            + " where annovar_id = ?";

    private static final String SQL_MAP_GENE = "SELECT annovar_id, gene_id FROM " + SQL_MAP_TABLE
            + " ORDER BY annovar_id";

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

    @Override
    public List<Integer> findGeneIdsForAnnovarId( Integer id ) throws DAOException {
        if ( id == null ) {
            return new ArrayList<>();
        }

        List<Integer> geneIds = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_MAP_GENE_BY_ANNOVAR_ID, false, id );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                geneIds.add( resultSet.getInt( "gene_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return geneIds;
    }

    @Override
    public List<Integer> findVariantIdsByGeneId( Integer geneId ) throws DAOException {
        if ( geneId == null ) {
            return new ArrayList<>();
        }
        List<Integer> annovarIds = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_MAP_FIND_VARIANT_BY_GENE_ID, false,
                        geneId );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                annovarIds.add( resultSet.getInt( "variant_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return annovarIds;
    }

    @Override
    public Map<Integer, List<Integer>> listGeneMap() throws DAOException {
        Map<Integer, List<Integer>> annovarGene = new HashMap<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_MAP_GENE );
                ResultSet resultSet = statement.executeQuery();) {
            Integer prevAnnovarId = null;
            List<Integer> geneIds = new ArrayList<>();
            if ( resultSet.next() ) {
                prevAnnovarId = resultSet.getInt( "annovar_id" );
                geneIds.add( resultSet.getInt( "gene_id" ) );
            }

            while ( resultSet.next() ) {
                // Rows ordered by key, thus allowing a nice method for creating the map
                Integer annovarId = resultSet.getInt( "annovar_id" );
                Integer geneId = resultSet.getInt( "gene_id" );

                if ( !annovarId.equals( prevAnnovarId ) ) {
                    // New key, save previous one
                    annovarGene.put( prevAnnovarId, geneIds );
                    geneIds = new ArrayList<>();
                }

                geneIds.add( geneId );

                prevAnnovarId = annovarId;

            }
            if ( !geneIds.isEmpty() ) {
                // Save last entry
                annovarGene.put( prevAnnovarId, geneIds );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return annovarGene;
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
        return new AnnovarDTO( resultSet.getInt( "id" ), resultSet.getInt( "variant_id" ), resultSet.getString( "Chr" ),
                resultSet.getInt( "Start" ), resultSet.getInt( "End" ), resultSet.getString( "Ref" ),
                resultSet.getString( "Alt" ), resultSet.getString( "Func_refGene" ),
                resultSet.getString( "Gene_refGene" ), resultSet.getString( "GeneDetail_refGene" ),
                resultSet.getString( "ExonicFunc_refGene" ), resultSet.getString( "AAChange_refGene" ),
                resultSet.getString( "cytoBand" ), resultSet.getString( "genomicSuperDups" ),
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
