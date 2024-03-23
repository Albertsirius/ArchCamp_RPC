package org.galaxy.siriusrpc.demo.provider;

import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.demo.api.Order;
import org.galaxy.siriusrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * @author AlbertSirius
 * @since 2024/3/20
 */

@Component
@SiriusProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {

        if (id == 404) {
            throw new RuntimeException("404 exception");
        }

        return new Order(id.longValue(), 15.6f);
    }
}
