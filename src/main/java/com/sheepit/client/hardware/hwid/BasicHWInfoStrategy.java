package com.sheepit.client.hardware.hwid;

import java.util.Optional;

public interface BasicHWInfoStrategy {

    Optional<String> getHarddriveID();
    Optional<String> getMAC();
    Optional<String> getProcessorName();
}
