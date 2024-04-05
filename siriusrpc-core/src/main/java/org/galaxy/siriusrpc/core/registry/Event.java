package org.galaxy.siriusrpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */

@Data
@AllArgsConstructor
public class Event {

    List<String> data;
}
