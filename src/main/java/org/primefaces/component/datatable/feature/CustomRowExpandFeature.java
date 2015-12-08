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

package org.primefaces.component.datatable.feature;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.rowexpansion.RowExpansion;
import org.primefaces.component.subtable.SubTable;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class CustomRowExpandFeature extends RowExpandFeature {

    private static final Logger log = Logger.getLogger( CustomRowExpandFeature.class );

    @Override
    public void encodeExpansion( FacesContext context, DataTableRenderer renderer, DataTable table, int rowIndex,
            boolean hidden ) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        RowExpansion rowExpansion = table.getRowExpansion();

        String styleClass = DataTable.EXPANDED_ROW_CONTENT_CLASS + " ui-widget-content";
        if ( rowExpansion.getStyleClass() != null ) {
            styleClass = styleClass + " " + rowExpansion.getStyleClass();
        }

        table.setRowIndex( rowIndex );

        if ( rowIndexVar != null ) {
            context.getExternalContext().getRequestMap().put( rowIndexVar, rowIndex );
        }

        writer.startElement( "tr", null );
        if ( hidden ) {
            writer.writeAttribute( "style", "display:none", null );
        }
        writer.writeAttribute( "class", styleClass, null );

        writer.startElement( "td", null );
        writer.writeAttribute( "colspan", getColumnsCountWithSpan( table ), null );

        table.getRowExpansion().encodeAll( context );

        writer.endElement( "td" );

        writer.endElement( "tr" );
    }

    private int getColumnsCountWithSpan( DataTable table ) {
        int columnsCountWithSpan = 0;

        for ( UIComponent kid : table.getChildren() ) {
            if ( kid.isRendered() ) {
                if ( kid instanceof Columns ) {
                    int dynamicColumnsCount = ( ( Columns ) kid ).getRowCount();
                    if ( dynamicColumnsCount > 0 ) {
                        columnsCountWithSpan += dynamicColumnsCount;
                    }
                } else if ( kid instanceof Column ) {
                    columnsCountWithSpan += ( ( Column ) kid ).getColspan();
                } else if ( kid instanceof SubTable ) {
                    SubTable subTable = ( SubTable ) kid;
                    for ( UIComponent subTableKid : subTable.getChildren() ) {
                        if ( subTableKid.isRendered() && subTableKid instanceof Column ) {
                            columnsCountWithSpan += ( ( Column ) subTableKid ).getColspan();
                        }
                    }
                }
            }
        }

        return columnsCountWithSpan;
    }
}
