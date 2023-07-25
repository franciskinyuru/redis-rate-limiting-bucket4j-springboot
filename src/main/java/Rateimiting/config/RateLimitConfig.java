package Rateimiting.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class RateLimitConfig {
    @Autowired
    public ProxyManager buckets;

    public Bucket resolveBucket(String key,int tps){
        Supplier<BucketConfiguration> configurationSupplier = getConfigSupplier(key,tps);
        return buckets.builder().build(key,configurationSupplier);
    }
    private Supplier<BucketConfiguration> getConfigSupplier(String key, int tps){
        Refill refill=Refill.intervally(tps, Duration.ofSeconds(1));
        Bandwidth limit=Bandwidth.classic(tps,refill);

        return () -> (BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }
}
