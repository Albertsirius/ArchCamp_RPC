package org.galaxy.siriusrpc.core.cluster;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 灰度路由
 *
 * @author AlbertSirius
 * @since 2024/6/2
 */
@Slf4j
@Data
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;
    private final Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> rout(List<InstanceMeta> providers) {
        if (Objects.isNull(providers) || providers.size() <= 1) {
            return providers;
        }
        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();
        providers.forEach(p -> {
            if ("true".equals(p.getParameters().get("gray"))) {
                grayNodes.add(p);
            }else {
                normalNodes.add(p);
            }
        });
        if (normalNodes.isEmpty() || grayNodes.isEmpty()) return providers;
        if (grayRatio <= 0) {
            return normalNodes;
        }else if (grayRatio >= 100) {
            return grayNodes;
        }
        // 假设LB的算法一定是线性均匀分布
        if (random.nextInt(100) < grayRatio) {
            return grayNodes;
        }else {
            return normalNodes;
        }
    }
}
