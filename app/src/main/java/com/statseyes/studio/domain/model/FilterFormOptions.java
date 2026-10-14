package com.statseyes.studio.domain.model;

import java.util.List;
public record FilterFormOptions(
        List<LookupFilterOption> teams,
        List<LookupFilterOption> positions,
        List<LookupFilterOption> athlete
) {
}
