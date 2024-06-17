package com.knocksfornometer.mapimage.data;

import com.knocksfornometer.mapimage.domain.Candidates;

import java.util.Map;
import java.util.function.Function;

public interface ElectionDataLoader extends Function<Map<String, String>, Map<String, Candidates>> {
}