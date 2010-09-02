/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.tpon.dsproxy.support.jndi;

import com.tpon.dsproxy.listener.QueryExecutionListener;

import javax.naming.BinaryRefAddr;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

/**
 * Implements the javax.naming ObjectFactory required to support proxied datasources correctly.
 *
 * @author Juergen, 2010-03-15
 * @version 1.0
 */
public class JndiProxyDataSourceFactory implements ObjectFactory {

    private static final String TYPE_PREFIX = JndiProxyDataSourceFactory.class.getSimpleName();

    private static String infoAddressType = TYPE_PREFIX.concat(".parentInfo");

    /**
     * Creates a reference that can re-construct the proxied datasource.
     *
     * @param jndiProxyDataSource The datasource to reference.
     * @return a reference that can re-construct the proxied datasource.
     * @throws NamingException In case of the parten reference failed to get created.
     */
    public static Reference createReference(JndiProxyDataSource jndiProxyDataSource) throws NamingException {

        Reference parent = ((Referenceable) jndiProxyDataSource.getParentDataSource()).getReference();
        Object[] parentInfo = new Object[]{
                parent,
                jndiProxyDataSource.getDataSourceName(),
                jndiProxyDataSource.getListener()
        };

        return new Reference(JndiProxyDataSource.class.getName(),
                new BinaryRefAddr(infoAddressType, toByteArray(parentInfo)),
                JndiProxyDataSourceFactory.class.getName(), null);
    }

    static byte[] toByteArray(Object obj) {
        try {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream oOut = new ObjectOutputStream(bOut);
            oOut.writeObject(obj);
            oOut.close();
            return bOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Object fromByteArray(byte[] data) {
        try {
            ObjectInputStream oIn = new ObjectInputStream(new ByteArrayInputStream(data));
            return oIn.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ObjectFactory parentFactory;

    /**
     * {@inheritDoc}
     */
    public Object getObjectInstance(Object obj, Name name,
                                    Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (!(obj instanceof Reference))
            return null;

        Reference reference = (Reference) obj;

        RefAddr parentInfoContent = reference.get(infoAddressType);
        if (parentInfoContent == null)
            throw new IllegalArgumentException("The given reference is not compatible.");

        Object[] parentInfo = (Object[]) fromByteArray((byte[]) parentInfoContent.getContent());

        Reference originalReference = (Reference) parentInfo[0];

        String factoryClass = originalReference.getFactoryClassName();
        if (parentFactory == null || !parentFactory.getClass().getName().equals(factoryClass))
            parentFactory = (ObjectFactory) Class.forName(factoryClass).newInstance();

        DataSource parentDataSource = (DataSource) parentFactory.getObjectInstance(
                originalReference, name, nameCtx, environment);

        JndiProxyDataSource pds = new JndiProxyDataSource(parentInfo[1].toString(), parentDataSource);
        pds.setListener((QueryExecutionListener) parentInfo[2]);
        return pds;
    }
}
