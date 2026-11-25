package de.ea234.securitydemo.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import de.ea234.securitydemo.security.user.entity.Benutzer;
import de.ea234.securitydemo.security.user.repository.BenutzerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("jpa")
class JpaSecurityIntegrationTest {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final String USER_PASSWORD = "user123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BenutzerRepository benutzerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        benutzerRepository.deleteAll();

        Benutzer admin = new Benutzer(
            "admin",
            passwordEncoder.encode(ADMIN_PASSWORD),
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        admin.setEnable(true);

        Benutzer user = new Benutzer(
            "demo",
            passwordEncoder.encode(USER_PASSWORD),
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        user.setEnable(true);

        benutzerRepository.saveAll(List.of(admin, user));
    }

    private MockHttpSession login(String username, String password)
            throws Exception {

        MvcResult result = mockMvc.perform(
                formLogin()
                    .user(username)
                    .password(password)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(authenticated().withUsername(username))
            .andReturn();

        MockHttpSession session =
            (MockHttpSession) result.getRequest().getSession(false);

        assertNotNull(session);

        return session;
    }

    @Test
    void adminLoginShouldSucceed() throws Exception {

        login("admin", ADMIN_PASSWORD);
    }

    @Test
    void userLoginShouldSucceed() throws Exception {

        login("demo", USER_PASSWORD);
    }

    @Test
    void adminShouldAccessAdminEndpoint() throws Exception {

        MockHttpSession session = login("admin", ADMIN_PASSWORD);

        mockMvc.perform(
                get("/api/security/test/admin")
                    .session(session)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void userShouldNotAccessAdminEndpoint() throws Exception {

        MockHttpSession session = login("demo", USER_PASSWORD);

        mockMvc.perform(
                get("/api/security/test/admin")
                    .session(session)
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void adminAndUserShouldAccessPingEndpoint() throws Exception {

        MockHttpSession adminSession = login("admin", ADMIN_PASSWORD);

        mockMvc.perform(
                get("/api/security/test/ping")
                    .session(adminSession)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));

        MockHttpSession userSession = login("demo", USER_PASSWORD);

        mockMvc.perform(
                get("/api/security/test/ping")
                    .session(userSession)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("demo"));
    }

    @Test
    void loginWithWrongPasswordShouldFail() throws Exception {

        mockMvc.perform(
                formLogin()
                    .user("admin")
                    .password("wrong-password")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(unauthenticated());
    }
}
