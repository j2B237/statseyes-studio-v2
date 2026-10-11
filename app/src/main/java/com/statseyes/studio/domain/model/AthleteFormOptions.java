package com.statseyes.studio.domain.model;

import javax.swing.text.Position;
import java.util.List;

public record AthleteFormOptions(
        List<LookupOption> teams,
        List<LookupOption> positions
) {
}
