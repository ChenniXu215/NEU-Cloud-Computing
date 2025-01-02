import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestMetricsConfig {

    @Bean
    public SimpleMeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}

