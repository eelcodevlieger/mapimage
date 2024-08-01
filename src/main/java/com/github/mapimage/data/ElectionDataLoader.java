package com.github.mapimage.data;

import com.github.mapimage.domain.CandidateResults;

import java.util.Map;

public interface ElectionDataLoader {
    Map<String, CandidateResults> load();
}