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

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.model.AAChange;
import ubc.pavlab.ndb.model.Event;
import ubc.pavlab.ndb.model.Gene;
import ubc.pavlab.ndb.model.Variant;
import ubc.pavlab.ndb.model.enums.Category;
import ubc.pavlab.ndb.model.enums.Inheritance;

/**
 * Exports Data as CSV
 * 
 * @author mjacobson
 * @version $Id$
 */
public class CSVExporter extends Exporter {

    private static final Logger log = Logger.getLogger( CSVExporter.class );

    private static final String CONTENT_TYPE = "text/csv";

    public CSVExporter( String fileName ) {
        super( CONTENT_TYPE, fileName );
    }

    // Data Loaders

    public void loadData( Collection<Event> events ) {
        StringBuilder csvText = new StringBuilder();
        Date today = Calendar.getInstance().getTime();
        String timestampHeader = "Downloaded from MARVdb on " + today.toString();
        CSVFormat format = CSVFormat.RFC4180.withHeader(timestampHeader).withDelimiter( '\t' );
        CSVPrinter printer;
        try {
            printer = new CSVPrinter( csvText, format );
            printer.printRecord( "Variant Event ID", "Gene", "Subject", "Sample_ID", "REF", "ALT", "Location", "Context",
                    "Effects", "HGVS", "CADD_1.0_Phred", "ExAC_0.3", "Inheritance", "Validation_Status", "Validation_Method", "Sources" );

            for ( Event event : events ) {
                if ( event.isComplex() ) {
                    for ( Variant v : event.getVariants() ) {
                        printer.printRecord( variantToCSVLine( v ) );
                    }
                } else {
                    printer.printRecord( eventToCSVLine( event ) );
                }

            }
            printer.close();
        } catch ( IOException e ) {
            log.error( "Error writing CSV", e );
        }

        content = csvText.toString().getBytes();
    }

    private static List<String> variantToCSVLine( Variant e ) {
        List<String> csvData = Lists.newArrayList();

        csvData.add( e.getEventId().toString() );

        StringBuilder result = new StringBuilder();
        for ( Gene g : e.getGenes() ) {
            result.append( g.getSymbol() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );
        csvData.add( e.getSubjectId().toString() );
        csvData.add( e.getSampleId() );
        csvData.add( e.getRef() );
        csvData.add( e.getAlt() );
        csvData.add( "chr" + e.getChromosome() + ":" + e.getStart().toString() + "-" + e.getStop().toString() );
        csvData.add( e.getFunc() );

        if (e.getCategory() != null){
            csvData.add( e.getCategory().getLabel() );
        } else {
            csvData.add( "NULL" ); // TODO: Return Null or Nothing (or .) ?
        }


        result = new StringBuilder();
        for ( String aa : e.getAaChanges() ) {
            //result.append( aa.getTranscript() + ":" + aa.getContext() + ":" + ":" + aa.getHgvsC() + ":" + aa.getHgvsP() );
            result.append( aa );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );
        Double cadd = e.getAnnovar().getCaddPhred();
        csvData.add( cadd == 0 ? "-" : cadd.toString() );
        csvData.add( String.valueOf( e.getAnnovar().getExac03() ) );

        if (e.getInheritance() == null){
            csvData.add("");
        }else{
            csvData.add(e.getInheritance().getLabel());
        }

        if (e.getValidation() == null){
            csvData.add("");
        }else{
            csvData.add(e.getValidation().getLabel());
        }
        csvData.add( e.getValidationMethod() );
        csvData.add( e.getPaper().getPaperKey() );

        return csvData;
    }

    private static List<String> eventToCSVLine( Event e ) {
        List<String> csvData = Lists.newArrayList();

        csvData.add( e.getId().toString() );

        StringBuilder result = new StringBuilder();
        for ( Gene g : e.getGenes() ) {
            result.append( g.getSymbol() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        csvData.add( e.getSubjectId().toString() );
        csvData.add( e.getSampleId() );

        csvData.add( e.getRef() );
        csvData.add( e.getAlt() );
        csvData.add( "chr" + e.getChromosome() + ":" + e.getStart().toString() + "-" + e.getStop().toString() );

        result = new StringBuilder();
        for ( String f : e.getFuncs() ) {
            result.append( f );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        result = new StringBuilder();
        for ( Category c : e.getCategories() ) {
            result.append( c.getLabel() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );


        result = new StringBuilder();
        for ( AAChange aa : e.getAAChanges() ) {
            result.append( aa.getTranscript() + ":" + aa.getContext() + ":" + ":" + aa.getHgvsC() + ":" + aa.getHgvsP() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        csvData.add( e.getCaddPhred() == 0 ? "-" : e.getCaddPhred().toString() );
        csvData.add( String.valueOf( e.getExacFreq() ) );


        // Add inheritance
        //  csvData.add( e.getInheritanceText() );
        result = new StringBuilder();
        for ( Variant v : e.getVariants() ) {
            if (v.getInheritance() != null) {
                result.append( v.getInheritance().getLabel() );
                result.append( ";" );
            }
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        // Append Validation
        result = new StringBuilder();
        for ( Variant v : e.getVariants() ) {
            if ( v.getValidation() != null ) {
                result.append( v.getValidation().getLabel() );
                result.append( ";" );
            }
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        result = new StringBuilder();
        for ( Variant v : e.getVariants() ) {
            result.append( v.getValidationMethod() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        result = new StringBuilder();
        for ( Variant v : e.getVariants() ) {
            result.append( v.getPaper().getPaperKey() );
            result.append( ";" );
        }
        csvData.add( result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" );

        return csvData;

    }

}
