package com.github.mapimage.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mapimage.data.ElectionYearDataSource;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.github.mapimage.Main.CONFIG;

public class Config {

    public static final String CONFIG_JSON_PATH = "target/classes/mapimage-config.json";
    public Map<ElectionYearDataSource, ElectionDataSourceConfig> electionDataSources = new HashMap<>();

    public String targetOutputBaseDir;
    public String resourcesDir;
    public String svgMapInputFileNameSuffix;

    @SneakyThrows
    public static Config load(){
        var objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return objectMapper.readValue(new URI("file:" + CONFIG_JSON_PATH).toURL(), Config.class);
    }

    public ElectionDataSourceConfig get(final ElectionYearDataSource electionDataSource){
        return electionDataSources.get(electionDataSource);
    }

    public static class ElectionDataSourceConfig {

    }

    public String getSvgMapInputFileName(final int year) {
        return year + svgMapInputFileNameSuffix;
    }

    public String getElectionYearDataSourceResourcePath(final ElectionYearDataSource electionYearDataSource){
        return CONFIG.resourcesDir +
                "\\election_data\\%d\\%s\\".formatted(
                        electionYearDataSource.getElectionYear().getYear(),
                        electionYearDataSource.getElectionDataSource().name().toLowerCase());
    }
}
