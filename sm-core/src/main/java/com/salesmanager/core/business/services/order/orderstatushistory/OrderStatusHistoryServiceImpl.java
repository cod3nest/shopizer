package com.salesmanager.core.business.services.order.orderstatushistory;

import com.salesmanager.core.business.repositories.order.OrderStatusHistoryRepository;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    public List<OrderStatusHistory> findByOrder(Order order) {
        return orderStatusHistoryRepository.findByOrderId(order.getId());
    }
}
