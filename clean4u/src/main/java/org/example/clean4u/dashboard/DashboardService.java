package org.example.clean4u.dashboard;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
}
