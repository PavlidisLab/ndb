$(function () {

    $('#container').highcharts({

        chart: {
            type: 'heatmap',
            marginTop: 40,
            marginBottom: 80,
            plotBorderWidth: 1
            
        },
        
        title: {
            text: $('#container').attr('title')
        },

        xAxis: {
            categories: JSON.parse( $('#container').attr('categories') )
        },

        yAxis: {
            categories: JSON.parse($('#container').attr('categories') ),
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
            max: 100, // Update this value if we ever get another major paper with > 2500 variants.
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

        tooltip: {           
            formatter: function () {
               if ( this.point.value  == 0){ 
                  return false; // Don't show tooltip 
               }
               
                return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> reports <br><b>' +
                    this.point.value + '</b> variant events also reported in <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
            }
        },

        series: [{
            name: 'Variants per paper',
            borderWidth: 1,            
            turboThreshold: 0, // Important, or else the chart will not render > 1000 elements.
            data: color_me_fancy(JSON.parse($('#container').attr('data'))),
            //data: JSON.parse($('#container').attr('data')), 
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
        *   2) If z == 0, then color light grey
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
    
    //console.log( JSON.parse($('#container').attr('data') ) );
});