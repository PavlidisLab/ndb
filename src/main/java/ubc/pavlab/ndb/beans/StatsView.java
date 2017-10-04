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
package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.utility.HeatmapModel;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

/**
 * @author mbelmadani
 * @author mjacobson
 * @version $Id$
 */

@ManagedBean
@ApplicationScoped
public class StatsView implements Serializable {

    private static final long serialVersionUID = 7322631871107434336L;

    private static final Logger log = Logger.getLogger( StatsView.class );

    private PieChartModel pieModel;
    private PieChartModel compactPie;

    private BarChartModel variantCategoriesBarModel;
    private BarChartModel variantFuncBarModel;

    private HeatmapModel<Paper> heatmapModel;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    // private List<Tuple2<String, Integer>> variantCntByCategory;

    public StatsView() {
        log.info( "create StatsView" );
    }

    @PostConstruct
    public void init() {
        if ( FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest() ) {
            return; // Skip ajax requests.
        }

        log.info( "init StatsView" );

        pieModel = bakePie();

        // Create small pie
        compactPie = makeCompactPie( pieModel );

        // Make bar charts

        variantCategoriesBarModel = makeBarChart( statsService.getEventCntByCategory(),
                "Total Variant Events by Category", "Category", "Variants" );

        Axis xAxis = variantCategoriesBarModel.getAxis( AxisType.X );
        xAxis.setTickAngle( -25 );

        variantFuncBarModel = makeBarChart( statsService.getEventCntByContext(), "Total Variant Events by Function",
                "Function", "Variants" );

        // makeCategoryCharts();
        heatmapModel = makeHeatmap();

    }

    private HeatmapModel<Paper> makeHeatmap() {
        List<Paper> paperWithVariants = statsService.getPapersWithVariants();
        HeatmapModel<Paper> heatmapModel = new HeatmapModel<>( paperWithVariants );

        int x = 0;
        for ( Paper paper : paperWithVariants ) {
            int y = 0;
            Map<Integer, Integer> counts = statsService.overlappingEventsBetweenPapers( paper.getId() );
            for ( Paper p2 : paperWithVariants ) {
                Integer cnt = counts.get( p2.getId() );
                cnt = cnt == null ? 0 : cnt;
                heatmapModel.add( x, y, cnt );
                y++;
            }
            x++;

        }

        return heatmapModel;
    }

    private <T extends Number> BarChartModel makeBarChart( List<Tuple2<String, T>> data, String title, String xLabel,
            String yLabel ) {
        BarChartModel barModel = initBarModel( data );

        // Stylize categories
        barModel.setAnimate( true );

        barModel.setTitle( title );
        barModel.setExtender( "barExtender" );

        Axis xAxis = barModel.getAxis( AxisType.X );
        xAxis.setLabel( xLabel );
        xAxis.setTickAngle( -45 );
        /* xAxis.setTickFormat( "%s" ); */

        Axis yAxis = barModel.getAxis( AxisType.Y );
        yAxis.setLabel( yLabel );
        yAxis.setMin( 1 );
        /*
         * yAxis.setTickFormat( "%d" ); yAxis.setTickCount( 11 ); yAxis.setMin( 0 ); yAxis.setMax( 4500 ); // TODO: Do
         * this programmatically
         */
        return barModel;
    }

    private <T extends Number> BarChartModel initBarModel( List<Tuple2<String, T>> lst ) {
        BarChartModel model = new BarChartModel();

        ChartSeries counts = new ChartSeries();
        // counts.setLabel( "counts" );
        for ( Tuple2<String, T> t : lst ) {
            if ( t.getT1() != null ) {
                counts.set( t.getT1(), t.getT2() );
            }
        }

        model.addSeries( counts );

        return model;
    }

    private PieChartModel bakePie() {
        /*
         * Create model for pie chart
         */

        // TODO: Highlight segment should show author
        // TODO: Make colors somewhat unique, or more unique.

        PieChartModel pieModel = new PieChartModel();

        for ( Paper p : cacheService.listPapers() ) {
            int count = statsService.getEventCntByPaperId( p.getId() );
            String citation = p.getPaperKey();
            // pieModel.set( "<a href='google.com'>" + citation + "</a>", count );
            pieModel.set( citation, count );
            // if ( pieModel.getData().size() > 4 ) break;

        }

        pieModel.setTitle( "Variant Events Counts by Paper" );
        pieModel.setShowDataLabels( true );
        pieModel.setMouseoverHighlight( true );
        pieModel.setLegendPosition( "e" );

        double MAX_PAPER_PER_COLUMN = 10.0;
        int nCols = ( int ) Math.ceil( pieModel.getData().size() / MAX_PAPER_PER_COLUMN );
        pieModel.setLegendCols( nCols );
        pieModel.setExtender( "pieExtender" );

        // pieModel.setDiameter( 150 );

        return pieModel;

    }

    public PieChartModel makeCompactPie( PieChartModel pieModel ) {

        final int TOP = 5; // For the top 10 items
        // final int topColor = 0x3399ff;
        // final int bottomColor = 0xe5f2ff;

        Map<String, Number> data = pieModel.getData();
        Map<String, Number> compact = new HashMap<String, Number>();

        List<Integer> ints = new ArrayList<Integer>();
        for ( Number n : data.values() ) {
            ints.add( n.intValue() );
        }

        Collections.sort( ints ); // Sorted ASCENDING
        
        List<Integer> top = null;
        int bottom = 0;

        if ( ints.size() <= TOP){
        	top = ints;
        } else {
        	top = ints.subList( ints.size() - TOP, ints.size() );

            for ( int i : ints.subList( 0, ints.size() - TOP ) ) {
                bottom += i;
            }
        }
        


        int min = top.get( 0 );
        for ( String k : data.keySet() ) {
            if ( data.get( k ).intValue() >= min ) {
                compact.put( k, data.get( k ) );
            }
        }
        compact.put( "Others", bottom );

        PieChartModel compactPie = new PieChartModel( compact );
        compactPie.setTitle( "Top " + TOP + " Variant Event Counts by Paper" );
        compactPie.setShowDataLabels( true );
        compactPie.setMouseoverHighlight( true );
        compactPie.setLegendPosition( "e" );

        // compactPie.setSeriesColors( colorScale( topColor, bottomColor, TOP ) );

        double MAX_PAPER_PER_COLUMN = 10.0;
        int nCols = ( int ) Math.ceil( compactPie.getData().size() / MAX_PAPER_PER_COLUMN );
        compactPie.setLegendCols( nCols );
        compactPie.setExtender( "pieExtender" );

        return compactPie;

    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    /*
     * private String colorScale( int start, int end, int step ) {
     * 
     * TODO: Implement a pretty scaling function for colors. How do we graduate and RGB hexadecimal value
     * programmatically?
     * 
     * String scale = "";
     * 
     * int diff = ( end - start ) / step; for ( int i = 0; i < step - 1; i++ ) { scale += Integer.toHexString( start +
     * 0x01000 * i ) + ","; } scale += Integer.toHexString( end );
     * 
     * return scale; }
     */

    public PieChartModel getCompactPieModel() {
        return compactPie;
    }

    public HeatmapModel<Paper> getHeatmapModel() {
        return heatmapModel;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
    }

    public BarChartModel getVariantCategoriesBarModel() {
        return variantCategoriesBarModel;
    }

    public BarChartModel getVariantFuncBarModel() {
        return variantFuncBarModel;
    }

}