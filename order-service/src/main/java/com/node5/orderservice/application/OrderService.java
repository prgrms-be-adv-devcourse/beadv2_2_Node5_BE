package com.node5.orderservice.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.orderservice.application.dto.OrderCommand;
import com.node5.orderservice.application.dto.OrderCreateInfo;
import com.node5.orderservice.application.dto.OrderItemCommand;
import com.node5.orderservice.domain.Order;
import com.node5.orderservice.domain.OrderItem;
import com.node5.orderservice.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    public ResponseEntity<ApiResponseDto<OrderCreateInfo>> create(OrderCommand command){
        // Order 생성
        String orderNum = generateNewOrderNum();
        int totalAmount = command.items().stream()
                .mapToInt(OrderItemCommand::totalPrice)
                .sum();

        Order order = Order.create(command, orderNum, totalAmount);
        Order saved = orderRepository.save(order);

        // OrderItem 생성
        UUID orderId = saved.getId();
        List<OrderItemCommand> itemCommands = command.items();
        List<OrderItem> orderItems = itemCommands.stream()
                .map(oi -> OrderItem.create(orderId, oi))
                .toList();
        orderItemService.saveAll(orderItems);

        // TODO 결제 API 호출
        // - 결제 성공 시: (재고 차감) -> 주문 상태 PAID로 변경
        // - 결제 실패 시: 주문 상태 PAYMENT_FAILED로 변경 후 실패 응답

        ApiResponseDto<OrderCreateInfo> responseDto = new ApiResponseDto<>(HttpStatus.CREATED.value(), "주문 생성 성공", OrderCreateInfo.from(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    public String generateNewOrderNum() {
        Long nextSequenceValue = orderRepository.getNextSequenceNum();

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequencePart = String.format("%08d", nextSequenceValue);

        return datePart + "-" + sequencePart;
    }
}
