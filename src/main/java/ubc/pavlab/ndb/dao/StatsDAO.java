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

import java.util.List;
import java.util.Map;

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * This interface represents a contract for {@link StatsDAOImpl}. Note that currently all methods are read-only.
 * 
 * @author mjacobson
 * @version $Id$
 */
public interface StatsDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the total number of papers.
     * 
     * @return the total number of papers.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalPapers() throws DAOException;

    /**
     * Returns the total number of variants for a given paper Id.
     * 
     * @param paperId paper Id to find the total number of variants for.
     * @return the total number of variants for the given paper Id.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalVariantsByPaperId( Integer paperId ) throws DAOException;

    /**
     * Returns the total number of events for a given paper Id.
     * 
     * @param paperId paper Id to find the total number of events for.
     * @return the total number of events for the given paper Id.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalEventsByPaperId( Integer paperId ) throws DAOException;

    /**
     * Returns the total number of variants.
     * 
     * @return the total number of variants.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalVariants() throws DAOException;

    /**
     * Returns the total number of events.
     * 
     * @return the total number of events.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalEvents() throws DAOException;

    /**
     * Returns the total number of LoF variants.
     * 
     * @return the total number of LoF variants.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalLof() throws DAOException;

    /**
     * Returns the total number of de novo variants.
     * 
     * @return the total number of de novo variants.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalDenovo() throws DAOException;

    /**
     * Returns the total number of subjects.
     *
     * @return the total number of subjects.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalSubjects() throws DAOException;


    /**
     * Returns the total number of subjects based on the paper display_count.
     *  This value is mainly computed/partially manually entered in the
     *  non-computable database based on knowledge of subject counts.
     *  (E.g. if the sample ids are missing, it's sometimes unclear how
     *  many subjects are in the paper variant tables.)
     *
     * @return the total number of display subjects.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalDisplaySubjects() throws DAOException;

    /**
     * Returns the total number of papers that have variants.
     * 
     * @return the total number of papers that have variants.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalPapersWithVariants() throws DAOException;

    /**
     * Returns the top n genes by variant cnt. The list is never null.
     * 
     * @param n number of top genes to return, default 5.
     * @return the top n genes by variant cnt.
     * @throws DAOException If something fails at database level.
     */
    public List<Integer> findTopGenesByVariantCnt( Integer n ) throws DAOException;

    /**
     * Returns the top n genes by event cnt. The list is never null.
     * 
     * @param n number of top genes to return, default 5.
     * @return the top n genes by event cnt.
     * @throws DAOException If something fails at database level.
     */
    public List<Integer> findTopGenesByEventCnt( Integer n ) throws DAOException;

    /**
     * Returns the top n genes by paper cnt. The list is never null.
     * 
     * @param n number of top genes to return, default 5.
     * @return the top n genes by paper cnt.
     * @throws DAOException If something fails at database level.
     */
    public List<Integer> findTopGenesByPaperCnt( Integer n ) throws DAOException;

    public List<Tuple2<String, Integer>> findTotalVariantsByContextForPaperId( Integer paperId ) throws DAOException;

    public List<Tuple2<String, Integer>> findTotalEventsByContextForPaperId( Integer paperId ) throws DAOException;

    public List<Tuple2<String, Integer>> findTotalVariantsByCategoryForPaperId( Integer paperId ) throws DAOException;

    public List<Tuple2<String, Integer>> findTotalEventsByCategoryForPaperId( Integer paperId ) throws DAOException;

    public List<Tuple2<String, Integer>> findTotalEventsByCategory() throws DAOException;

    public List<Tuple2<String, Integer>> findTotalEventsByContext() throws DAOException;

    public List<Integer> findTopGenesByDenovoLof( Integer n ) throws DAOException;

    public Map<Integer, Integer> overlappingEventsBetweenPapers( Integer paper_id ) throws DAOException;

}
