package Rateimiting.controller;

import Rateimiting.model.request.Request;
import Rateimiting.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

@RestController
@RequestMapping("/v1/")
public class RateLimitController {
    @Autowired
    private HttpServletRequest requests;

    private final RateLimitService rateLimitService;
    @Autowired
    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("rate/{id}")
    public ResponseEntity<?> getInfo(@PathVariable("id") String id){
        return rateLimitService.getInfo(id);
    }


    @PostMapping("/rate")
    public ResponseEntity<?> addInfo(@RequestBody Request request){
        String uri = requests.getRequestURI();
        String path = (String) requests.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    return rateLimitService.addInfo(request, path);
    }
}
