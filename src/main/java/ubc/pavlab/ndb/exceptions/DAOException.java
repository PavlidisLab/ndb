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

package ubc.pavlab.ndb.exceptions;

/**
 * Exception to capture problems in usage of the DAO.
 * 
 * @author mjacobson
 * @version $Id$
 */
public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 4707425874504414585L;

    /**
     * Constructs a DAOException with the given detail message.
     * 
     * @param message The detail message of the DAOException.
     */
    public DAOException( String message ) {
        super( message );
    }

    /**
     * Constructs a DAOException with the given root cause.
     * 
     * @param cause The root cause of the DAOException.
     */
    public DAOException( Throwable cause ) {
        super( cause );
    }

    /**
     * Constructs a DAOException with the given detail message and root cause.
     * 
     * @param message The detail message of the DAOException.
     * @param cause The root cause of the DAOException.
     */
    public DAOException( String message, Throwable cause ) {
        super( message, cause );
    }

}