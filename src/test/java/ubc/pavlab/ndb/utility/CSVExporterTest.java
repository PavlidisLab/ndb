/*
 * The ndb project
 * 
 * Copyright (c) 2016 University of British Columbia
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

package ubc.pavlab.ndb.utility;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.model.Annovar;
import ubc.pavlab.ndb.model.Event;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.model.Variant;
import ubc.pavlab.ndb.model.dto.GeneDTO;
import ubc.pavlab.ndb.model.dto.PaperDTO;
import ubc.pavlab.ndb.model.enums.Category;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class CSVExporterTest {
    private static final Logger log = Logger.getLogger( CSVExporterTest.class );
    private static final ArrayList<String> categories = Lists.newArrayList( "category1", "category2", "category3" );
    private CSVExporter csvExporter;

    List<Variant> variants;

    List<Event> events;

    @Before
    public void setup() {
        csvExporter = new CSVExporter( "test.csv" );

        variants = createMockVariants();

        events = Event.groupVariants( variants );
    }

    @After
    public void after() {
        csvExporter = null;
        variants = null;
        events = null;
    }

    private List<Variant> createMockVariants() {
        List<Variant> variants = Lists.newArrayList();
        Variant v = Mockito.mock( Variant.class );
        Mockito.when( v.getEventId() ).thenReturn( 1 );
        Mockito.when( v.getSubjectId() ).thenReturn( 1 );
        Mockito.when( v.getRef() ).thenReturn( "A" );
        Mockito.when( v.getAlt() ).thenReturn( "T" );
        Mockito.when( v.getChromosome() ).thenReturn( "1" );
        Mockito.when( v.getStart() ).thenReturn( 100 );
        Mockito.when( v.getStartHg19() ).thenReturn( 100 );
        Mockito.when( v.getStop() ).thenReturn( 200 );
        Mockito.when( v.getStopHg19() ).thenReturn( 200 );
        Mockito.when( v.getFunc() ).thenReturn( "function" );
        Mockito.when( v.getCategory() ).thenReturn( Category.stopgain );

        //Gene
        List<Gene> genes = Lists.newArrayList();
        Gene g = new Gene( new GeneDTO( 1, "GENE1", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        g = new Gene( new GeneDTO( 2, "GENE2", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        Mockito.when( v.getGenes() ).thenReturn( genes );

        //Annovar
        Annovar a = Mockito.mock( Annovar.class );
        Mockito.when( a.getCaddPhred() ).thenReturn( 10.0 );

        Mockito.when( v.getAnnovar() ).thenReturn( a );

        //Paper
        Paper p = new Paper(
                new PaperDTO(
                        1,
                        null,
                        null,
                        "key1",
                        null,
                        null,
                        null,
                        "author1",
                        null,
                        null,
                        null,
                        "0",
                        null,
                        null,
                        null,
                        0,
                        false,
                        null) );
        Mockito.when( v.getPaper() ).thenReturn( p );

        variants.add( v );

        // Second variant

        v = Mockito.mock( Variant.class );
        Mockito.when( v.getEventId() ).thenReturn( 2 );
        Mockito.when( v.getSubjectId() ).thenReturn( 2 );
        Mockito.when( v.getRef() ).thenReturn( "G" );
        Mockito.when( v.getAlt() ).thenReturn( "C" );
        Mockito.when( v.getChromosome() ).thenReturn( "2" );
        Mockito.when( v.getStart() ).thenReturn( 1000 );
        Mockito.when( v.getStartHg19() ).thenReturn( 1000 );
        Mockito.when( v.getStop() ).thenReturn( 2000 );
        Mockito.when( v.getStopHg19() ).thenReturn( 2000 );
        Mockito.when( v.getFunc() ).thenReturn( "function" );
        Mockito.when( v.getCategory() ).thenReturn( Category.synonymousSNV );

        //Gene
        genes = Lists.newArrayList();
        g = new Gene( new GeneDTO( 3, "GENE1B", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        g = new Gene( new GeneDTO( 4, "GENE2B", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        Mockito.when( v.getGenes() ).thenReturn( genes );

        //Annovar
        a = Mockito.mock( Annovar.class );
        Mockito.when( a.getCaddPhred() ).thenReturn( 10.0 );

        Mockito.when( v.getAnnovar() ).thenReturn( a );

        //Paper
        p = new Paper(
                new PaperDTO(
                        2,
                        null,
                        null,
                        "key2",
                        null,
                        null,
                        null,
                        "author2",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                         null,
                        0,
                        false,
                        null) );
        Mockito.when( v.getPaper() ).thenReturn( p );

        variants.add( v );

        // Third variant makes #2 complex

        v = Mockito.mock( Variant.class );
        Mockito.when( v.getEventId() ).thenReturn( 2 );
        Mockito.when( v.getSubjectId() ).thenReturn( 2 );
        Mockito.when( v.getRef() ).thenReturn( "G" );
        Mockito.when( v.getAlt() ).thenReturn( "C" );
        Mockito.when( v.getChromosome() ).thenReturn( "2" );
        Mockito.when( v.getStart() ).thenReturn( 1001 );
        Mockito.when( v.getStartHg19() ).thenReturn( 1001 );
        Mockito.when( v.getStop() ).thenReturn( 2001 );
        Mockito.when( v.getStopHg19() ).thenReturn( 2001 );
        Mockito.when( v.getFunc() ).thenReturn( "function" );
        Mockito.when( v.getCategory() ).thenReturn( Category.synonymousSNV );

        //Gene
        genes = Lists.newArrayList();
        g = new Gene( new GeneDTO( 3, "GENE1B", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        g = new Gene( new GeneDTO( 4, "GENE2B", "", "", null, null, null, null, null, null, null ) );
        genes.add( g );
        Mockito.when( v.getGenes() ).thenReturn( genes );

        //Annovar
        a = Mockito.mock( Annovar.class );
        Mockito.when( a.getCaddPhred() ).thenReturn( 10.0 );

        Mockito.when( v.getAnnovar() ).thenReturn( a );

        //Paper
        p = new Paper(
                new PaperDTO( 3,
                        null,
                        null,
                        "key3",
                        null,
                        null,
                        null,
                        "author3",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        false,
                        null) );
        Mockito.when( v.getPaper() ).thenReturn( p );

        variants.add( v );

        return variants;

    }

    @Test
    public void testLoadData() {
        //        "Variant Event ID", "Gene", "Subject", "REF", "ALT", "Location", "Context",
        //        "Effects", "CADD Phred", "Sources"

        csvExporter.loadData( events );

        byte[] bytes = csvExporter.getContent();

        Assert.assertNotEquals( bytes.length, 0 );

        String s = new String( bytes );

        String xy = StringEscapeUtils.escapeJava( s );

        Assert.assertEquals( xy,
                "\\r\\nVariant Event ID,Gene,Subject,REF,ALT,Location,Context,Effects,CADD Phred,Sources\\r\\n"
                        + "1,GENE1;GENE2,1,A,T,chr1:100-200,function,stopgain,10.0,author1\\r\\n"
                        + "2,GENE1B;GENE2B,2,G,C,chr2:1000-2000,function,synonymous SNV,10.0,author2\\r\\n"
                        + "2,GENE1B;GENE2B,2,G,C,chr2:1001-2001,function,synonymous SNV,10.0,author3\\r\\n" );

    }

}
