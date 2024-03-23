package org.galaxy.siriusrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AlbertSirius
 * @since 2024/3/20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    Long id;
    Float amout;
}
