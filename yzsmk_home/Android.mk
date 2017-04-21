LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := \
			glide1 \
			nineoldandroids \
			supportv4 \
			supportv7 

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) 

LOCAL_ASSET_FILES += $(call find-subdir-assets)

LOCAL_PACKAGE_NAME := yzsmk_home

LOCAL_SDK_VERSION := current

LOCAL_CERTIFICATE := platform
#LOCAL_JNI_SHARED_LIBRARIES += libserial_port1


include $(BUILD_PACKAGE)

############################################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
			glide1:libs/glide-3.7.0.jar \
			nineoldandroids:libs/nineoldandroids-2.4.0.jar \
			supportv4:libs/android-support-v4.jar \
			supportv7:libs/android-support-v7-appcompat.jar 
			

include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))
