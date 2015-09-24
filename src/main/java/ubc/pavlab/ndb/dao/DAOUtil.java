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

package ubc.pavlab.ndb.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for DAO's. This class contains commonly used DAO logic which is been refactored in single static
 * methods. As far it contains a PreparedStatement values setter and several quiet close methods.
 */
public final class DAOUtil {

    // Constructors -------------------------------------------------------------------------------

    private DAOUtil() {
        // Utility class, hide constructor.
    }

    // Actions ------------------------------------------------------------------------------------

    /**
     * Will replace '(%s) with (?,?,?,?,?,...) depending on size to accommodate lists of parameters in an SQL query
     * 
     * @param length length of list of placeholders to be prepared
     */
    public static String preparePlaceHolders( int length ) {
        StringBuilder builder = new StringBuilder( length * 2 - 1 );
        for ( int i = 0; i < length; i++ ) {
            if ( i > 0 ) builder.append( ',' );
            builder.append( '?' );
        }
        return builder.toString();
    }

    /**
     * Returns a PreparedStatement of the given connection, set with the given SQL query and the given parameter values.
     * 
     * @param connection The Connection to create the PreparedStatement from.
     * @param sql The SQL query to construct the PreparedStatement with.
     * @param returnGeneratedKeys Set whether to return generated keys or not.
     * @param values The parameter values to be set in the created PreparedStatement.
     * @throws SQLException If something fails during creating the PreparedStatement.
     */
    public static PreparedStatement prepareStatement( Connection connection, String sql, boolean returnGeneratedKeys,
            Object... values ) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement( sql,
                returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );
        setValues( preparedStatement, values );
        return preparedStatement;
    }

    /**
     * Set the given parameter values in the given PreparedStatement.
     * 
     * @param connection The PreparedStatement to set the given parameter values in.
     * @param values The parameter values to be set in the created PreparedStatement.
     * @throws SQLException If something fails during setting the PreparedStatement values.
     */
    public static void setValues( PreparedStatement preparedStatement, Object... values ) throws SQLException {
        for ( int i = 0; i < values.length; i++ ) {
            preparedStatement.setObject( i + 1, values[i] );
        }
    }

    /**
     * Converts the given java.util.Date to java.sql.Date.
     * 
     * @param date The java.util.Date to be converted to java.sql.Date.
     * @return The converted java.sql.Date.
     */
    public static Date toSqlDate( java.util.Date date ) {
        return ( date != null ) ? new Date( date.getTime() ) : null;
    }

    /**
     * Quietly close the Connection. Any errors will be printed to the stderr.
     * 
     * @param connection The Connection to be closed quietly.
     */
    public static void close( Connection connection ) {
        if ( connection != null ) {
            try {
                connection.close();
            } catch ( SQLException e ) {
                System.err.println( "Closing Connection failed: " + e.getMessage() );
                e.printStackTrace();
            }
        }
    }

    /**
     * Quietly close the Statement. Any errors will be printed to the stderr.
     * 
     * @param statement The Statement to be closed quietly.
     */
    public static void close( Statement statement ) {
        if ( statement != null ) {
            try {
                statement.close();
            } catch ( SQLException e ) {
                System.err.println( "Closing Statement failed: " + e.getMessage() );
                e.printStackTrace();
            }
        }
    }

    /**
     * Quietly close the ResultSet. Any errors will be printed to the stderr.
     * 
     * @param resultSet The ResultSet to be closed quietly.
     */
    public static void close( ResultSet resultSet ) {
        if ( resultSet != null ) {
            try {
                resultSet.close();
            } catch ( SQLException e ) {
                System.err.println( "Closing ResultSet failed: " + e.getMessage() );
                e.printStackTrace();
            }
        }
    }

    /**
     * Quietly close the Connection and Statement. Any errors will be printed to the stderr.
     * 
     * @param connection The Connection to be closed quietly.
     * @param statement The Statement to be closed quietly.
     */
    public static void close( Connection connection, Statement statement ) {
        close( statement );
        close( connection );
    }

    /**
     * Quietly close the Connection, Statement and ResultSet. Any errors will be printed to the stderr.
     * 
     * @param connection The Connection to be closed quietly.
     * @param statement The Statement to be closed quietly.
     * @param resultSet The ResultSet to be closed quietly.
     */
    public static void close( Connection connection, Statement statement, ResultSet resultSet ) {
        close( resultSet );
        close( statement );
        close( connection );
    }

}
