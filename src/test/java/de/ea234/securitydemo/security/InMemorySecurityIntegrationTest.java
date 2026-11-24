package de.ea234.securitydemo.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inmemory")
class InMemorySecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    void adminShouldAccessPing() throws Exception {

        MockHttpSession session = login("admin", "admin");

        mockMvc.perform(get("/api/security/test/ping")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void adminShouldAccessAdminEndpoint() throws Exception {

        MockHttpSession session = login("admin", "admin");

        mockMvc.perform(get("/api/security/test/admin")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void demoShouldAccessPing() throws Exception {

        MockHttpSession session = login("demo", "demo");

        mockMvc.perform(get("/api/security/test/ping")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("demo"));
    }

    @Test
    void demoShouldNotAccessAdminEndpoint() throws Exception {

        MockHttpSession session = login("demo", "demo");

        mockMvc.perform(get("/api/security/test/admin")
                .session(session))
            .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserShouldBeRedirectedToLogin()
            throws Exception {

        mockMvc.perform(get("/api/security/test/ping"))
            .andExpect(status().is3xxRedirection());
    }
}
