/*
 * This file contains software that has been made available under The BSD
 * license. Use and distribution hereof are subject to the restrictions set
 * forth therein.
 * 
 * Copyright (c) 2003 TADA AB - Taby Sweden
 * All Rights Reserved
 */
#ifndef __pljava_pljava_h
#define __pljava_pljava_h

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#ifdef __STRICT_ANSI__
extern int vsnprintf(char* buf, size_t count, const char* format, va_list arg);
#endif

#include <postgres.h>
#include <lib/stringinfo.h>
#include <fmgr.h>
#include <mb/pg_wchar.h>
#include <utils/syscache.h>
#include <utils/memutils.h>
#include <tcop/tcopprot.h>

/* The elogErrorOccured will be set when a call from Java into one of the
 * backend functions results in a elog that causes a longjmp (Levels >= ERROR)
 * that was trapped using the PLJAVA_TRY/PLJAVA_CATCH macros.
 * When this happens, all further calls from Java must be blocked since the
 * state of the current transaction is unknown. Further more, once the function
 * that initially called Java finally returns, the intended longjmp (the one
 * to the original value of Warn_restart) must be made.
 */
extern bool elogErrorOccured;
extern bool isCallingJava;
extern bool pljavaEntryFence(JNIEnv* env);
 
/* NOTE!
 * When using the PLJAVA_TRY, PLJAVA_CATCH, PLJAVA_END_CATCH family of macros,
 * it is an ABSOLUTE NECESSITY to use the PLJAVA_TRY_RETURN, PLJAVA_TRY_BREAK
 * and PLJAVA_TRY_CONTINUE in place of any return, break, or continue. Failure
 * to comply with this rule is that the Warn_restart buffer is not restored!
 */
#define PLJAVA_TRY { \
	sigjmp_buf saveRestart; \
	memcpy(&saveRestart, &Warn_restart, sizeof(saveRestart)); \
	if(sigsetjmp(Warn_restart, 1) == 0) {

#define PLJAVA_CATCH \
		memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart)); \
	} else { \
		elogErrorOccured = true; \
		memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart));

#define PLJAVA_TCEND }}

#define PLJAVA_TRY_RETURN(retVal) { memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart)); return retVal; }
#define PLJAVA_TRY_RETURN_VOID { memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart)); return; }
#define PLJAVA_TRY_BREAK { memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart)); break; }
#define PLJAVA_TRY_CONTINUE { memcpy(&Warn_restart, &saveRestart, sizeof(Warn_restart)); continue; }

#define PLJAVA_ENTRY_FENCE(retVal) if(pljavaEntryFence(env)) return retVal;
#define PLJAVA_ENTRY_FENCE_VOID if(pljavaEntryFence(env)) return;

/* Some error codes missing from errcodes.h
 * 
 * Class 07 - Dynamic SQL Exception
 */
#define ERRCODE_INVALID_DESCRIPTOR_INDEX		MAKE_SQLSTATE('0','7', '0','0','9')

/*
 * Union used when coercing void* to jlong and vice versa
 */
typedef union
{
	void*  ptrVal;
	jlong  longVal; /* 64 bit quantity */
	struct
	{
		/* Used when calculating pointer hash in systems where
		 * a pointer is 64 bit
		 */
		uint32 intVal_1;
		uint32 intVal_2;
	} x64;
} Ptr2Long;

#ifdef __cplusplus
}
#endif
#endif