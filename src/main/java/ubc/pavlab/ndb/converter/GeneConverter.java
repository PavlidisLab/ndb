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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.log4j.Logger;

import ubc.pavlab.ndb.beans.services.CacheService;
import ubc.pavlab.ndb.model.Gene;

/**
 * Converter for {@link Gene}. Must pass in a species attribute or it will fail.
 * 
 * @author mjacobson
 * @version $Id$
 */
@FacesConverter("geneConverter")
public class GeneConverter implements Converter {

    private static final Logger log = Logger.getLogger( GeneConverter.class );

    @Override
    public Object getAsObject( FacesContext fc, UIComponent uic, String value ) {
        if ( value != null && value.trim().length() > 0 ) {
            try {
                //Integer species = ( Integer ) uic.getAttributes().get( "species" );
                log.info( value );
                CacheService cache = ( CacheService ) fc.getExternalContext().getApplicationMap().get( "cacheService" );
                return cache.getGeneById( Integer.valueOf( value ) );
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
            return ( ( Gene ) object ).getGeneId().toString();
        } else {
            return null;
        }
    }
}