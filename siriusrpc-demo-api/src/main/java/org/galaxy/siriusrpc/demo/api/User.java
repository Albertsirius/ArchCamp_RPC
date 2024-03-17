package org.galaxy.siriusrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    Integer id;
    String name;
}
