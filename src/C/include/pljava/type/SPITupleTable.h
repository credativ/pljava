/*
 * This file contains software that has been made available under The BSD
 * license. Use and distribution hereof are subject to the restrictions set
 * forth therein.
 * 
 * Copyright (c) 2003 TADA AB - Taby Sweden
 * All Rights Reserved
 */
#ifndef __pljava_SPITupleTable_h
#define __pljava_SPITupleTable_h

#include "pljava/type/NativeStruct.h"
#ifdef __cplusplus
extern "C" {
#endif

#include <executor/spi.h>

/*****************************************************************
 * The SPITupleTable java class extends the NativeStruct and provides JNI
 * access to some of the attributes of the SPITupleTable structure.
 * 
 * Author: Thomas Hallgren
 *****************************************************************/

/*
 * Create the org.postgresql.pljava.SPITupleTable instance
 */
extern jobject SPITupleTable_create(JNIEnv* env, SPITupleTable* SPITupleTable);

#ifdef __cplusplus
}
#endif
#endif