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

package org.primefaces.component.datatable;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.feature.CustomRowExpandFeature;
import org.primefaces.component.datatable.feature.DataTableFeatureKey;
import org.primefaces.component.datatable.feature.RowExpandFeature;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class CustomDataTableRenderer extends DataTableRenderer {

    private static final Logger log = Logger.getLogger( CustomDataTableRenderer.class );
    /*
     * private static CustomRowExpandFeature CUSTOM_ROW_EXPANDER = new CustomRowExpandFeature();
     */

    public CustomDataTableRenderer() {
        DataTable.FEATURES.put( DataTableFeatureKey.ROW_EXPAND, new CustomRowExpandFeature() );
        log.info( "CustomDataTableRenderer ROWEXPAND MonkeyPatch" );
    }

    @Override
    public boolean encodeRow( FacesContext context, DataTable table, String clientId, int rowIndex )
            throws IOException {
        return this.encodeRow( context, table, clientId, rowIndex, 0, table.getColumnsCount() );
    }

    @Override
    public boolean encodeRow( FacesContext context, DataTable table, String clientId, int rowIndex, int columnStart,
            int columnEnd ) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean selectionEnabled = table.isSelectionEnabled();
        Object rowKey = null;
        List<UIColumn> columns = table.getColumns();

        if ( selectionEnabled ) {
            //try rowKey attribute
            rowKey = table.getRowKey();

            //ask selectable datamodel
            if ( rowKey == null )
                rowKey = table.getRowKeyFromModel( table.getRowData() );
        }

        //Preselection
        boolean selected = table.getSelectedRowKeys().contains( rowKey );

        String userRowStyleClass = table.getRowStyleClass();
        String rowStyleClass = rowIndex % 2 == 0 ? DataTable.ROW_CLASS + " " + DataTable.EVEN_ROW_CLASS
                : DataTable.ROW_CLASS + " " + DataTable.ODD_ROW_CLASS;
        if ( selectionEnabled && !table.isDisabledSelection() )
            rowStyleClass = rowStyleClass + " " + DataTable.SELECTABLE_ROW_CLASS;

        if ( selected )
            rowStyleClass = rowStyleClass + " ui-state-highlight";

        if ( table.isEditingRow() )
            rowStyleClass = rowStyleClass + " " + DataTable.EDITING_ROW_CLASS;

        if ( userRowStyleClass != null )
            rowStyleClass = rowStyleClass + " " + userRowStyleClass;

        writer.startElement( "tr", null );
        writer.writeAttribute( "data-ri", rowIndex, null );
        if ( rowKey != null ) {
            writer.writeAttribute( "data-rk", rowKey, null );
        }
        writer.writeAttribute( "class", rowStyleClass, null );
        writer.writeAttribute( "role", "row", null );
        if ( selectionEnabled ) {
            writer.writeAttribute( "aria-selected", String.valueOf( selected ), null );
        }

        for ( int i = columnStart; i < columnEnd; i++ ) {
            UIColumn column = columns.get( i );

            if ( column instanceof Column ) {
                encodeCell( context, table, column, clientId, selected );
            } else if ( column instanceof DynamicColumn ) {
                DynamicColumn dynamicColumn = ( DynamicColumn ) column;
                dynamicColumn.applyModel();

                encodeCell( context, table, dynamicColumn, null, false );
            }
        }

        writer.endElement( "tr" );

        if ( table.isExpandedRow() ) {
            ( ( RowExpandFeature ) table.getFeature( DataTableFeatureKey.ROW_EXPAND ) ).
                    encodeExpansion( context, this, table, rowIndex);
        }

        return true;
    }

}
