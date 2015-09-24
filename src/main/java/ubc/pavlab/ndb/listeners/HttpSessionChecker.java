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

package ubc.pavlab.ndb.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Listener that logs when sessions are created and destroyed
 * 
 * @author mjacobson
 * @version $Id$
 */
public class HttpSessionChecker implements HttpSessionListener {

    private static final Logger log = Logger.getLogger( HttpSessionChecker.class );

    @Override
    public void sessionCreated( HttpSessionEvent event ) {
        log.info( String.format( "Session ID %s created", event.getSession().getId() ) );
    }

    @Override
    public void sessionDestroyed( HttpSessionEvent event ) {
        log.info( String.format( "Session ID %s destroyed", event.getSession().getId() ) );
    }
}