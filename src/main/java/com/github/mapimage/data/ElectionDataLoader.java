package com.github.mapimage.data;

import com.github.mapimage.domain.Candidates;

import java.util.Map;
import java.util.function.Function;

public interface ElectionDataLoader extends Function<Map<String, String>, Map<String, Candidates>> {
}