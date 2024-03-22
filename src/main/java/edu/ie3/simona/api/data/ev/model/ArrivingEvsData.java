package edu.ie3.simona.api.data.ev.model;

import java.util.List;
import java.util.Optional;

public record ArrivingEvsData(List<EvModel> arrivals, Optional<Long> nextTick) {}
