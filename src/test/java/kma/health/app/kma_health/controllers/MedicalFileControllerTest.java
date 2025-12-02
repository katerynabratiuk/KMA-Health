package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.MedicalTestsTemplateGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicalFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalTestsTemplateGeneratorService testGenerator;

    @MockitoBean
    private JwtUtils jwtUtils;

    @TempDir
    Path tempDir;

    @Test
    @WithMockUser(roles = "LAB_ASSISTANT")
    void testDownloadBoilerplate_Success() throws Exception {
        File testFile = tempDir.resolve("test.xlsx").toFile();
        testFile.createNewFile();

        when(testGenerator.generateTemplate(anyList(), anyString()))
                .thenReturn(testFile.getAbsolutePath());

        mockMvc.perform(get("/api/files/template")
                        .param("testIds", "1", "2")
                        .param("patientName", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.xlsx\""));
    }

    @Test
    @WithMockUser(roles = "LAB_ASSISTANT")
    void testDownloadBoilerplate_FileNotFound() throws Exception {
        when(testGenerator.generateTemplate(anyList(), anyString()))
                .thenReturn("/nonexistent/path/file.xlsx");

        mockMvc.perform(get("/api/files/template")
                        .param("testIds", "1")
                        .param("patientName", "John Doe"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testDownloadBoilerplate_Forbidden() throws Exception {
        mockMvc.perform(get("/api/files/template")
                        .param("testIds", "1")
                        .param("patientName", "John Doe"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDownloadBoilerplate_ForbiddenForDoctor() throws Exception {
        mockMvc.perform(get("/api/files/template")
                        .param("testIds", "1")
                        .param("patientName", "John Doe"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDownloadBoilerplate_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/files/template")
                        .param("testIds", "1")
                        .param("patientName", "John Doe"))
                .andExpect(status().isForbidden());
    }
}

