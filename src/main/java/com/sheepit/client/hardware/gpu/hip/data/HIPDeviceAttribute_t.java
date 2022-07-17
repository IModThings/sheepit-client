package com.sheepit.client.hardware.gpu.hip.data;

public interface HIPDeviceAttribute_t {
	public static final int hipDeviceAttributeCudaCompatibleBegin = 0;
	public static final int hipDeviceAttributeEccEnabled = hipDeviceAttributeCudaCompatibleBegin; ///< Whether ECC support is enabled.
	public static final int hipDeviceAttributeAccessPolicyMaxWindowSize = 1;        ///< Cuda only. The maximum size of the window policy in bytes.
	public static final int hipDeviceAttributeAsyncEngineCount = 2;                 ///< Cuda only. Asynchronous engines number.
	public static final int hipDeviceAttributeCanMapHostMemory = 3;                 ///< Whether host memory can be mapped into device address space
	public static final int hipDeviceAttributeCanUseHostPointerForRegisteredMem = 4;///< Cuda only. Device can access host registered memory
	///< at the same virtual address as the CPU
	public static final int hipDeviceAttributeClockRate = 5;                        ///< Peak clock frequency in kilohertz.
	public static final int hipDeviceAttributeComputeMode = 6;                      ///< Compute mode that device is currently in.
	public static final int hipDeviceAttributeComputePreemptionSupported = 7;       ///< Cuda only. Device supports Compute Preemption.
	public static final int hipDeviceAttributeConcurrentKernels = 8;                ///< Device can possibly execute multiple kernels concurrently.
	public static final int hipDeviceAttributeConcurrentManagedAccess = 9;          ///< Device can coherently access managed memory concurrently with the CPU
	public static final int hipDeviceAttributeCooperativeLaunch = 10;                ///< Support cooperative launch
	public static final int hipDeviceAttributeCooperativeMultiDeviceLaunch = 11;     ///< Support cooperative launch on multiple devices
	public static final int hipDeviceAttributeDeviceOverlap = 12;                    ///< Cuda only. Device can concurrently copy memory and execute a kernel.
	///< Deprecated. Use instead asyncEngineCount.
	public static final int hipDeviceAttributeDirectManagedMemAccessFromHost = 13;   ///< Host can directly access managed memory on
	///< the device without migration
	public static final int hipDeviceAttributeGlobalL1CacheSupported = 14;           ///< Cuda only. Device supports caching globals in L1
	public static final int hipDeviceAttributeHostNativeAtomicSupported = 15;        ///< Cuda only. Link between the device and the host supports native atomic operations
	public static final int hipDeviceAttributeIntegrated = 16;                       ///< Device is integrated GPU
	public static final int hipDeviceAttributeIsMultiGpuBoard = 17;                  ///< Multiple GPU devices.
	public static final int hipDeviceAttributeKernelExecTimeout = 18;                ///< Run time limit for kernels executed on the device
	public static final int hipDeviceAttributeL2CacheSize = 19;                      ///< Size of L2 cache in bytes. 0 if the device doesn't have L2 cache.
	public static final int hipDeviceAttributeLocalL1CacheSupported = 20;            ///< caching locals in L1 is supported
	public static final int hipDeviceAttributeLuid = 21;                             ///< Cuda only. 8-byte locally unique identifier in 8 bytes. Undefined on TCC and non-Windows platforms
	public static final int hipDeviceAttributeLuidDeviceNodeMask = 22;               ///< Cuda only. Luid device node mask. Undefined on TCC and non-Windows platforms
	public static final int hipDeviceAttributeComputeCapabilityMajor = 23;           ///< Major compute capability version number.
	public static final int hipDeviceAttributeManagedMemory = 24;                    ///< Device supports allocating managed memory on this system
	public static final int hipDeviceAttributeMaxBlocksPerMultiProcessor = 25;       ///< Cuda only. Max block size per multiprocessor
	public static final int hipDeviceAttributeMaxBlockDimX = 26;                     ///< Max block size in width.
	public static final int hipDeviceAttributeMaxBlockDimY = 27;                     ///< Max block size in height.
	public static final int hipDeviceAttributeMaxBlockDimZ = 28;                     ///< Max block size in depth.
	public static final int hipDeviceAttributeMaxGridDimX = 29;                      ///< Max grid size  in width.
	public static final int hipDeviceAttributeMaxGridDimY = 30;                      ///< Max grid size  in height.
	public static final int hipDeviceAttributeMaxGridDimZ = 31;                      ///< Max grid size  in depth.
	public static final int hipDeviceAttributeMaxSurface1D = 32;                     ///< Maximum size of 1D surface.
	public static final int hipDeviceAttributeMaxSurface1DLayered = 33;              ///< Cuda only. Maximum dimensions of 1D layered surface.
	public static final int hipDeviceAttributeMaxSurface2D = 34;                     ///< Maximum dimension (width; height) of 2D surface.
	public static final int hipDeviceAttributeMaxSurface2DLayered = 35;              ///< Cuda only. Maximum dimensions of 2D layered surface.
	public static final int hipDeviceAttributeMaxSurface3D = 36;                     ///< Maximum dimension (width; height; depth) of 3D surface.
	public static final int hipDeviceAttributeMaxSurfaceCubemap = 37;                ///< Cuda only. Maximum dimensions of Cubemap surface.
	public static final int hipDeviceAttributeMaxSurfaceCubemapLayered = 38;         ///< Cuda only. Maximum dimension of Cubemap layered surface.
	public static final int hipDeviceAttributeMaxTexture1DWidth = 39;                ///< Maximum size of 1D texture.
	public static final int hipDeviceAttributeMaxTexture1DLayered = 40;              ///< Cuda only. Maximum dimensions of 1D layered texture.
	public static final int hipDeviceAttributeMaxTexture1DLinear = 41;               ///< Maximum number of elements allocatable in a 1D linear texture.
	///< Use cudaDeviceGetTexture1DLinearMaxWidth() instead on Cuda.
	public static final int hipDeviceAttributeMaxTexture1DMipmap = 42;               ///< Cuda only. Maximum size of 1D mipmapped texture.
	public static final int hipDeviceAttributeMaxTexture2DWidth = 43;                ///< Maximum dimension width of 2D texture.
	public static final int hipDeviceAttributeMaxTexture2DHeight = 44;               ///< Maximum dimension hight of 2D texture.
	public static final int hipDeviceAttributeMaxTexture2DGather = 45;               ///< Cuda only. Maximum dimensions of 2D texture if gather operations  performed.
	public static final int hipDeviceAttributeMaxTexture2DLayered = 46;              ///< Cuda only. Maximum dimensions of 2D layered texture.
	public static final int hipDeviceAttributeMaxTexture2DLinear = 47;               ///< Cuda only. Maximum dimensions (width; height; pitch) of 2D textures bound to pitched memory.
	public static final int hipDeviceAttributeMaxTexture2DMipmap = 48;               ///< Cuda only. Maximum dimensions of 2D mipmapped texture.
	public static final int hipDeviceAttributeMaxTexture3DWidth = 49;                ///< Maximum dimension width of 3D texture.
	public static final int hipDeviceAttributeMaxTexture3DHeight = 50;               ///< Maximum dimension height of 3D texture.
	public static final int hipDeviceAttributeMaxTexture3DDepth = 51;                ///< Maximum dimension depth of 3D texture.
	public static final int hipDeviceAttributeMaxTexture3DAlt = 52;                  ///< Cuda only. Maximum dimensions of alternate 3D texture.
	public static final int hipDeviceAttributeMaxTextureCubemap = 53;                ///< Cuda only. Maximum dimensions of Cubemap texture
	public static final int hipDeviceAttributeMaxTextureCubemapLayered = 54;         ///< Cuda only. Maximum dimensions of Cubemap layered texture.
	public static final int hipDeviceAttributeMaxThreadsDim = 55;                    ///< Maximum dimension of a block
	public static final int hipDeviceAttributeMaxThreadsPerBlock = 56;               ///< Maximum number of threads per block.
	public static final int hipDeviceAttributeMaxThreadsPerMultiProcessor = 57;      ///< Maximum resident threads per multiprocessor.
	public static final int hipDeviceAttributeMaxPitch = 58;                         ///< Maximum pitch in bytes allowed by memory copies
	public static final int hipDeviceAttributeMemoryBusWidth = 59;                   ///< Global memory bus width in bits.
	public static final int hipDeviceAttributeMemoryClockRate = 60;                  ///< Peak memory clock frequency in kilohertz.
	public static final int hipDeviceAttributeComputeCapabilityMinor = 61;           ///< Minor compute capability version number.
	public static final int hipDeviceAttributeMultiGpuBoardGroupID = 62;             ///< Cuda only. Unique ID of device group on the same multi-GPU board
	public static final int hipDeviceAttributeMultiprocessorCount = 63;              ///< Number of multiprocessors on the device.
	public static final int hipDeviceAttributeName = 64;                             ///< Device name.
	public static final int hipDeviceAttributePageableMemoryAccess = 65;             ///< Device supports coherently accessing pageable memory
	///< without calling hipHostRegister on it
	public static final int hipDeviceAttributePageableMemoryAccessUsesHostPageTables = 66; ///< Device accesses pageable memory via the host's page tables
	public static final int hipDeviceAttributePciBusId = 67;                         ///< PCI Bus ID. // should be 68 but seems to be one off -- Harlekin
	public static final int hipDeviceAttributePciDeviceId = 68;                      ///< PCI Device ID. // see above
	public static final int hipDeviceAttributePciDomainID = 69;                      ///< PCI Domain ID. // see above
	public static final int hipDeviceAttributePersistingL2CacheMaxSize = 70;         ///< Cuda11 only. Maximum l2 persisting lines capacity in bytes
	public static final int hipDeviceAttributeMaxRegistersPerBlock = 71;             ///< 32-bit registers available to a thread block. This number is shared
	///< by all thread blocks simultaneously resident on a multiprocessor.
	public static final int hipDeviceAttributeMaxRegistersPerMultiprocessor = 72;    ///< 32-bit registers available per block.
	public static final int hipDeviceAttributeReservedSharedMemPerBlock = 73;        ///< Cuda11 only. Shared memory reserved by CUDA driver per block.
	public static final int hipDeviceAttributeMaxSharedMemoryPerBlock = 74;          ///< Maximum shared memory available per block in bytes.
	public static final int hipDeviceAttributeSharedMemPerBlockOptin = 75;           ///< Cuda only. Maximum shared memory per block usable by special opt in.
	public static final int hipDeviceAttributeSharedMemPerMultiprocessor = 76;       ///< Cuda only. Shared memory available per multiprocessor.
	public static final int hipDeviceAttributeSingleToDoublePrecisionPerfRatio = 77; ///< Cuda only. Performance ratio of single precision to double precision.
	public static final int hipDeviceAttributeStreamPrioritiesSupported = 78;        ///< Cuda only. Whether to support stream priorities.
	public static final int hipDeviceAttributeSurfaceAlignment = 79;                 ///< Cuda only. Alignment requirement for surfaces
	public static final int hipDeviceAttributeTccDriver = 80;                        ///< Cuda only. Whether device is a Tesla device using TCC driver
	public static final int hipDeviceAttributeTextureAlignment = 81;                 ///< Alignment requirement for textures
	public static final int hipDeviceAttributeTexturePitchAlignment = 82;            ///< Pitch alignment requirement for 2D texture references bound to pitched memory;
	public static final int hipDeviceAttributeTotalConstantMemory = 83;              ///< Constant memory size in bytes.
	public static final int hipDeviceAttributeTotalGlobalMem = 84;                   ///< Global memory available on devicice.
	public static final int hipDeviceAttributeUnifiedAddressing = 85;                ///< Cuda only. An unified address space shared with the host.
	public static final int hipDeviceAttributeUuid = 86;                             ///< Cuda only. Unique ID in 16 byte.
	public static final int hipDeviceAttributeWarpSize = 87;                         ///< Warp size in threads.
	public static final int hipDeviceAttributeMemoryPoolsSupported = 88;             ///< Device supports HIP Stream Ordered Memory Allocator
	public static final int hipDeviceAttributeVirtualMemoryManagementSupported = 89; ///< Device supports HIP virtual memory management
	
