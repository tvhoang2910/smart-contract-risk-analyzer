package vn.techmaster.nowj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test")
public class TestJwtController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint - no JWT required!");
    }

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName() + "! JWT is working correctly.");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminEndpoint(Principal principal) {
        return ResponseEntity.ok("Hello Admin " + principal.getName() + "!");
    }
}
