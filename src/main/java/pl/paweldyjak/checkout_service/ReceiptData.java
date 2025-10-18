package pl.paweldyjak.checkout_service;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReceiptData {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentDate;

    private BigDecimal priceWithoutDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal finalPrice;
    private List<ReceiptLineData> receiptLines = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptLineData {
        private String itemName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String discountsApplied;
        private BigDecimal originalPrice;
        private BigDecimal finalPrice;
    }

}
