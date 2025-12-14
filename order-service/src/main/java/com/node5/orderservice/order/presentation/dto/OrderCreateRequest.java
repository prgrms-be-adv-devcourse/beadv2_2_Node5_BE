package com.node5.orderservice.order.presentation.dto;

import com.node5.orderservice.order.application.dto.OrderCommand;
import com.node5.orderservice.order.application.dto.OrderItemCommand;
import com.node5.orderservice.order.domain.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        @NotNull(message = "구매자 ID는 필수 값입니다.")
        UUID memberId,

        @NotNull(message = "주문 유형은 필수 값입니다.")
        OrderType orderType,

        UUID subscriptionId,

        String recipientName,
        String recipientAddress,

        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수 값입니다.")
            UUID productId,

            String name,
            String imgUrl,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal totalPrice
    ) {
    }

    public OrderCommand toCommand() {
        List<OrderItemCommand> itemCommands = this.items.stream()
                .map(item -> new OrderItemCommand(
                        item.productId,
                        item.name,
                        item.imgUrl,
                        item.unitPrice,
                        item.quantity,
                        item.totalPrice
                ))
                .toList();

        return new OrderCommand(
                memberId,
                orderType(),
                subscriptionId,
                recipientName,
                recipientAddress,
                itemCommands
        );
    }
}
