package com.csye6225_ChenniXu.healthcheck.config;

import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class MetricsConfig {

    @Bean
    @Profile("!test")
    public CloudWatchMeterRegistry cloudWatchMeterRegistry() {
        CloudWatchAsyncClient client = CloudWatchAsyncClient.builder().build();
        CloudWatchConfig config = new CloudWatchConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String namespace() {
                return "Springboot_Cloudwatch";
            }
        };

        return new CloudWatchMeterRegistry(config, Clock.SYSTEM, client);
    }

    @Bean
    @Profile("test")
    public MeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}

