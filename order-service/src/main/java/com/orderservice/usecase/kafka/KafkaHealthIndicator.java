package com.orderservice.usecase.kafka;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(1000);
            DescribeClusterResult clusterResult = adminClient.describeCluster(options);
            KafkaFuture<Integer> nodeCountFuture = clusterResult.nodes().thenApply(Collection::size);

            int nodeCount = nodeCountFuture.get();
            if (nodeCount > 0) {
                return Health.up()
                    .withDetail("clusterId", clusterResult.clusterId().get())
                    .withDetail("nodeCount", nodeCount)
                    .build();
            } else {
                return Health.down()
                    .withDetail("reason", "No nodes available in Kafka cluster")
                    .build();
            }
        } catch (InterruptedException | ExecutionException e) {
            return Health.down(e).build();
        }
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return this.health();
    }

    public boolean isKafkaUp() {
        return this.health().getStatus().toString().equals("UP");
    }
}
