package com.statseyes.studio.application.usecase.session;

import com.statseyes.studio.application.port.PodDevicePort;
import com.statseyes.studio.domain.model.PodDevice;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListPodDevicesUseCase {

    // ==================
    // INSTANCE VARIABLES
    // ==================

    private final PodDevicePort devicePort;

    public ListPodDevicesUseCase(PodDevicePort devicePort) {
        this.devicePort = devicePort;
    }

    public List<PodDevice> execute(boolean includeAllPorts) {
        return devicePort.listDevices().stream()
                .filter(d -> includeAllPorts || d.likelyUsb())
                .toList();
    }
}
