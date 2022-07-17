package com.sheepit.client.hardware.gpu.hip;

import com.sheepit.client.hardware.gpu.GPUDevice;
import com.sheepit.client.hardware.gpu.GPULister;
import com.sheepit.client.hardware.gpu.hip.data.HIPDeviceAttribute_t;
import com.sheepit.client.hardware.gpu.hip.data.HIPDeviceProp_t;
import com.sheepit.client.hardware.gpu.hip.data.HipError_t;
import com.sheepit.client.os.Windows;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import oshi.SystemInfo;

import java.util.ArrayList;
import java.util.List;

public class HIP implements GPULister {
	
	private static final String HIP_LIBRARY = "amdhip64";
	private static final String HIP_LIBRARY_LINUX = "libamdhip64.so"; 	//for potential future use
	private static final String MINIMAL_WINDOWS_DRIVER_VERSION = "30.0.15021.7000";
	public static final String TYPE = "HIP";
	
	private HIPLib hip;
	
	public HIP() {
	
	}
	
	public String getIdentifier(int deviceID) {
		
		String deviceName = getDeviceName(deviceID);
		
		int pciDeviceID, pciBusID, pciDomainID;
		pciBusID = getPciBusID(deviceID);
		pciDeviceID = getPciDeviceID(deviceID);
		pciDomainID = getPciDomainID(deviceID);
		
		String deviceIdentifier = String.format("HIP_%s_%04x:%02x:%02x",
			deviceName,
			pciDomainID,
			pciBusID,
			pciDeviceID);
		
		return deviceIdentifier;
	}
	
	private long getDeviceMemory(int deviceID) {
		HIPDeviceProp_t deviceProperties = new HIPDeviceProp_t();
		int status = hip.hipGetDeviceProperties(deviceProperties, deviceID);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error retrieving name of device " + deviceID);
			return -1;
		}
		return deviceProperties.totalGlobalMem;
	}
	
	public int getRuntimeVersion() {
		IntByReference version = new IntByReference();
		int result = hip.hipRuntimeGetVersion(version);
		if (result != HipError_t.HIP_SUCCESS) {
			System.err.println("Error");
		}
		return version.getValue();
	}
	
	private String getDriverVersion() {
		SystemInfo info = new SystemInfo();
		var hardware = info.getHardware();
		var gpus = hardware.getGraphicsCards();
		
		if (gpus.isEmpty() || getNumberOfDevices() == 0) {
			return null;
		}
		String driverVersion = "";
		for (var gpu : gpus) {
			if (gpu.getName().contains("AMD ")) {
				driverVersion = gpu.getVersionInfo();
				break;
			}
		}
		
		var tmp = driverVersion.split("=");
		if (tmp.length < 2) {	//could not extract driver version number. Looking for a string like this: "DriverVersion=30.0.21017.1000"
			return null;
		}
		
		String versionNumber = tmp[1];
		
		return versionNumber;
	}
	
	private int getNumberOfDevices() {
		IntByReference deviceCount = new IntByReference();
		int status = hip.hipGetDeviceCount(deviceCount);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error");
			return -1;
		}
		return deviceCount.getValue();
	}
	
	public int getCurrentDeviceID() {
		IntByReference deviceID = new IntByReference();
		int status = hip.hipGetDevice(deviceID);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error");
			return -1;
		}
		
		return deviceID.getValue();
	}
	
	public String getDeviceName(int deviceID) {
		HIPDeviceProp_t deviceProperties = new HIPDeviceProp_t();
		int status = hip.hipGetDeviceProperties(deviceProperties, deviceID);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error retrieving name of device " + deviceID);
			return null;
		}
		
		return new String(deviceProperties.name).trim();
	}
	
	private int getPciBusID(int deviceID) {
		IntByReference busID = new IntByReference();
		int status = hip.hipDeviceGetAttribute(busID, HIPDeviceAttribute_t.hipDeviceAttributePciBusId, deviceID);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error");
			return -1;
		}
		
		return busID.getValue();
	}
	
	private int getPciDeviceID(int deviceID) {
		IntByReference id = new IntByReference();
		int status = hip.hipDeviceGetAttribute(id, HIPDeviceAttribute_t.hipDeviceAttributePciDeviceId, deviceID);
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("Error");
			return -1;
		}
		
		return id.getValue();
	}
	
	private int getPciDomainID(int deviceID) {
		IntByReference id = new IntByReference();
		int status = hip.hipDeviceGetAttribute(id, HIPDeviceAttribute_t.hipDeviceAttributePciDomainID+2, deviceID);	//69 does not seem to be present on my installation. Tests conclude that its "likely" 71
		if (status != HipError_t.HIP_SUCCESS) {
			System.err.println("HIP::getPciDomainID::Error: " + status);
			return -1;
		}
		
		return id.getValue();
	}
	
	@Override public List<GPUDevice> getGpus() {
		
		try {
			this.hip = (HIPLib) Native.load(HIP_LIBRARY, HIPLib.class);
		}
		catch (java.lang.UnsatisfiedLinkError e) {
			System.out.println("HIP::getGpus failed(A) to load HIP lib (path: " + HIP_LIBRARY + ")");
			return null;
		}
		catch (java.lang.ExceptionInInitializerError e) {
			System.out.println("HIP::getGpus failed(B) ExceptionInInitializerError " + e);
			return null;
		}
		catch (Exception e) {
			System.out.println("HIP::getGpus failed(C) generic exception " + e);
			return null;
		}
		
		List<GPUDevice> gpuDevices = new ArrayList<>();
		
		int numberOfDevices = getNumberOfDevices();
		if (numberOfDevices < 1) {
			return null;
		}
		
		//check that the driver is capable
		long driverVersionAsLong = -1;
		String driverVersion = "";
		try {
			driverVersion = getDriverVersion();
			if (driverVersion == null) {
				return null;
			}
			
			driverVersionAsLong = Long.parseLong(driverVersion.replace(".", ""));
			long minimumVersionAsLong = Long.parseLong(MINIMAL_WINDOWS_DRIVER_VERSION.replace(".",""));
			
			if (driverVersionAsLong < minimumVersionAsLong) {
				System.out.println("HIP::getGpus AMD driver too old");
				return null;
			}
		}
		catch (NumberFormatException e) {
			System.out.println("HIP::getGpus Unable to convert driver version to long: " + driverVersion + ". Exception: " + e.getMessage());
			return null;
		}
		
		int numberedID = 0;
		for (int i = 0; i < numberOfDevices; i++) {
			String deviceName = getDeviceName(i);
			long vram = getDeviceMemory(i);
			String deviceIdentifier = getIdentifier(i);
			String oldID = TYPE + "_" + numberedID;
			GPUDevice device = new GPUDevice(TYPE, deviceName, vram, deviceIdentifier, oldID);
			gpuDevices.add(device);
			numberedID++;
		}
		
		return gpuDevices;
	}
}