	public static final int hipDeviceAttributeCudaCompatibleEnd = 90;
	public static final int hipDeviceAttributeAmdSpecificBegin = 91;
	
	public static final int hipDeviceAttributeClockInstructionRate = 92;  ///< Frequency in khz of the timer used by the device-side "clock*"
	public static final int hipDeviceAttributeArch = 93;                                     ///< Device architecture
	public static final int hipDeviceAttributeMaxSharedMemoryPerMultiprocessor = 94;         ///< Maximum Shared Memory PerMultiprocessor.
	public static final int hipDeviceAttributeGcnArch = 95;                                  ///< Device gcn architecture
	public static final int hipDeviceAttributeGcnArchName = 96;                              ///< Device gcnArch name in 256 bytes
	public static final int hipDeviceAttributeHdpMemFlushCntl = 97;                          ///< Address of the HDP_MEM_COHERENCY_FLUSH_CNTL register
	public static final int hipDeviceAttributeHdpRegFlushCntl = 98;                          ///< Address of the HDP_REG_COHERENCY_FLUSH_CNTL register
	public static final int hipDeviceAttributeCooperativeMultiDeviceUnmatchedFunc = 99;      ///< Supports cooperative launch on multiple
	///< devices with unmatched functions
	public static final int hipDeviceAttributeCooperativeMultiDeviceUnmatchedGridDim = 100;   ///< Supports cooperative launch on multiple
	///< devices with unmatched grid dimensions
	public static final int hipDeviceAttributeCooperativeMultiDeviceUnmatchedBlockDim = 101;  ///< Supports cooperative launch on multiple
	///< devices with unmatched block dimensions
	public static final int hipDeviceAttributeCooperativeMultiDeviceUnmatchedSharedMem = 102; ///< Supports cooperative launch on multiple
	///< devices with unmatched shared memories
	public static final int hipDeviceAttributeIsLargeBar = 103;                               ///< Whether it is LargeBar
	public static final int hipDeviceAttributeAsicRevision = 104;                             ///< Revision of the GPU in this device
	public static final int hipDeviceAttributeCanUseStreamWaitValue = 105;                    ///< '1' if Device supports hipStreamWaitValue32() and
	///< hipStreamWaitValue64(); '0' otherwise.
	public static final int hipDeviceAttributeImageSupport = 106;                             ///< '1' if Device supports image; '0' otherwise.
	public static final int hipDeviceAttributePhysicalMultiProcessorCount = 107;              ///< All available physical compute
	///< units for the device
	public static final int hipDeviceAttributeFineGrainSupport = 108;                         ///< '1' if Device supports fine grain; '0' otherwise
	
	public static final int hipDeviceAttributeAmdSpecificEnd = 109;
	public static final int hipDeviceAttributeVendorSpecificBegin = 110;
	// Extended attributes for vendors
}
