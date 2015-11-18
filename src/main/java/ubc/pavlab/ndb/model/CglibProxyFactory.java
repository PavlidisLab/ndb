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
import java.util.List;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.NoOp;

/**
 * This proxy factory uses the cglib and Objenesis to create new
 * proxy objects for existing objects.
 * 
 * @author mjacobson
 * @see https://github.com/junit-team/junit.contrib/blob/master/assertthrows/src/main/java/org/junit/contrib/
 *      assertthrows/proxy/CglibProxyFactory.java
 * 
 * @version $Id$
 */
public class CglibProxyFactory {

    /**
     * The default object creator.
     */
    private ObjectCreator objectCreator;

    /**
     * Get the object creator.
     *
     * @return the object creator (never null)
     */
    public ObjectCreator getObjectCreator() {
        if ( objectCreator == null ) {
            objectCreator = new ObjenesisObjectCreator();
        }
        return objectCreator;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy( Class<T> c, final InvocationHandler handler ) {
        net.sf.cglib.proxy.InvocationHandler cglibHandler = new net.sf.cglib.proxy.InvocationHandler() {
            @Override
            public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
                return handler.invoke( proxy, method, args );
            }
        };
        Class<?> proxyClass = createProxyClass( c );
        Factory proxy = ( Factory ) newInstance( c, proxyClass );
        proxy.setCallbacks( new Callback[] { cglibHandler, NoOp.INSTANCE } );
        return ( T ) proxy;
    }

    private Object newInstance( Class<?> baseClass, Class<?> c ) {
        ObjectCreator creator = getObjectCreator();
        try {
            return creator.newInstance( c );
        } catch ( Exception e ) {
            IllegalArgumentException ia = new IllegalArgumentException(
                    "Could not create a new proxy instance for the base class " +
                            baseClass.getName() );
            ia.initCause( e );
            throw ia;
        }
    }

    private <T> Class<?> createProxyClass( Class<?> baseClass ) {
        Enhancer enhancer = new Enhancer() {
            @SuppressWarnings("rawtypes")
            @Override
            protected void filterConstructors( Class sc, List constructors ) {
                // don't filter, so that even classes without
                // visible constructors will work
            }
        };
        enhancer.setUseFactory( true );
        enhancer.setSuperclass( baseClass );
        enhancer.setCallbackType( net.sf.cglib.proxy.InvocationHandler.class );

        try {
            return enhancer.createClass();
        } catch ( IllegalArgumentException e ) {
            throw new IllegalArgumentException(
                    "Can not create a proxy for the class " + baseClass.getName(), e );
        }
    }

    /**
     * A tool to create new objects, if possible without calling any constructors.
     */
    interface ObjectCreator {
        Object newInstance( Class<?> c ) throws Exception;
    }

    /**
     * An object creator that uses the Objenesis library.
     */
    static class ObjenesisObjectCreator implements ObjectCreator {

        /**
         * A ObjenesisStd object, or null if Objenesis is not in the classpath.
         */
        private static final Objenesis OBJENESIS = new ObjenesisStd();

        @Override
        public Object newInstance( Class<?> c ) {
            return OBJENESIS.newInstance( c );
        }

    }

}