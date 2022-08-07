package org.molgenis.armadillo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.molgenis.armadillo.settings.ArmadilloSettingsService.SETTINGS_FILE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.armadillo.audit.AuditEventPublisher;
import org.molgenis.armadillo.settings.ArmadilloSettingsService;
import org.molgenis.armadillo.settings.ProjectDetails;
import org.molgenis.armadillo.settings.UserDetails;
import org.molgenis.armadillo.storage.ArmadilloStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SettingsController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Import({AuditEventPublisher.class})
public class SettingsControllerTest {
  public static final String EXAMPLE_SETTINGS =
      "{\"users\": {\"bofke@email.com\": {} }, \"projects\": {\"myproject\":{}}, \"permissions\": [{\"email\": \"bofke@email.com\", \"project\":\"myproject\"}]}";
  @MockBean private ArmadilloStorageService armadilloStorage;
  @Autowired AuditEventPublisher auditEventPublisher;
  @Autowired private MockMvc mockMvc;
  @MockBean JwtDecoder jwtDecoder;
  @Autowired ArmadilloSettingsService armadilloSettingsService;

  @BeforeEach
  void setup() {
    // default state
    when(armadilloStorage.loadSystemFile(SETTINGS_FILE))
        .thenReturn(new ByteArrayInputStream(EXAMPLE_SETTINGS.getBytes()));
    armadilloSettingsService.reload();
  }

  @Test
  @WithMockUser(roles = "SU")
  void settings_GET() throws Exception {
    mockMvc
        .perform(get("/settings"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(EXAMPLE_SETTINGS));
  }

  @Test
  @WithMockUser(roles = "SU")
  void permissions_POST() throws Exception {
    ArgumentCaptor<ByteArrayInputStream> argument =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    mockMvc
        .perform(
            post("/settings/permissions")
                .param("project", "myproject")
                .param("email", "chefke@email.com")
                .with(csrf()))
        .andExpect(status().isCreated());

    // verify mock magic, I must say I prefer integration tests above this nonsense
    verify(armadilloStorage)
        .saveSystemFile(argument.capture(), eq(SETTINGS_FILE), eq(APPLICATION_JSON));
    assertEquals(
        "{\"users\":{\"bofke@email.com\":{},\"chefke@email.com\":{}},\"projects\":{\"myproject\":{}},\"permissions\":[{\"email\":\"chefke@email.com\",\"project\":\"myproject\"},{\"email\":\"bofke@email.com\",\"project\":\"myproject\"}]}",
        new String(argument.getValue().readAllBytes()));
  }

  @Test
  @WithMockUser(roles = "SU")
  void permissions_DELETE() throws Exception {
    ArgumentCaptor<ByteArrayInputStream> argument =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    mockMvc
        .perform(
            delete("/settings/permissions")
                .param("email", "bofke@email.com")
                .param("project", "myproject")
                .with(csrf()))
        .andExpect(status().isOk());

    // verify mock magic, I must say I prefer integration tests above this nonsense
    verify(armadilloStorage)
        .saveSystemFile(argument.capture(), eq(SETTINGS_FILE), eq(APPLICATION_JSON));
    assertEquals(
        "{\"users\":{\"bofke@email.com\":{}},\"projects\":{\"myproject\":{}}}",
        new String(argument.getValue().readAllBytes()));
  }

  @Test
  @WithMockUser(roles = "SU")
  void settings_projects_GET() throws Exception {
    mockMvc
        .perform(get("/settings/projects"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("{\"myproject\":{\"users\":[\"bofke@email.com\"]}}"));
  }

  @Test
  @WithMockUser(roles = "SU")
  @WithUserDetails("bofke")
  void settings_projects_name_GET() throws Exception {
    mockMvc
        .perform(get("/settings/projects/myproject"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("{\"users\":[\"bofke@email.com\"]}"));
  }

  @Test
  @WithMockUser(roles = "SU")
  void projects_PUT() throws Exception {
    ArgumentCaptor<ByteArrayInputStream> argument =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    mockMvc
        .perform(
            put("/settings/projects/otherproject")
                .content(new Gson().toJson(ProjectDetails.create()))
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk());

    // verify mock magic, I must say I prefer integration tests above this nonsense
    verify(armadilloStorage)
        .saveSystemFile(argument.capture(), eq(SETTINGS_FILE), eq(APPLICATION_JSON));
    assertEquals(
        "{\"users\":{\"bofke@email.com\":{}},\"projects\":{\"otherproject\":{},\"myproject\":{}},\"permissions\":[{\"email\":\"bofke@email.com\",\"project\":\"myproject\"}]}",
        new String(argument.getValue().readAllBytes()));
  }

  @Test
  @WithMockUser
  void settings_projects_GET_PermissionDenied() throws Exception {
    mockMvc.perform(get("/settings/projects")).andExpect(status().is(403));
  }

  @Test
  @WithMockUser(roles = "SU")
  void users_GET() throws Exception {
    mockMvc
        .perform(get("/settings/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("{\"bofke@email.com\":{}}"));
  }

  @Test
  @WithMockUser(roles = "SU")
  void users_GET_byEmail() throws Exception {
    mockMvc
        .perform(get("/settings/users/bofke@email.com"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json("{}"));
  }

  @Test
  @WithMockUser(roles = "SU")
  void users_PUT() throws Exception {
    ArgumentCaptor<ByteArrayInputStream> argument =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    mockMvc
        .perform(
            put("/settings/users/chefke@email.com")
                .content(
                    new Gson().toJson(UserDetails.create("first", "last", "myInstitution", null)))
                .contentType(APPLICATION_JSON)
                .with(csrf()))
        .andExpect(status().isOk());

    // verify mock magic, I must say I prefer integration tests above this nonsense
    verify(armadilloStorage)
        .saveSystemFile(argument.capture(), eq(SETTINGS_FILE), eq(APPLICATION_JSON));
    assertEquals(
        "{\"users\":{\"bofke@email.com\":{},\"chefke@email.com\":{\"firstName\":\"first\",\"lastName\":\"last\",\"institution\":\"myInstitution\"}},\"projects\":{\"myproject\":{}},\"permissions\":[{\"email\":\"bofke@email.com\",\"project\":\"myproject\"}]}",
        new String(argument.getValue().readAllBytes()));
  }

  @Test
  @WithMockUser(roles = "SU")
  void users_email_DELETE() throws Exception {
    ArgumentCaptor<ByteArrayInputStream> argument =
        ArgumentCaptor.forClass(ByteArrayInputStream.class);
    mockMvc
        .perform(delete("/settings/users/bofke@email.com").with(csrf()))
        .andExpect(status().isOk());

    // verify mock magic, I must say I prefer integration tests above this nonsense
    verify(armadilloStorage)
        .saveSystemFile(argument.capture(), eq(SETTINGS_FILE), eq(APPLICATION_JSON));
    assertEquals(
        "{\"projects\":{\"myproject\":{}}}", new String(argument.getValue().readAllBytes()));
  }
}
