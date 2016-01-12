package ubc.pavlab.ndb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.StatsService;
import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Paper;
import ubc.pavlab.ndb.utility.Correlation;
import ubc.pavlab.ndb.utility.HeatmapModel;
import ubc.pavlab.ndb.utility.Tuples.Tuple2;

@ManagedBean
@ViewScoped
public class StatsView implements Serializable {

    /**
     * @author mbelmadani
     * @version $Id$
     */
    private static final long serialVersionUID = 7322631871107434336L;

    private static final Logger log = Logger.getLogger( StatsView.class );

    private PieChartModel pieModel;
    private PieChartModel compactPie;

    private List<Tuple2<String, Integer>> variantCategories;
    private List<Tuple2<String, Integer>> variantFunc;

    private BarChartModel variantCategoriesBarModel;
    private BarChartModel variantFuncBarModel;

    private HeatmapModel heatmapModel;

    private List<Correlation> correlations;

    @ManagedProperty("#{cacheService}")
    private CacheService cacheService;

    @ManagedProperty("#{statsService}")
    private StatsService statsService;

    @ManagedProperty("#{variantService}")
    private VariantService variantService;

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
        Map<String, String> requestParams = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap();

        bakePie();
        makeCategoryCharts();
        makeHeatmap();

    }

    private void makeHeatmap() {
        this.heatmapModel = new HeatmapModel();
        this.correlations = new ArrayList<Correlation>();

        List<Integer> paper_ids = new ArrayList<Integer>();
        List<String> paper_authors = new ArrayList<String>();

        // List all paper ids
        for ( Paper p : cacheService.listPapers() ) {
            paper_ids.add( p.getId() );
            paper_authors.add( p.getAuthor() );
        }

        // For each paper, compute event overlap with other papers
        boolean classic = false;
        if ( classic ) {
            // This method is too slow and should only be used in development to compare with faster methods.
            for ( int i = 0; i < paper_ids.size() - 1; i++ ) {
                for ( int j = i; j < paper_ids.size(); j++ ) {
                    int p1 = paper_ids.get( i );
                    int p2 = paper_ids.get( j );
                    int count = statsService.eventOverlapByPapers( p1, p2 );

                    this.correlations.add( new Correlation( paper_authors.get( i ), paper_authors.get( j ), count ) );
                    // System.out.printf( "COUNTS: %d x %d %d \n", p1, p2, count );
                }
            }
        }

        // Compute overlaps for all papers (much faster than "classic")
        boolean fancy = true;
        if ( fancy ) {
            for ( int i = 0; i < paper_ids.size(); i++ ) {
                int p1 = paper_ids.get( i );
                List<Integer> counts = statsService.eventOverlapByPaper( p1 );

                for ( int j = 0; j < paper_ids.size(); j++ ) {
                    int p2 = paper_ids.get( j );
                    int p_count = Collections.frequency( counts, p2 );

                    Correlation corr = new Correlation( paper_authors.get( i ), paper_authors.get( j ), p_count );
                    this.correlations.add( corr );
                }
            }
        }

        this.heatmapModel.insertPoints( this.correlations );
    }

    private void makeCategoryCharts() {
        this.variantCategories = statsService.getVariantCategoryOccurences();
        this.variantFunc = statsService.getVariantFuncOccurences();

        variantCategoriesBarModel = initBarModel( this.variantCategories );
        variantFuncBarModel = initBarModel( this.variantFunc );

        // Stylize categories
        variantCategoriesBarModel.setAnimate( true );

        variantCategoriesBarModel.setTitle( "Total variants by categories" );
        variantCategoriesBarModel.setExtender( "barExtender" );

        Axis xAxis = variantCategoriesBarModel.getAxis( AxisType.X );
        xAxis.setLabel( "Category" );
        xAxis.setTickAngle( -45 );
        xAxis.setTickFormat( "%s" );

        Axis yAxis = variantCategoriesBarModel.getAxis( AxisType.Y );
        yAxis.setLabel( "Variants" );
        yAxis.setTickFormat( "%d" );
        yAxis.setTickCount( 11 );
        yAxis.setMin( 0 );
        yAxis.setMax( 4500 ); // TODO: Do this programmatically

        // Stylize func
        variantFuncBarModel.setAnimate( true );

        variantFuncBarModel.setTitle( "Total variants by function" );
        variantFuncBarModel.setExtender( "barExtender" );

        xAxis = variantFuncBarModel.getAxis( AxisType.X );
        xAxis.setLabel( "Function" );
        xAxis.setTickAngle( -45 );
        xAxis.setTickFormat( "%s" );

        yAxis = variantFuncBarModel.getAxis( AxisType.Y );
        yAxis.setLabel( "Variants" );
        yAxis.setTickFormat( "%d" );
        yAxis.setTickCount( 11 );
        yAxis.setMin( 0 );
        yAxis.setMax( 5500 ); // TODO: Do this programmatically

    }

    private BarChartModel initBarModel( List<Tuple2<String, Integer>> lst ) {
        BarChartModel model = new BarChartModel();

        ChartSeries counts = new ChartSeries();
        // counts.setLabel( "counts" );
        for ( Tuple2<String, Integer> t : lst ) {
            if ( t.getT1() != null ) {
                counts.set( t.getT1(), t.getT2() );
                log.info( t.getT1() );
            }
        }

        model.addSeries( counts );

        return model;
    }

    private void bakePie() {
        /*
         * Create model for pie chart
         */

        // TODO: Highlight segment should show author
        // TODO: Make colors somewhat unique, or more unique.

        pieModel = new PieChartModel();

        for ( Paper p : cacheService.listPapers() ) {
            int count = statsService.getVariantCntByPaperId( p.getId() );
            String citation = p.getAuthor(); // TODO: Use getCitation instead of getAuthor once it has been added to the
                                             // database model

            pieModel.set( citation, count );
            // if ( pieModel.getData().size() > 4 ) break;

        }

        pieModel.setTitle( "Variant counts per paper" );
        pieModel.setShowDataLabels( true );
        pieModel.setMouseoverHighlight( true );
        pieModel.setLegendPosition( "e" );

        double MAX_PAPER_PER_COLUMN = 10.0;
        int nCols = ( int ) Math.ceil( pieModel.getData().size() / MAX_PAPER_PER_COLUMN );
        pieModel.setLegendCols( nCols );
        pieModel.setExtender( "pieExtender" );

        // pieModel.setDiameter( 150 );

        // Create small pie
        makeCompactPie();

    }

    public void makeCompactPie() {

        final int TOP = 5; // For the top 10 items
        final int topColor = 0x3399ff;
        final int bottomColor = 0xe5f2ff;

        Map<String, Number> data = this.pieModel.getData();
        Map<String, Number> compact = new HashMap<String, Number>();

        List<Integer> ints = new ArrayList<Integer>();
        for ( Number n : data.values() ) {
            ints.add( n.intValue() );
        }

        Collections.sort( ints ); // Sorted ASCENDING
        List<Integer> top = ints.subList( ints.size() - TOP, ints.size() );

        int bottom = 0;
        for ( int i : ints.subList( 0, ints.size() - TOP ) ) {
            bottom += i;
        }

        int min = top.get( 0 );
        for ( String k : data.keySet() ) {
            if ( data.get( k ).intValue() >= min ) {
                compact.put( k, data.get( k ) );
            }
        }
        compact.put( "Others", bottom );

        compactPie = new PieChartModel( compact );
        compactPie.setTitle( "Variant counts for top " + TOP + " papers in variant event count" );
        compactPie.setShowDataLabels( true );
        compactPie.setMouseoverHighlight( true );
        compactPie.setLegendPosition( "e" );

        compactPie.setSeriesColors( colorScale( topColor, bottomColor, TOP ) );

        double MAX_PAPER_PER_COLUMN = 10.0;
        int nCols = ( int ) Math.ceil( compactPie.getData().size() / MAX_PAPER_PER_COLUMN );
        compactPie.setLegendCols( nCols );
        compactPie.setExtender( "pieExtender" );

    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public String colorScale( int start, int end, int step ) {
        /*
         * TODO: Implement a pretty scaling function for colors. How do we graduate and RGB hexadecimal value
         * programmatically?
         */
        String scale = "";

        int diff = ( end - start ) / step;
        for ( int i = 0; i < step - 1; i++ ) {
            scale += Integer.toHexString( start + 0x01000 * i ) + ",";
        }
        scale += Integer.toHexString( end );

        return scale;
    }

    public PieChartModel getCompactPieModel() {
        return compactPie;
    }

    public HeatmapModel getHeatmapModel() {
        return heatmapModel;
    }

    public List<Correlation> getCorrelations() {
        return correlations;
    }

    public void setCacheService( CacheService cacheService ) {
        this.cacheService = cacheService;
    }

    public void setStatsService( StatsService statsService ) {
        this.statsService = statsService;
    }

    public void setVariantService( VariantService variantService ) {
        this.variantService = variantService;
    }

    public List<Tuple2<String, Integer>> getVariantCategories() {
        return variantCategories;
    }

    public void setVariantCategories( List<Tuple2<String, Integer>> variantCategories ) {
        this.variantCategories = variantCategories;
    }

    public BarChartModel getVariantCategoriesBarModel() {
        return variantCategoriesBarModel;
    }

    public void setVariantCategoriesBarModel( BarChartModel variantCategoriesBarModel ) {
        this.variantCategoriesBarModel = variantCategoriesBarModel;
    }

    public BarChartModel getVariantFuncBarModel() {
        return variantFuncBarModel;
    }

    public void setVariantFuncBarModel( BarChartModel variantFuncBarModel ) {
        this.variantFuncBarModel = variantFuncBarModel;
    }

}