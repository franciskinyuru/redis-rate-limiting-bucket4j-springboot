package Rateimiting.service;

import Rateimiting.config.RateLimitConfig;
import Rateimiting.model.ApiResponse;
import Rateimiting.model.TpsDb;
import Rateimiting.model.request.Request;
import Rateimiting.repository.TpsDbRepository;
import io.github.bucket4j.Bucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
    int tps;
    @Autowired
    private RedissonClient redissonClient;
    private final TpsDbRepository tpsD;

    private final RateLimitConfig rateLimitConfig;
    @Autowired
    public RateLimitService(TpsDbRepository tpsD, RateLimitConfig rateLimitConfig) {
        this.tpsD = tpsD;
        this.rateLimitConfig = rateLimitConfig;
    }

    public ResponseEntity<?> getInfo(String id){

        Bucket bucket= rateLimitConfig.resolveBucket(id,10);
        if (bucket.tryConsume(1)){
            return ResponseEntity.status(200).body(new ApiResponse("Request Success for user " + id,"4000","success"));
        }else{
            return ResponseEntity.status(429).body(new ApiResponse("Request failed for user " + id,"4003","failed"));
        }
    }

    public ResponseEntity<?> addInfo(Request request, String path) {
        String username = request.getUsername();
        // Check if the TpsDb is cached in Redis
        RMapCache<String, TpsDb> cache = redissonClient.getMapCache("tpsDbCache");
        TpsDb tpsDb = cache.get(username + "-" + path);

        if (tpsDb == null) {
            tpsDb = tpsD.findByUsernameAndPath(path, username);

            if (tpsDb == null) {
                tpsDb = new TpsDb();
                tpsDb.setUsername(username);
                tpsDb.setPath(path);
                tpsDb.setTps(10); // Default TPS value if not found in the database
            }
            cache.put(username + "-" + path, tpsDb);
        }


        int tps = tpsDb.getTps();
        Bucket bucket = rateLimitConfig.resolveBucket(username, tps);
        if (bucket.tryConsume(1)) {
            return ResponseEntity.status(200).body(new ApiResponse("Request Success for user " + username, "4000", "success"));
        } else {
            return ResponseEntity.status(429).body(new ApiResponse("Request failed for user " + username, "4003", "failed"));
        }
    }
}
