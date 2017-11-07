
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

package ubc.pavlab.ndb.model.enums;

/**
 * TODO Document Me
 *      Categories for different types of validation.
 *      -y - Yes.
 *      -n - No.
 *      -u is Unknown.
 * @author mbelmadani
 * @version $Id$
 */

public enum Validation {
    y("Yes"),
    n("No"),
    u("Unknown");

    private String label;

    Validation( String label ){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }
}
