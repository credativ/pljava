/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_postgresql_pljava_AclId */

#ifndef _Included_org_postgresql_pljava_AclId
#define _Included_org_postgresql_pljava_AclId
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_postgresql_pljava_AclId
 * Method:    getUser
 * Signature: ()Lorg/postgresql/pljava/AclId;
 */
JNIEXPORT jobject JNICALL Java_org_postgresql_pljava_AclId_getUser
  (JNIEnv *, jclass);

/*
 * Class:     org_postgresql_pljava_AclId
 * Method:    getSessionUser
 * Signature: ()Lorg/postgresql/pljava/AclId;
 */
JNIEXPORT jobject JNICALL Java_org_postgresql_pljava_AclId_getSessionUser
  (JNIEnv *, jclass);

/*
 * Class:     org_postgresql_pljava_AclId
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_postgresql_pljava_AclId_getName
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
