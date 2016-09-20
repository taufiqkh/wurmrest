package com.quiptiq.wurmrest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

/**
 * Deserialises Result objects
 */
public class ResultDeserializer extends
        JsonDeserializer<Result<?>> implements ContextualDeserializer {
    private JavaType valueType;

    @Override
    public JsonDeserializer<?> createContextual(
            DeserializationContext deserializationContext, BeanProperty beanProperty)
            throws JsonMappingException {
        JavaType resultType;
        if (beanProperty == null) { // Occurs when Result is a root value
            resultType = deserializationContext.getContextualType();
        } else {
            resultType = beanProperty.getType();
        }
        JavaType valueType = resultType.containedType(0);
        ResultDeserializer deserializer = new ResultDeserializer();
        deserializer.valueType = valueType;
        return deserializer;
    }

    @Override
    public Result<?> deserialize(JsonParser jsonParser, DeserializationContext
            deserializationContext) throws IOException, JsonProcessingException {
        if ("error".equals(jsonParser.nextFieldName())) {
            return Result.error(jsonParser.nextTextValue());
        }
        jsonParser.nextToken();
        return Result.success(deserializationContext.readValue(jsonParser, valueType));
    }
}
