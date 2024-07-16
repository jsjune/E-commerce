package com.payment.paymentcore.infrastructure.kafka;

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
        int retries = 3; // 재시도 횟수
        int timeoutMs = 5000; // 타임아웃 설정을 5초로 증가

        for (int i = 0; i < retries; i++) {
            try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
                DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(timeoutMs);
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
                if (i == retries - 1) {
                    return Health.down(e).build(); // 재시도 후에도 실패하면 에러 반환
                }
                try {
                    Thread.sleep(1000); // 재시도 전 1초 대기
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return Health.down()
            .withDetail("reason", "Failed to connect to Kafka cluster after retries")
            .build();
    }

    @Override
    public Health getHealth(boolean includeDetails) {
        return this.health();
    }

    public boolean isKafkaUp() {
        return this.health().getStatus().toString().equals("UP");
    }
}
