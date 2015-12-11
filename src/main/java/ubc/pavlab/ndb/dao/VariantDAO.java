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

import ubc.pavlab.ndb.exceptions.DAOException;
import ubc.pavlab.ndb.model.dto.VariantDTO;

/**
 * This interface represents a contract for {@link VariantDAOImpl}. Note that currently all methods are
 * read-only.
 * 
 * @author mjacobson
 * @version $Id$
 */
public interface VariantDAO {
    // Actions ------------------------------------------------------------------------------------

    /**
     * Returns the VariantDTO from the database matching the given ID, otherwise null.
     * 
     * @param id The ID of the variant to be returned.
     * @return The VariantDTO from the database matching the given ID, otherwise null.
     * @throws DAOException If something fails at database level.
     */
    public VariantDTO find( Integer id ) throws DAOException;

    /**
     * Returns a list of VariantDTO from the database matching any of the given IDs. The list is never null and
     * is empty when the database does not contain any variants matching these IDs.
     * 
     * @param ids The list of IDs of the variants to be returned.
     * @return The list of VariantDTO from the database matching the given IDs.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> find( List<Integer> ids ) throws DAOException;

    /**
     * Returns a list of VariantDTO from the database matching the given Paper ID. The list is never null and
     * is empty when the database does not contain any variants matching the Paper ID.
     * 
     * @param ids The Paper ID of the variant to be returned.
     * @return The list of VariantDTO from the database matching the given Paper ID.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> findByPaperId( Integer id ) throws DAOException;

    /**
     * Returns a list of VariantDTO from the database matching the given Event ID. The list is never null and
     * is empty when the database does not contain any variants matching the Event ID.
     * 
     * @param ids The Event ID of the variant to be returned.
     * @return The list of VariantDTO from the database matching the given Event ID.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> findByEventId( Integer id ) throws DAOException;

    /**
     * Returns a list of VariantDTO from the database matching the given Subject ID. The list is never null and
     * is empty when the database does not contain any variants matching the Subject ID.
     * 
     * @param ids The Subject ID of the variant to be returned.
     * @return The list of VariantDTO from the database matching the given Subject ID.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> findBySubjectId( Integer id ) throws DAOException;

    /**
     * Returns a list of VariantDTO from the database within the given chromosome and coordinate range. The list is
     * never null and is empty when the database does not contain any variants within the chromosome and coordinate
     * range.
     * 
     * @param chr The chromosome to search.
     * @param start The start coordinate in Hg19.
     * @param stop The stop coordinate in Hg19.
     * @return The list of VariantDTO from the database within the chromosome and coordinate range.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> findByPosition( String chr, Integer start, Integer stop ) throws DAOException;

    /**
     * Returns a list of all VariantDTO from the database ordered by ID. The list is never null and
     * is empty when the database does not contain any variants.
     * 
     * @return A list of all VariantDTO from the database ordered by ID.
     * @throws DAOException If something fails at database level.
     */
    public List<VariantDTO> list() throws DAOException;

    /**
     * Returns a list of all Gene Ids from the database matching the given Variant Id. The list is never null and
     * is empty when the database does not contain any Gene Ids matching this Variant Id.
     * 
     * @return A list of all Gene Ids from the database matching the given Variant Id.
     * @throws DAOException If something fails at database level.
     */
    public List<Integer> findGeneIdsForVariantId( Integer id ) throws DAOException;

    /**
     * Returns a list of all Variant Ids from the database matching the given Gene Id. The list is never null and
     * is empty when the database does not contain any Variant Ids matching this Gene Id.
     * 
     * @return A list of all Variant Ids from the database matching the given Gene Id.
     * @throws DAOException If something fails at database level.
     */
    public List<Integer> findVariantIdsForGeneId( Integer id ) throws DAOException;

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
     * Returns the total number of subjects.
     * 
     * @return the total number of subjects.
     * @throws DAOException If something fails at database level.
     */
    public int findTotalSubjects() throws DAOException;

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

}
