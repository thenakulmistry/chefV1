package com.chef.V1.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Import JavaTimeModule
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    public static class ObjectIdSerializer extends JsonSerializer<ObjectId> {
        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toHexString());
            }
        }
    }

    public static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String objectIdString = null;
            if (node.isTextual()) {
                objectIdString = node.asText();
            } else if (node.isObject() && node.has("$oid")) { // Handle cases where it might still come as {$oid: "..."}
                objectIdString = node.get("$oid").asText();
            }
            
            if (objectIdString != null && ObjectId.isValid(objectIdString)) {
                return new ObjectId(objectIdString);
            }
            // Allow null if the input is null or empty string, or handle as error
            if (objectIdString == null || objectIdString.isEmpty()) return null; 
            
            throw new IOException("Invalid ObjectId format: " + node.toString());
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        SimpleModule objectIdModule = new SimpleModule("ObjectIdModule");
        objectIdModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
        objectIdModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());

        // Create Jackson2ObjectMapperBuilder and register modules
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(objectIdModule, new JavaTimeModule()); // Add JavaTimeModule here

        // Optional: Disable writing dates as timestamps if you prefer ISO-8601 strings
        // builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return builder;
    }
}
