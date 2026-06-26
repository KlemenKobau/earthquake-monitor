package com.kkobau.earthquakemonitor.dto.deserializers;

import com.kkobau.earthquakemonitor.dto.Coordinates;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class CoordinatesDeserializer extends ValueDeserializer<Coordinates> {

    @Override
    public Coordinates deserialize(JsonParser jsonParser, DeserializationContext context) throws JacksonException {
        JsonNode arrayNode = jsonParser.readValueAsTree();
        double longitude = arrayNode.get(0).asDouble();
        double latitude = arrayNode.get(1).asDouble();
        double depth = arrayNode.size() > 2 ? arrayNode.get(2).asDouble() : 0.0;
        return new Coordinates(longitude, latitude, depth);
    }
}
