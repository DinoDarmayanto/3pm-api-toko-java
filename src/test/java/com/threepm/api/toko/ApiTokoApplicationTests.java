package com.threepm.api.toko;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:api_toko_context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.security.jwt-secret=3pm-backend-test-secret-key-minimum-32-characters",
        "app.security.jwt-expiration-ms=86400000",
        "app.security.permit-all=true"
})
class ApiTokoApplicationTests {

    @Test
    void contextLoads() {
    }
}