package com.example.studentportal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentportalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Student Portal")));
    }

    @Test
    void testVersion1Endpoint() throws Exception {
        mockMvc.perform(get("/v1"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("STUDENT PORTAL")))
            .andExpect(content().string(containsString("VERSION 1")))
            .andExpect(content().string(containsString("Azure App Service Deployment Slots")))
            .andExpect(content().string(containsString("Azure App Service Deployment Project")));
    }

    @Test
    void testVersion2Endpoint() throws Exception {
        mockMvc.perform(get("/v2"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("STUDENT PORTAL")))
            .andExpect(content().string(containsString("VERSION 2")))
            .andExpect(content().string(containsString("DEPLOYMENT VALIDATED")))
            .andExpect(content().string(containsString("NEW RELEASE")))
            .andExpect(content().string(containsString("Student Information Directory")));
    }

    @Test
    void testDiagnosticStatusEndpoint() throws Exception {
        mockMvc.perform(get("/api/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.swapValidation").value("READY"));
    }

    @Test
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }
}
