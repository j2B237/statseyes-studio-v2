package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.PodDevice;
import com.statseyes.studio.domain.model.PodDump;

import java.util.List;
import java.io.IOException;

public interface PodDevicePort {
    List<PodDevice> listDevices();
    PodDump openDump(String deviceId) throws IOException;
}