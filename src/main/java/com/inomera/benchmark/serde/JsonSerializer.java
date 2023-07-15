package com.inomera.benchmark.serde;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonSerializer {

    private static ObjectMapper jsonObjectMapper = new ObjectMapper();

    static {
	jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	jsonObjectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	jsonObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static int serializeAndDeserializeJson(PlayerResponse playerResponse) throws JsonProcessingException {
	var playerRestResponse = jsonObjectMapper.writeValueAsString(playerResponse);
	jsonObjectMapper.readValue(playerRestResponse, PlayerResponse.class);
	return 0;
    }

}
