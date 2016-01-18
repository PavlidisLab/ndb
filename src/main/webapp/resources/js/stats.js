   
   function pieExtender() {
      this.cfg.highlighter = {
         show : true,
         tooltipLocation : 'n',
         useAxesFormatters : false,
         formatString : '%s: %d variants'
      };
   }

   function barExtender() {

      function tooltipContentEditor(str, seriesIndex, pointIndex, plot) {
         // display series_label, x-axis_tick, y-axis value
         return plot.options.axes.xaxis.ticks[pointIndex] + ": " + plot.data[seriesIndex][pointIndex];
      }

      this.cfg.legend = {
         show : false
      };

      this.cfg.highlighter = {
         show : true,
         tooltipLocation : 'e',
         tooltipContentEditor : tooltipContentEditor
      };

      this.cfg.seriesDefaults = {
         renderer : $.jqplot.BarRenderer,
         pointLabels : {
            show : true,
            location : 'n',
            hideZeros : true
         }
      };

      this.cfg.seriesDefaults.rendererOptions = {
         varyBarColor : true
      };
      

      this.cfg.tickDefaults = {
         syncTicks : true,
         useSeriesColor : true,
         autoscale : true,
         alignTicks : true,
         forceTickAt0 : true
      };

      //console.log( this.cfg.axes.yaxis );

      this.cfg.axes.yaxis = {
         label : 'Counts',
         //ticks: [0.1, 1, 10, 100, 1000, 10000],
         ticks: [0.1, 1, 5,10,50,100,500,1000,5000,10000], // TODO: Make general
         tickOptions : {
            tickDistribution : "power",
            formatString : "%'i"
         },
         renderer : $.jqplot.LogAxisRenderer,

      };

      //this.cfg.PointLabels = {show : false};

   }

$( document ).ready(function() {
   

$(function () {

    $('#heatmap-container').highcharts({

        chart: {
            type: 'heatmap',
            marginTop: 40,
            marginBottom: 80,
            plotBorderWidth: 1
            
        },
        
        title: {
            text: "Variant Event Overlap Between Papers"
        },

        xAxis: {
            categories: heatmap_categories
        },

        yAxis: {
            categories: heatmap_categories,
            title: null
        },

//        colorAxis: {
//           
//           stops: [
//                   [0, '#FFFFFF'],
//                   [25, '#000000']
//                  ]
//       },
       colorAxis: {
            min: 0,
            max: 100, // TODO Update this value if we ever get another major paper with > 2500 variants.
            minColor: '#FFFFFF',
            maxColor: Highcharts.getOptions().colors[0]
        },

        legend: {
            align: 'right',
            layout: 'vertical',
            margin: 0,
            verticalAlign: 'top',
            y: 25,
            symbolHeight: 280
        },
        
        plotOptions: {
           series: {
               events: {
                   click: function (event) {
                      console.log(event.point);
                      goToPaperOverlap(heatmap_paper_ids[event.point.x], heatmap_paper_ids[event.point.y]);/*
                      console.log(event);
                      console.log(heatmap_categories[event.point.x], heatmap_paper_ids[event.point.x]);
                      console.log(heatmap_categories[event.point.y], heatmap_paper_ids[event.point.y]);*/
                   }
               }
           }
       },

        tooltip: {           
            formatter: function () {
               if ( this.point.value  == 0 && 
                    !(this.series.xAxis.categories[this.point.x] == this.series.xAxis.categories[this.point.y]) 
                    ){
                  // No overlap // TODO: Do we even want to show papers with 0 reports?
                  return false; // Don't show tooltip 
               }
               else if ( this.series.xAxis.categories[this.point.x] == this.series.xAxis.categories[this.point.y] ){
                  // Diagonal
                  return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> reports <br><b>' +
                  this.point.value + '</b> variant events.';                  
               }
               else{
                  // Any non-zero, non-diagonal cell.
                  return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> reports <br><b>' +
                  this.point.value + '</b> variant events also reported in <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
                  }                  
               }                               
        },
        
        exporting : {
           // enabled: false,
           sourceWidth: 1400,
           sourceHeight: 1000
        }, 
        
        series: [{
            name: 'Variants per paper',
            cursor: 'pointer', 
            borderWidth: 1,            
            turboThreshold: 0, // Important, or else the chart will not render > 1000 elements.
            data: color_me_fancy(heatmap_data),
            //data: JSON.parse($('#heatmap-container').attr('data')), 
            dataLabels: {
                enabled: true,
                color: '#000000'
            }
        }]

    });
          
    function color_me_fancy( arr ){
       /*
        *  RULES:
        *   1) If x == y (the diagonal), then color the cell ____
        *   2) If z == 0, then change the value to ""
        *   3) 
        */
       
       var zeros = [];
       var diagonals = [];
       var others = [];

       function make_zero_color_object(x, y, val){
          var zero = { 'x' : x, 
                       'y' : y,
                       'value' : "",
                       //'color' : '#dddddd',
                       style: {
                          'font-weight': 'lighter'
                       }          
         }
          return zero;
       }       
       function make_diag_color_object(x, y, val){
          var diag = { 'x' : x, 
                       'y' : y,
                       'value' : val,
                       'color' : '#ffd699',
                       'borderColor': 'black',
                       dataLabels: {
                          style: {
                                'font-weight': 'bold'
                             }          
                       }
                       
                     }
          return diag;
       } 
       function make_unchanged_object(x, y, val){
          var o = { 'x' : x, 
                       'y' : y,
                       'value' : val 
                     }
          return o;
       }     
       
       for (var i = 0 ; i < arr.length; i++){
          current = arr[i];
          
          
          if (current[0] == current[1]){
             // Mark diagonals
             diagonals.push(make_diag_color_object(current[0], current[1], current[2] ));
          }
          else if ( current[2] == 0 ) {
             // If not a diagonal, check if == 0 and mark.
             zeros.push( make_zero_color_object(current[0], current[1], current[2] ) );
          }         
          else {
             others.push( current )
          }
       }
              
       return others.concat(zeros).concat(diagonals);
    }
    
    //console.log( JSON.parse($('#heatmap-container').attr('data') ) );
});

$(Highcharts.charts).each(function(i,chart){
   var height = chart.renderTo.clientHeight; 
   var width = chart.renderTo.clientWidth; 
   chart.setSize(width, height); 
 });
});