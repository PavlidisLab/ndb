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

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public class Tuples {
    public static <T1, T2> Tuple2<T1, T2> tuple2( T1 t1, T2 t2 ) {
        return new Tuples.Tuple2<T1, T2>( t1, t2 );
    }

    public static class Tuple2<T1, T2> {
        protected T1 t1;
        protected T2 t2;

        public Tuple2( T1 f1, T2 f2 ) {
            this.t1 = f1;
            this.t2 = f2;
        }

        public T1 getT1() {
            return t1;
        }

        public T2 getT2() {
            return t2;
        }
    }
}
