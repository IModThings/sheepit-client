package com.sheepit.client.hardware.hwid.impl;

import com.sheepit.client.hardware.hwid.BasicHWInfoStrategy;
import com.sun.jna.Platform;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class BaseHWInfoImpl implements BasicHWInfoStrategy {
    protected HardwareAbstractionLayer hardware;

    public BaseHWInfoImpl() {
        hardware = new SystemInfo().getHardware();
    }

    @Override
    public Optional<String> getMAC() {
        List<String> macs = new ArrayList<>();
        List<NetworkIF> nics = hardware.getNetworkIFs();
        for (NetworkIF nic : nics) {
            macs.add(nic.getMacaddr());
        }
        Collections.sort(macs);
        return Optional.of(String.join(" ", macs));
    }

    @Override
    public Optional<String> getProcessorName() {
        return Optional.of(hardware.getProcessor().getProcessorIdentifier().getName());
    }

    public Optional<String> getHarddriveID() {
        String rootMountpoint;
        if (Platform.isWindows()) {
            rootMountpoint = "C:";
        }
        else {
            rootMountpoint = "/";
        }
        return getHarddriveID(rootMountpoint);
    }

    /**
     * Tries to find the root partition and returns that hard drives serial
     * @param rootMountpoint
     * @return
     */
    private Optional<String> getHarddriveID(String rootMountpoint) {
        var drives = hardware.getDiskStores();

        String hddSerial = "";
        boolean rootFound = false;
        Iterator<HWDiskStore> iterator = drives.iterator();

        while (rootFound == false && iterator.hasNext()) {
            var drive = iterator.next();
            for (var partition : drive.getPartitions()) {
                if (partition.getMountPoint().equals(rootMountpoint)) {
                    hddSerial = drive.getSerial();
                    rootFound = true;
                    break;
                }
            }
        }
        return hddSerial.isEmpty() ? Optional.empty() : Optional.of(hddSerial);
    }
}
