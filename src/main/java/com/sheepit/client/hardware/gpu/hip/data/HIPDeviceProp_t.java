package com.sheepit.client.hardware.gpu.hip.data;

import com.sun.jna.Structure;

@Structure.FieldOrder({
	"name",
	"totalGlobalMem",
	"major",
	"minor",
	"multiProcessCount",
	"l2CacheSize",
	"maxThreadsPerMultiProcessor",
	"computeMode",
	"clockInstructionRate",
//	"arch",
	"concurrentKernels",
	"pciBusID",
	"pciDeviceID",
	"maxSharedMemoryPerMultiProcessor",
	"isMultiGpuBoard",
	"canMapHostMemory",
	"gcnArch",
	"gcnArchName"
})
public class HIPDeviceProp_t extends Structure {
	public static class ByReference extends HIPDeviceProp_t implements Structure.ByReference {
	
	}
	public byte[] name;
	public long totalGlobalMem;
	public int major;
	public int minor;
	public int multiProcessCount;
	public int l2CacheSize;
	public int maxThreadsPerMultiProcessor;
	public int computeMode;
	public int clockInstructionRate;
	//public HIPDeviceArch_t arch;
	public int concurrentKernels;
	public int pciBusID;
	public int pciDeviceID;
	public long maxSharedMemoryPerMultiProcessor;
	public boolean isMultiGpuBoard;
	public boolean canMapHostMemory;
	public int gcnArch;
	public char[] gcnArchName;
	
	
	public HIPDeviceProp_t() {
//		this.arch = new HIPDeviceArch_t();
		name = new byte[256];
		gcnArchName = new char[256];
		this.totalGlobalMem = 0L;
		this.major = 0;
		this.minor = 0;
		this.multiProcessCount = 0;
		this.l2CacheSize = 0;
		this.maxThreadsPerMultiProcessor = 0;
		this.computeMode = 0;
		this.clockInstructionRate = 0;
		this.concurrentKernels = 0;
		this.pciBusID = 0;
		this.pciDeviceID = 0;
		this.maxSharedMemoryPerMultiProcessor = 0L;
		this.isMultiGpuBoard = false;
		this.canMapHostMemory = false;
		this.gcnArch = 0;
	}
}
