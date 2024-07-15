package com.knocksfornometer.mapimage.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
import lombok.SneakyThrows;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public Map<ElectionYearDataSource, ElectionDataSourceConfig> electionDataSources = new HashMap<>();

    public String targetOutputBaseDir;
    public String resourcesDir;

    @SneakyThrows
    public static Config load(){
        var objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return objectMapper.readValue(new URI("file:target/classes/mapimage-config.json").toURL(), Config.class);
    }

    public ElectionDataSourceConfig get(final ElectionYearDataSource electionDataSource){
        return electionDataSources.get(electionDataSource);
    }

    public static class ElectionDataSourceConfig {

    }
}
