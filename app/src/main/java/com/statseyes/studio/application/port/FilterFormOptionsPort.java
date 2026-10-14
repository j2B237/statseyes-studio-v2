package com.statseyes.studio.application.port;

import com.statseyes.studio.domain.model.FilterFormOptions;
public interface FilterFormOptionsPort {

    FilterFormOptions search(Integer accountId, String token);
}
