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
import ubc.pavlab.ndb.model.dto.MutationDTO;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class MutationDAOImpl implements MutationDAO {

    private static final Logger log = Logger.getLogger( MutationDAOImpl.class );

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID = "SELECT id, paper_id, sample_identifier, chromosome, hg19_start, hg19_stop, ref, alt, gene_id, mutation_effect, code_change, prot_change, qvalue, sift, polyphen, goodmut "
            + "FROM mutations WHERE id = ?";
    private static final String SQL_FIND_BY_GENE_ID = "SELECT id, paper_id, sample_identifier, chromosome, hg19_start, hg19_stop, ref, alt, gene_id, mutation_effect, code_change, prot_change, qvalue, sift, polyphen, goodmut "
            + "FROM mutations WHERE gene_id = ?";
    private static final String SQL_FIND_BY_PAPER_ID = "SELECT id, paper_id, sample_identifier, chromosome, hg19_start, hg19_stop, ref, alt, gene_id, mutation_effect, code_change, prot_change, qvalue, sift, polyphen, goodmut "
            + "FROM mutations WHERE paper_id = ?";
    private static final String SQL_LIST_ORDER_BY_ID = "SELECT id, paper_id, sample_identifier, chromosome, hg19_start, hg19_stop, ref, alt, gene_id, mutation_effect, code_change, prot_change, qvalue, sift, polyphen, goodmut "
            + "FROM mutations ORDER BY id";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Mutation DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * 
     * @param daoFactory The DAOFactory to construct this Mutation DAO for.
     */
    MutationDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public MutationDTO find( Integer id ) throws DAOException {
        return find( SQL_FIND_BY_ID, id );
    }

    @Override
    public List<MutationDTO> findByGeneId( Integer id ) throws DAOException {
        return findAll( SQL_FIND_BY_GENE_ID, id );
    }

    @Override
    public List<MutationDTO> findByPaperId( Integer id ) throws DAOException {
        return findAll( SQL_FIND_BY_PAPER_ID, id );
    }

    /**
     * Returns the mutation from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The mutation from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private MutationDTO find( String sql, Object... values ) throws DAOException {
        MutationDTO mutation = null;

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                mutation = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return mutation;
    }

    /**
     * Returns a list of MutationDTO from the database matching the given SQL query with the given values.
     * 
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return A list of MutationDTO from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private List<MutationDTO> findAll( String sql, Object... values ) throws DAOException {
        List<MutationDTO> mutations = new ArrayList<>();
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, sql, false, values );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                mutations.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return mutations;
    }

    @Override
    public List<MutationDTO> list() throws DAOException {
        List<MutationDTO> mutations = new ArrayList<>();

        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LIST_ORDER_BY_ID );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                mutations.add( map( resultSet ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return mutations;
    }

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to a MutationDTO.
     * 
     * @param resultSet The ResultSet of which the current row is to be mapped to a MutationDTO.
     * @return The mapped MutationDTO from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static MutationDTO map( ResultSet resultSet ) throws SQLException {
        return new MutationDTO( resultSet.getInt( "id" ), resultSet.getInt( "paper_id" ),
                resultSet.getString( "sample_identifier" ), resultSet.getString( "chromosome" ),
                resultSet.getInt( "hg19_start" ), resultSet.getInt( "hg19_stop" ), resultSet.getString( "ref" ),
                resultSet.getString( "alt" ), resultSet.getInt( "gene_id" ), resultSet.getString( "mutation_effect" ),
                resultSet.getString( "code_change" ), resultSet.getString( "prot_change" ),
                resultSet.getString( "qvalue" ), resultSet.getString( "sift" ), resultSet.getString( "polyphen" ),
                resultSet.getBoolean( "goodmut" ) );
    }
}
