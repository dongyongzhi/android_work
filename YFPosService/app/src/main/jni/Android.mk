LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := libserial_port1
LOCAL_SRC_FILES := SerialPort.c
//LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
