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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class StatsDAOImpl implements StatsDAO {

    private static final Logger log = Logger.getLogger( StatsDAOImpl.class );

    // SQL Constants ----------------------------------------------------------------------------------

    private static final String SQL_PAPER_TABLE = "papers";
    private static final String SQL_VARIANT_TABLE = "variant";
    private static final String SQL_GENE_MAP_TABLE = "variant_gene";

    // SQL Statements

    private static final String SQL_PAPER_CNT = "select COUNT(*) from " + SQL_PAPER_TABLE;

    // Variants

    private static final String SQL_VARIANT_CNT_BY_PAPER_ID = "select COUNT(*) from " + SQL_VARIANT_TABLE
            + " where paper_id=?";
    private static final String SQL_EVENT_CNT_BY_PAPER_ID = "select COUNT(distinct event_id) from " + SQL_VARIANT_TABLE
            + " where paper_id=?";

    // private static final String SQL_EVENT_IDS_BY_PAPER_ID = "SELECT COUNT(*) FROM " + SQL_VARIANT_TABLE
    // + " v WHERE EXISTS (SELECT null "
    // + "FROM variant v1 where v.event_id = v1.event_id AND v.id <> v1.id AND v.paper_id=?)";

    private static final String SQL_EVENT_OVERLAPS_BY_PAPER = "select paper_id, COUNT(distinct event_id) cnt from "
            + SQL_VARIANT_TABLE + " where event_id in (select distinct event_id from " + SQL_VARIANT_TABLE
            + " where paper_id = ?) group by paper_id";

    private static final String SQL_EVENT_CNT_BY_CATEGORY = "select category , COUNT(distinct event_id ) cnt from "
            + SQL_VARIANT_TABLE + " group by category ORDER BY cnt DESC";

    private static final String SQL_EVENT_CNT_BY_CONTEXT = "select func , COUNT(distinct event_id ) cnt from "
            + SQL_VARIANT_TABLE + " group by func ORDER BY cnt DESC";

    private static final String SQL_VARIANT_CNT = "select COUNT(*) from " + SQL_VARIANT_TABLE;
    private static final String SQL_EVENT_CNT = "select COUNT(distinct event_id) from " + SQL_VARIANT_TABLE;

    private static final String SQL_SUBJECT_CNT = "select COUNT(distinct subject_id) from " + SQL_VARIANT_TABLE;
    private static final String SQL_PAPER_CNT_WITH_VARIANTS = "select COUNT(distinct paper_id) from "
            + SQL_VARIANT_TABLE;

    private static final String SQL_TOP_GENES_BY_VARIANT = "select gene_id, COUNT(*) cnt from " + SQL_VARIANT_TABLE
            + " var inner join " + SQL_GENE_MAP_TABLE
            + " vmap on var.id=vmap.variant_id group by gene_id order by cnt DESC limit ?";

    private static final String SQL_TOP_GENES_BY_EVENT = "select gene_id, COUNT(distinct event_id) cnt from "
            + SQL_VARIANT_TABLE + " var inner join " + SQL_GENE_MAP_TABLE
            + " vmap on var.id=vmap.variant_id group by gene_id order by cnt DESC limit ?";

    private static final String SQL_TOP_GENES_BY_PAPER = "select gene_id, COUNT(distinct paper_id) cnt from "
            + SQL_VARIANT_TABLE + " var inner join " + SQL_GENE_MAP_TABLE
            + " vmap on var.id=vmap.variant_id group by gene_id order by cnt DESC limit ?";

    private static final String SQL_VARIANT_CNT_BY_CONTEXT_FOR_PAPER = "SELECT func, COUNT(*) FROM " + SQL_VARIANT_TABLE
            + " where paper_id=? group by func";

    private static final String SQL_EVENT_CNT_BY_CONTEXT_FOR_PAPER = "SELECT func, COUNT(DISTINCT event_id) FROM "
            + SQL_VARIANT_TABLE + " where paper_id=? group by func";

    private static final String SQL_VARIANT_CNT_BY_CATEGORY_FOR_PAPER = "SELECT category, COUNT(*) FROM "
            + SQL_VARIANT_TABLE + " where paper_id=? group by category";

    private static final String SQL_EVENT_CNT_BY_CATEGORY_FOR_PAPER = "SELECT category, COUNT(DISTINCT event_id) FROM "
            + SQL_VARIANT_TABLE + " where paper_id=? group by category";

    private static final String SQL_DENOVO_CNT = " SELECT COUNT( DISTINCT event_id ) FROM " + SQL_VARIANT_TABLE
            + " WHERE denovo = 'yes'; ";

    private static final String SQL_LOF_CNT = " SELECT COUNT( DISTINCT event_id ) FROM " + SQL_VARIANT_TABLE
            + " WHERE func = 'splicing'  OR category in ('frameshift insertion', 'frameshift deletion', 'stopgain', 'splicing', 'stoploss', 'frameshift substituition') ; ";

    private static final String SQL_DENOVO_LOF_GENES = "SELECT gene_ID, count(distinct event_id) as cnt  " + "FROM "
            + SQL_VARIANT_TABLE + " as var " + " inner join " + SQL_GENE_MAP_TABLE + " vmap on var.id=vmap.variant_id "
            + " WHERE denovo = 'yes' AND "
            + "(func = 'splicing'  OR category in ('frameshift insertion', 'frameshift deletion', 'stopgain', 'splicing', 'stoploss', 'frameshift substituition')) "
            + "GROUP BY gene ORDER BY cnt DESC limit ?";

    // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an Gene DAO for the given DAOFactory. Package private so that it can be constructed inside the DAO
     * package only.
     * 
     * @param daoFactory The DAOFactory to construct this Gene DAO for.
     */
    StatsDAOImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    // Aggregate

    // Variants

    @Override
    public int findTotalVariantsByPaperId( Integer paperId ) throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_VARIANT_CNT_BY_PAPER_ID, false,
                        paperId );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalEventsByPaperId( Integer paperId ) throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_CNT_BY_PAPER_ID, false, paperId );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalVariants() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_VARIANT_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalEvents() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_EVENT_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalLof() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_LOF_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalDenovo() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_DENOVO_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalSubjects() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_SUBJECT_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public int findTotalPapersWithVariants() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_PAPER_CNT_WITH_VARIANTS );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

    @Override
    public Map<Integer, Integer> overlappingEventsBetweenPapers( Integer paper_id ) throws DAOException {
        Map<Integer, Integer> counts = Maps.newHashMap();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_OVERLAPS_BY_PAPER, false,
                        paper_id );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                counts.put( resultSet.getInt( "paper_id" ), resultSet.getInt( "cnt" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return counts;
    }

    @Override
    public List<Tuple2<String, Integer>> findTotalEventsByCategory() throws DAOException {

        List<Tuple2<String, Integer>> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_CNT_BY_CATEGORY, false );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                String category = resultSet.getString( "category" );
                category = StringUtils.isBlank( category ) ? "other" : category;
                results.add( new Tuple2<>( category, resultSet.getInt( "cnt" ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Tuple2<String, Integer>> findTotalEventsByContext() throws DAOException {

        List<Tuple2<String, Integer>> results = Lists.newArrayList();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_CNT_BY_CONTEXT, false );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                results.add( new Tuple2<>( resultSet.getString( "func" ), resultSet.getInt( "cnt" ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Integer> findTopGenesByDenovoLof( Integer n ) throws DAOException {
        log.info( "SQL: Find genes by denovo AND lof. " );
        List<Integer> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_DENOVO_LOF_GENES, false, n );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                results.add( resultSet.getInt( "gene_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Integer> findTopGenesByVariantCnt( Integer n ) throws DAOException {
        if ( n == null || n <= 0 ) {
            n = 5;
        }

        List<Integer> top = Lists.newArrayList();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_TOP_GENES_BY_VARIANT, false, n );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                top.add( resultSet.getInt( "gene_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return top;
    }

    @Override
    public List<Integer> findTopGenesByEventCnt( Integer n ) throws DAOException {
        if ( n == null || n <= 0 ) {
            n = 5;
        }
        List<Integer> top = Lists.newArrayList();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_TOP_GENES_BY_EVENT, false, n );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                top.add( resultSet.getInt( "gene_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return top;

    }

    @Override
    public List<Integer> findTopGenesByPaperCnt( Integer n ) throws DAOException {
        if ( n == null || n <= 0 ) {
            n = 5;
        }
        List<Integer> top = Lists.newArrayList();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_TOP_GENES_BY_PAPER, false, n );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                top.add( resultSet.getInt( "gene_id" ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }

        return top;

    }

    @Override
    public List<Tuple2<String, Integer>> findTotalVariantsByContextForPaperId( Integer paperId ) throws DAOException {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_VARIANT_CNT_BY_CONTEXT_FOR_PAPER, false,
                        paperId );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                results.add( new Tuple2<>( resultSet.getString( 1 ), resultSet.getInt( 2 ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Tuple2<String, Integer>> findTotalEventsByContextForPaperId( Integer paperId ) throws DAOException {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_CNT_BY_CONTEXT_FOR_PAPER, false,
                        paperId );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                results.add( new Tuple2<>( resultSet.getString( 1 ), resultSet.getInt( 2 ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Tuple2<String, Integer>> findTotalVariantsByCategoryForPaperId( Integer paperId ) throws DAOException {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_VARIANT_CNT_BY_CATEGORY_FOR_PAPER,
                        false, paperId );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                String category = resultSet.getString( 1 );
                category = StringUtils.isBlank( category ) ? "other" : category;
                results.add( new Tuple2<>( category, resultSet.getInt( 2 ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    @Override
    public List<Tuple2<String, Integer>> findTotalEventsByCategoryForPaperId( Integer paperId ) throws DAOException {
        List<Tuple2<String, Integer>> results = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = prepareStatement( connection, SQL_EVENT_CNT_BY_CATEGORY_FOR_PAPER, false,
                        paperId );
                ResultSet resultSet = statement.executeQuery();) {
            while ( resultSet.next() ) {
                String category = resultSet.getString( 1 );
                category = StringUtils.isBlank( category ) ? "other" : category;
                results.add( new Tuple2<>( category, resultSet.getInt( 2 ) ) );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return results;
    }

    // Papers

    @Override
    public int findTotalPapers() throws DAOException {
        int total = 0;
        try (Connection connection = daoFactory.getConnection();
                PreparedStatement statement = connection.prepareStatement( SQL_PAPER_CNT );
                ResultSet resultSet = statement.executeQuery();) {
            if ( resultSet.next() ) {
                total = resultSet.getInt( 1 );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        }
        return total;
    }

}
