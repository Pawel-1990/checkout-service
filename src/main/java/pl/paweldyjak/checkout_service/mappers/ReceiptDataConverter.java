package pl.paweldyjak.checkout_service.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pl.paweldyjak.checkout_service.dtos.response.ReceiptResponseDto;

@Converter(autoApply = true)
public class ReceiptDataConverter implements AttributeConverter<ReceiptResponseDto, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ReceiptResponseDto attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert ReceiptResponse to JSON", e);
        }
    }

    @Override
    public ReceiptResponseDto convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ReceiptResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert JSON to ReceiptResponse", e);
        }
    }
}
