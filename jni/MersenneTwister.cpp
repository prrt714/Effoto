#include <jni.h>
#include <android/log.h>

#include <MersenneTwister.h>
#include <vnd_blueararat_Effoto_MTRNGJNILib.h>
#define  LOG_TAG    "mtrng2jni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

static MTRand mtrand1;

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_rand__
  (JNIEnv *, jclass)
{
	//MTRand mtrand1;
	return mtrand1.rand();
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_rand__F
  (JNIEnv *, jclass, jfloat f)
{
	return mtrand1.rand(f);
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randExc__
  (JNIEnv *, jclass)
{
	return mtrand1.randExc();
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randExc__F
  (JNIEnv *, jclass, jfloat f)
{
	return mtrand1.randExc(f);
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randDblExc__
  (JNIEnv *, jclass)
{
	return mtrand1.randDblExc();
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randDblExc__F
  (JNIEnv *, jclass, jfloat f)
{
	return mtrand1.randDblExc(f);
}

JNIEXPORT jfloat JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randNorm
  (JNIEnv *, jclass, jfloat f1, jfloat f2)
{
	return mtrand1.randNorm(f1,f2);
}

JNIEXPORT jint JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randInt__
  (JNIEnv *, jclass)
{
	//MTRand mtrand1;
	return mtrand1.randInt();
}

JNIEXPORT jint JNICALL Java_vnd_blueararat_Effoto_MTRNGJNILib_randInt__I
  (JNIEnv *, jclass, jint i)
{
	return mtrand1.randInt(i);
}
