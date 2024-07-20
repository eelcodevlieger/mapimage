package com.github.mapimage.data;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.github.mapimage.Main.CONFIG;
import static com.github.mapimage.utils.JsonUtils.loadStringMapFromJsonFile;

public class PartyColorMapping {

    public static Map<String, String> getPartyColorMapping(final ElectionYearDataSource electionYearDataSource) throws IOException {
        return loadStringMapFromJsonFile(new File(CONFIG.getElectionYearDataSourceResourcePath(electionYearDataSource) + "party_color_mapping.json"));
    }
}
