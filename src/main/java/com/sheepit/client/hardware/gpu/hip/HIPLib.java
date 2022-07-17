package com.sheepit.client.hardware.gpu.hip;

import com.sheepit.client.hardware.gpu.GPUDevice;
import com.sheepit.client.hardware.gpu.GPULister;
import com.sheepit.client.hardware.gpu.hip.data.HIPDeviceProp_t;
import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;

import java.util.List;

public interface HIPLib extends Library {
	
	int hipGetDeviceCount(IntByReference numDevices);
	int hipRuntimeGetVersion(IntByReference version);
	int hipDriverGetVersion(IntByReference driverVersion);
	int hipGetDeviceProperties(HIPDeviceProp_t props, int deviceID);
	int hipGetDevice(IntByReference deviceID);
	
	int hipDeviceGetAttribute(IntByReference pi, int hipDeviceAttribute_t, int deviceID);
}

