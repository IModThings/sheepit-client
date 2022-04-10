package com.sheepit.client.hardware.hwid;

import com.sheepit.client.Log;
import com.sheepit.client.hardware.hwid.impl.BaseHWInfoImpl;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HWIdentifier {
    private final BasicHWInfoStrategy strategy;
    private Log log;

    public HWIdentifier(Log log) {
        strategy = new BaseHWInfoImpl();
        this.log = log;
    }

    public String getMAC() {
        return strategy.getMAC().orElse("");
    }

    public String getHarddriveSerial() {
        return strategy.getHarddriveID().orElse("");
    }

    public String getProcessorName() {
        return strategy.getProcessorName().orElse("");
    }

    public String getHardwareHash() {
        byte[] hash;
        String mac;
        String cpuName;
        String hdSerial;

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            if ((hdSerial = getHarddriveSerial()).length() > 0) {
                hash = digest.digest(hdSerial.getBytes(StandardCharsets.UTF_8));
                log.debug("HWIdentifier::getHardwareHash found hdd hash");
            }
            else if ((mac = getMAC()).length() > 0) {
                hash = digest.digest(mac.getBytes(StandardCharsets.UTF_8));
                log.debug("HWIdentifier::getHardwareHash found MAC hash");
            }
            else {        //Fallback: computing a hash out of homepath+jarFileLocation+cpuName
            	log.debug("HWIdentifier::getHardwareHash using fallback method");
                cpuName = getProcessorName();
                if (cpuName.isEmpty()) {
                    log.error("HWIdentifier::getHardwareHash failed to retrieve CPU name. Can't create hardware hash");
                    throw new UnsupportedOperationException("Unable to create hash!");
                }

                String homeDir = System.getProperty("user.home");   //get home path
                URL clientURL = getClass().getProtectionDomain().getCodeSource().getLocation();
                String clientPath = new File(clientURL.toString()).getParent(); //get jar file location

                hash = digest.digest((homeDir + clientPath + cpuName).getBytes(StandardCharsets.UTF_8));
            }

            BigInteger num = new BigInteger(1, hash);
            return num.toString(16);

        }
		catch (Exception e) {
            e.printStackTrace();
            log.error("HWIdentifier::getHardwareHash could not retrieve hash: " + e);
            return "unknown";
        }
    }
}
