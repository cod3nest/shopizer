package com.salesmanager.core.business.services.order.orderproduct;

import com.salesmanager.core.business.repositories.order.orderproduct.OrderProductDownloadRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
class OrderProductDownloadServiceImpl extends SalesManagerEntityServiceImpl<Long, OrderProductDownload> implements OrderProductDownloadService {

    private final OrderProductDownloadRepository orderProductDownloadRepository;

    public OrderProductDownloadServiceImpl(OrderProductDownloadRepository orderProductDownloadRepository) {
        super(orderProductDownloadRepository);
        this.orderProductDownloadRepository = orderProductDownloadRepository;
    }

    @Override
    public List<OrderProductDownload> getByOrderId(Long orderId) {
        return orderProductDownloadRepository.findByOrderId(orderId);
    }


}
