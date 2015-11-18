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

package ubc.pavlab.ndb.model;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.InvocationHandler;

/**
 * TODO Document Me
 * 
 * @author mjacobson
 * @version $Id$
 */
public abstract class LazilyLoadedObject implements InvocationHandler {

    private Object target;
    protected int id;

    public LazilyLoadedObject( int id ) {
        this.id = id;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        if ( target == null )
            target = loadObject();
        return method.invoke( target, args );
    }

    /**
     * Loads the proxied object. This might be an expensive operation
     * or loading lots of objects could consume a lot of memory, so
     * we only load the object when it's needed.
     */
    protected abstract Object loadObject();

}
