/*
 * This file contains software that has been made available under
 * The Mozilla Public License 1.1. Use and distribution hereof are
 * subject to the restrictions set forth therein.
 *
 * Copyright (c) 2003 TADA AB - Taby Sweden
 * All Rights Reserved
 */
package org.postgresql.pljava;

/**
 * The <code>NativeStruct</code> maintains a pointer to a piece of memory
 * allocated with a life cycle that spans a call from the PostgreSQL function
 * manager (using <code>palloc()</code>). Since Java uses a garbage collector
 * and since an object in the Java domain might survive longer than memory
 * allocated using <code>palloc()</code>, some code must assert that pointers
 * from Java objects to such memory is cleared when the function manager call
 * ends. This code resides in the JNI part of the Pl/Java package.
 * 
 * @author Thomas Hallgren
 */
public abstract class NativeStruct
{
	/**
	 * Pointer that points stright to the allocated structure. This
	 * value is used by the internal JNI routines only. It must never
	 * be serialized.
	 */
	private transient long m_native;
}
