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

package ubc.pavlab.ndb.utility;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;

import ubc.pavlab.ndb.exceptions.ConfigurationException;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class PropertiesFileTest {

    private static final String FILE_NAME = "ndb.properties";
    private static final String GIVEN_FOLDER = "tomcat";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File createTempFile( String fileName, String folder, List<String> contents )
            throws IOException {
        File tempFile = null;
        if ( !StringUtils.isBlank( folder ) ) {
            tempFile = tempFolder.newFolder( folder )
                    .toPath()
                    .resolve( fileName )
                    .toFile();
        } else {
            tempFile = tempFolder.newFile( fileName );
        }

        BufferedWriter out = new BufferedWriter( new FileWriter( tempFile ) );
        for ( String line : contents ) {
            out.write( line + ( line.endsWith( "\n" ) ? "" : "\n" ) );
        }
        out.close();
        return tempFile;
    }

    @Test
    public void testLoadBothFiles() throws IOException {
        File propertiesInBaseDirectory = createTempFile( FILE_NAME, null,
                Lists.newArrayList( "first.name = Arthur",
                        "last.name = Dent",
                        "favorite.object = Towel" ) );
        File propertiesInGivenDirectory = createTempFile( FILE_NAME, GIVEN_FOLDER,
                Lists.newArrayList( "first.name = Harvey",
                        "last.name = Dent",
                        "favorite.object = Coin" ) );

        assertThat( propertiesInBaseDirectory.exists(), is( true ) );
        assertThat( propertiesInGivenDirectory.exists(), is( true ) );

        PropertiesFile properties = new PropertiesFile();
        properties.load( FILE_NAME, propertiesInGivenDirectory.getParent(),
                tempFolder.getRoot().getAbsolutePath() );

        assertThat( properties.getProperty( "first.name" ), is( "Harvey" ) );
        assertThat( properties.getProperty( "last.name" ), is( "Dent" ) );
        assertThat( properties.getProperty( "favorite.object" ), is( "Coin" ) );
    }

    @Test
    public void testLoadGivenFileOnly() throws IOException {
        File propertiesInBaseDirectory = new File( tempFolder.getRoot(), FILE_NAME );
        File propertiesInGivenDirectory = createTempFile( FILE_NAME, GIVEN_FOLDER,
                Lists.newArrayList( "first.name = Harvey",
                        "last.name = Dent",
                        "favorite.object = Coin" ) );

        assertThat( propertiesInBaseDirectory.exists(), is( false ) );
        assertThat( propertiesInGivenDirectory.exists(), is( true ) );

        PropertiesFile properties = new PropertiesFile();
        properties.load( FILE_NAME, propertiesInGivenDirectory.getParent(),
                tempFolder.getRoot().getAbsolutePath() );
        assertThat( properties.getProperty( "first.name" ), is( "Harvey" ) );
        assertThat( properties.getProperty( "last.name" ), is( "Dent" ) );
        assertThat( properties.getProperty( "favorite.object" ), is( "Coin" ) );
    }

    @Test
    public void testLoadBaseFileOnly() throws IOException {
        File propertiesInBaseDirectory = createTempFile( FILE_NAME, null,
                Lists.newArrayList( "first.name = Arthur",
                        "last.name = Dent",
                        "favorite.object = Towel" ) );
        File givenFolder = tempFolder.newFolder( GIVEN_FOLDER );
        File propertiesInGivenDirectory = new File( givenFolder, FILE_NAME );

        assertThat( propertiesInBaseDirectory.exists(), is( true ) );
        assertThat( propertiesInGivenDirectory.exists(), is( false ) );

        PropertiesFile properties = new PropertiesFile();
        properties.load( FILE_NAME, propertiesInGivenDirectory.getParent(),
                tempFolder.getRoot().getAbsolutePath() );
        assertThat( properties.getProperty( "first.name" ), is( "Arthur" ) );
        assertThat( properties.getProperty( "last.name" ), is( "Dent" ) );
        assertThat( properties.getProperty( "favorite.object" ), is( "Towel" ) );
    }

    @Test
    public void testLoadNoFiles() throws IOException {
        File propertiesInBaseDirectory = new File( tempFolder.getRoot(), FILE_NAME );

        File givenFolder = tempFolder.newFolder( GIVEN_FOLDER );
        File propertiesInGivenDirectory = new File( givenFolder, FILE_NAME );

        assertThat( propertiesInBaseDirectory.exists(), is( false ) );
        assertThat( propertiesInGivenDirectory.exists(), is( false ) );

        thrown.expect( ConfigurationException.class );
        thrown.expectMessage( "Properties file '" + FILE_NAME + "' is missing." );

        PropertiesFile properties = new PropertiesFile();
        properties.load( FILE_NAME, propertiesInGivenDirectory.getParent(),
                tempFolder.getRoot().getAbsolutePath() );

    }

}
