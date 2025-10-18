package pl.paweldyjak.checkout_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ReceiptDataConverter implements AttributeConverter<ReceiptData, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(ReceiptData receipt) {
        if (receipt == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(receipt);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting ReceiptData to JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public ReceiptData convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, ReceiptData.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error parsing ReceiptData from JSON: " + e.getMessage(), e);
        }
    }
}
