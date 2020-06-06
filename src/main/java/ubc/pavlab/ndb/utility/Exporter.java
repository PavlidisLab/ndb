/*
 * The ndb project
 * 
 * Copyright (c) 2016 University of British Columbia
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * 
 * Interface for exported used to create StreamedContent from model data.
 * 
 * @author mjacobson
 * @version $Id$
 */
public abstract class Exporter {

    protected byte[] content;
    private final String contentType;
    private final String fileName;

    public Exporter( String contentType, String fileName ) {
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean hasData() {
        return content != null && content.length > 0;
    }

    public StreamedContent getStreamedContent() {
        InputStream stream = new ByteArrayInputStream( content );
        return new DefaultStreamedContent( stream, contentType, fileName );
    }

}
