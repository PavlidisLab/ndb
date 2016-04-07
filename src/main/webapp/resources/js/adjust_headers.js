/*
 * Fix PF datatables header size when lazyloading.
 * */

function fix_header() {
  COUNT=0;
  //$('.ui-datatable-scrollable-body ').children('tr').children('td').each(function() {
  ($('.ui-datatable-scrollable-body tr:first-child td')).each(function() {
      // Cell count

      if  ( typeof($(this).attr('class')) == "undefined" ){
          // If it's not a datatable class, skip it.
        return true;
      }

      tdClasses = $(this).attr('class').split(/\s+/);
      if ( tdClasses.indexOf('centeredColumn') === -1 ) {
        // Not a datatable
        return true;
      } else if (tdClasses.indexOf("terminal") > -1 ){
        // Cell is a terminal cell
        return false; // Exit once we meet the terminal class.
      }

      // Get current cell width
      tdWidth = $(this).width() + 1;

      // Adjust th width
      $($('th')[COUNT]).css("width", tdWidth) ;
      COUNT++;
  });

}


fix_header_wrapper = function() {
   d = PF("variantTableWdg");
   oldF = d.loadLiveRows;
   console.log("oldF") 
   console.log(oldF)
   //newF = function(args) {oldF.apply(this, args);fix_header();};
   newF = function() {
      if (this.liveScrollActive) {
         return
      }
      this.liveScrollActive = true;
      var b = this
      , a = {
             source: this.id,
             process: this.id,
             update: this.id,
             formId: this.cfg.formId,
             params: [{
                name: this.id + "_scrolling",
                value: true
             }, {
                name: this.id + "_skipChildren",
                value: true
             }, {
                name: this.id + "_scrollOffset",
                value: this.scrollOffset
             }, {
                name: this.id + "_encodeFeature",
                value: true
             }],
             onsuccess: function(e, c, d) {
                PrimeFaces.ajax.Response.handle(e, c, d, {
                   widget: b,
                   handle: function(f) {
                      this.updateData(f, false);
                      this.scrollOffset += this.cfg.scrollStep;
                      if (this.scrollOffset === this.cfg.scrollLimit) {
                         this.shouldLiveScroll = false
                      }
                      this.liveScrollActive = false
                   }
                });
                fix_header();
                return true
             }
      };
     PrimeFaces.ajax.Request.handle(a)
 }
   d.loadLiveRows = newF;
};

