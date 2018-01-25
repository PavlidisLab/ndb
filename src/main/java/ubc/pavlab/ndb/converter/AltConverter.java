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

package ubc.pavlab.ndb.converter;

import org.apache.log4j.Logger;
import ubc.pavlab.ndb.beans.VariantView;
import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.beans.services.VariantService;
import ubc.pavlab.ndb.model.Event;
import ubc.pavlab.ndb.model.Gene;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Converter for {@link Gene}. Must pass in a species attribute or it will fail.
 * 
 * @author mjacobson
 * @version $Id$
 */
@FacesConverter("altConverter")
public class AltConverter implements Converter {

    private static final Logger log = Logger.getLogger( AltConverter.class );

    private static VariantService variantService = null;

    @Override
    public Object getAsObject( FacesContext fc, UIComponent uic, String value ) {
        //TODO: This hasn't been tested, should be returning an event object instead of variant.
        // This assumes the converter is only used for non-complex events.

        if ( value != null && value.trim().length() > 0 ) {
            try {

                log.info( value );
                if ( variantService == null ) {
                    log.info( "null" );
                    variantService = ( VariantService ) fc.getExternalContext().getApplicationMap().get( "variantService" );
                }
                return  variantService.fetchById( Integer.valueOf( value ) );
            } catch ( NumberFormatException e ) {
                throw new ConverterException(
                        new FacesMessage( FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid gene." ) );
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString( FacesContext fc, UIComponent uic, Object object ) {
        if ( object != null ) {
            String alt = ( (Event) object ).getAlt();
            if (alt.length() > 6){
                String padding = "...";
                alt = alt.substring( 0, 6 ) + padding;
            }
            return  alt;
        } else {
            return null;
        }
    }
}