package com.node5.orderservice.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedApiResponseDto;
import com.node5.orderservice.application.dto.*;
import com.node5.orderservice.domain.Order;
import com.node5.orderservice.domain.OrderItem;
import com.node5.orderservice.domain.OrderItemRepository;
import com.node5.orderservice.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;

    public ResponseEntity<ApiResponseDto<OrderCreateInfo>> create(OrderCommand command) {
        // Order 생성
        String orderNum = generateNewOrderNum();
        Optional<BigDecimal> totalAmountOptional = command.items().stream()
                .map(OrderItemCommand::totalPrice)
                .reduce(BigDecimal::add);
        BigDecimal totalAmount = totalAmountOptional.orElse(BigDecimal.ZERO);

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

        ApiResponseDto<OrderCreateInfo> responseDto = new ApiResponseDto<>(HttpStatus.CREATED.value(), "주문 생성 성공", OrderCreateInfo.from(saved));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    public ResponseEntity<PagedApiResponseDto<OrderListInfo>> getOrderList(UUID memberId, int page, int size, String period) {

        // 오늘 기준 n개월(period) 전 시점 구하기
        LocalDateTime nMonthsAgo = LocalDateTime.now().minusMonths(Integer.parseInt(period));

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);

        // nMonthsAgo 이후 최근순 조회
        Page<Order> orderPage = orderRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(memberId, nMonthsAgo, pageable);
        PageInfoDto pageInfo = new PageInfoDto(orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages());

        // 페이징 결과 처리
        if(!orderPage.hasContent()){
            return ResponseEntity.ok().body(new PagedApiResponseDto<>(HttpStatus.OK.value(), "주문 목록 조회 성공", Collections.emptyList(), pageInfo));
        }

        // - 주문 ID 목록 추출
        List<UUID> orderIds =  orderPage.getContent().stream()
                .map(Order::getId)
                .toList();

        // - 추출된 주문 ID 목록으로 모든 주문 상품 내역 조회
        List<OrderItem> orderedItems = orderItemRepository.findByOrderIdIn(orderIds);

        // - 데이터를 주문 ID별로 그룹핑
        Map<UUID, List<OrderItem>> orderGroup = orderedItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // - OrderListInfo DTO 생성
        List<OrderListInfo> orderListInfos = orderPage.getContent().stream()
                .map(order -> {
                    List<OrderItem> orderItemList = orderGroup.get(order.getId());
                    List<OrderItemInfo> orderedItemInfos = orderItemList.stream()
                            .map(OrderItemInfo::from)
                            .toList();
            return OrderListInfo.from(order, orderedItemInfos);
        }).toList();

        PagedApiResponseDto<OrderListInfo> responseDto = new PagedApiResponseDto<>(HttpStatus.OK.value(), "주문 목록 조회 성공", orderListInfos, pageInfo);
        return ResponseEntity.ok().body(responseDto);
    }

    public ResponseEntity<ApiResponseDto<OrderDetailInfo>> getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<OrderItemInfo> orderItemInfos = orderItems.stream()
                .map(OrderItemInfo::from)
                .toList();

        ApiResponseDto<OrderDetailInfo> responseDto = new ApiResponseDto<>(HttpStatus.OK.value(), "주문 상세 조회 성공", OrderDetailInfo.from(order, orderItemInfos));
        return ResponseEntity.ok().body(responseDto);
    }

    public String generateNewOrderNum() {
        Long nextSequenceValue = orderRepository.getNextSequenceNum();

        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String sequencePart = String.format("%08d", nextSequenceValue);

        return datePart + "-" + sequencePart;
    }

}
