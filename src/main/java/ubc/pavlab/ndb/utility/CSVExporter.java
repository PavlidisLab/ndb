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
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
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
    private ArrayList<String>  headers;

    public CSVExporter( String fileName ) {
        super( CONTENT_TYPE, fileName );
        this.headers = new ArrayList<String>(Arrays.asList("Paper_ID", "Paper_key", "Variant_Event_ID", "Gene",
                "Subject", "Sample_ID", "REF", "ALT", "Location", "Context", "Effects", "HGVS", "CADD_1.0_Raw", "CADD_1.0_Phred",
                "ExAC_0.3", "Inheritance", "Validation_Status", "Validation_Method", "Sources",

                "clinvar_20150629", // Additional annovar annotations
                "CADD13_raw",
                "CADD13_phred",
                "SIFT_score",
                "SIFT_pred",
                "Polyphen2_HDIV_score",
                "Polyphen2_HDIV_pred",
                "Polyphen2_HVAR_score",
                "Polyphen2_HVAR_pred",
                "LRT_score",
                "LRT_pred",
                "MutationTaster_score",
                "MutationTaster_pred",
                "MutationAssessor_score",
                "MutationAssessor_pred",
                "FATHMM_score",
                "FATHMM_pred",
                "RadialSVM_score",
                "RadialSVM_pred",
                "LR_score",
                "LR_pred",
                "VEST3_score",
                "GERP_RS",
                "phyloP46way_placental",
                "phyloP100way_vertebrate",
                "SiPhy_29way_logOdds"));


    }

    public CSVExporter( String fileName, String header ) {
        super( CONTENT_TYPE, fileName );
    }

    // Data Loaders

    public void loadData( Collection<Event> events ) throws Exception{
        StringBuilder csvText = new StringBuilder();
        Date today = Calendar.getInstance().getTime();
        String timestampHeader = "# Downloaded from VariCarta on " + today.toString() + " . These data were produced by the Pavlidis Lab, UBC and is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License. See: https://varicarta.msl.ubc.ca/about";
        CSVFormat format = CSVFormat.RFC4180.withHeader(timestampHeader).withDelimiter( '\t' );
        CSVPrinter printer;
        try {
            printer = new CSVPrinter( csvText, format );
            printer.printRecord( this.headers ); // TODO make header an array list

            for ( Event event : events ) {
                if ( true || event.isComplex() ) { // Disabling event printing.
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

    private static void addAnnovarToCsv(List<String> csvData, Variant e){
        // Annovar
        csvData.add(e.getAnnovar().getClinvar20150629());
        csvData.add(String.valueOf(e.getAnnovar().getCaddRaw_1_3()));
        csvData.add(String.valueOf(e.getAnnovar().getCaddPhred_1_3()));
        csvData.add(String.valueOf(e.getAnnovar().getSiftScore()));
        csvData.add(e.getAnnovar().getSiftPred());
        csvData.add(String.valueOf(e.getAnnovar().getPolyphen2hdivScore()));
        csvData.add(e.getAnnovar().getPolyphen2hdivPred());
        csvData.add(String.valueOf(e.getAnnovar().getPolyphen2hvarScore()));
        csvData.add(e.getAnnovar().getPolyphen2hvarPred());
        csvData.add(String.valueOf(e.getAnnovar().getLrtScore()));
        csvData.add(e.getAnnovar().getLrtPred());
        csvData.add(String.valueOf(e.getAnnovar().getMutationTasterScore()));
        csvData.add(e.getAnnovar().getMutationTasterPred());
        csvData.add(String.valueOf(e.getAnnovar().getMutationAssessorScore()));
        csvData.add(e.getAnnovar().getMutationAssessorPred());
        csvData.add(String.valueOf(e.getAnnovar().getFathmmScore()));
        csvData.add(e.getAnnovar().getFathmmPred());
        csvData.add(String.valueOf(e.getAnnovar().getRadialSVMScore()));
        csvData.add(e.getAnnovar().getRadialSVMPred());
        csvData.add(String.valueOf(e.getAnnovar().getLrtScore()));
        csvData.add(e.getAnnovar().getLrPred());
        csvData.add(String.valueOf(e.getAnnovar().getVest3Score()));
        csvData.add(String.valueOf(e.getAnnovar().getGerpRs()));
        csvData.add(String.valueOf(e.getAnnovar().getPhyloP46wayPlacental()));
        csvData.add(String.valueOf(e.getAnnovar().getPhyloP100wayVertebrate()));
        csvData.add(String.valueOf(e.getAnnovar().getSiphy29wayLogOdds()));

    }

    private static List<String> variantToCSVLine( Variant e ) {
        /*
        Build the CSV content
         */
        List<String> csvData = Lists.newArrayList();

        // Paper_ID
        csvData.add( e.getPaper().getId().toString() );

        // Paper_Key
        csvData.add( e.getPaper().getPaperKey().toString() );

        // Variant_Event_ID
        csvData.add( e.getEventId().toString() );

        // Gene
        StringBuilder result = new StringBuilder();
        for ( Gene g : e.getGenes() ) {
            result.append( g.getSymbol() );
            result.append( ";" ); // Always append an ; after the symbol
        }
        String genes = result.length() > 0 ? result.substring( 0, result.length() - 1 ) : ""; // Remove trailing ;
        csvData.add( genes );

        csvData.add( e.getSubjectId().toString() ); // Subject
        csvData.add( e.getSampleId() ); // Sample_ID
        csvData.add( e.getRef() ); // REF
        csvData.add( e.getAlt() ); // ALT
        csvData.add( "chr" + e.getChromosome() + ":" + e.getStart().toString() + "-" + e.getStop().toString() ); // Location
        csvData.add( e.getFunc() ); // Context

        //Effects
        if (e.getCategory() != null){
            csvData.add( e.getCategory().getLabel() );
        } else {
            csvData.add( "NULL" ); // TODO: Return Null or Nothing (or .) ?
        }

        // HGVS
        result = new StringBuilder();
        for ( String aa : e.getAaChanges() ) {
            //result.append( aa.getTranscript() + ":" + aa.getContext() + ":" + ":" + aa.getHgvsC() + ":" + aa.getHgvsP() );
            result.append( aa );
            result.append( ";" );
        }
        String hgvs = result.length() > 0 ? result.substring( 0, result.length() - 1 ) : "" ;
        csvData.add( hgvs);

        // CADD_1.0_Raw
        Double caddraw = e.getAnnovar().getCaddRaw();
        csvData.add( caddraw == 0 ? "-" : caddraw.toString() );

        // CADD_1.0_Phred
        Double caddphred = e.getAnnovar().getCaddPhred();
        csvData.add( caddphred == 0 ? "-" : caddphred.toString() );

        // ExAC_0.3
        csvData.add( String.valueOf( e.getAnnovar().getExac03() ) );

        // Inheritance
        if (e.getInheritance() == null){
            csvData.add("");
        }else{
            csvData.add(e.getInheritance().getLabel());
        }

        // Validation_Status
        if (e.getValidation() == null){
            csvData.add("");
        }else{
            csvData.add(e.getValidation().getLabel());
        }
        // 	Validation_Method
        csvData.add( e.getValidationMethod() );

        //	Sources
        csvData.add( e.getPaper().getPaperKey() );

        // Add annovar
        addAnnovarToCsv(csvData, e);

        return csvData;
    }

    private static List<String> eventToCSVLine( Event e ) throws Exception {

        if (true){
            throw new Exception("This doesn't work anymore, header doesn't line up to variant processing.");
        }

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

//        result = new StringBuilder();
//        List<String> annovarData = Lists.newArrayList();
//        for ( Variant v : e.getVariants() ) {
//
//            result.append( v.getPaper().getPaperKey() );
//            result.append( ";" );
//        }

        return csvData;

    }

}
