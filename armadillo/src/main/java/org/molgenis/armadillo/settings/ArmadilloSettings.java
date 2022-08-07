package org.molgenis.armadillo.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class ArmadilloSettings {
  @JsonProperty("users")
  public abstract ConcurrentMap<String, UserDetails> getUsers();

  @JsonProperty("projects")
  public abstract ConcurrentMap<String, ProjectDetails> getProjects();

  @JsonProperty("permissions")
  public abstract Set<ProjectPermission> getPermissions();

  @JsonCreator
  public static ArmadilloSettings create() {
    return new AutoValue_ArmadilloSettings(
        new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>());
  }

  @JsonCreator
  public static ArmadilloSettings create(
      @JsonProperty("users") ConcurrentMap<String, UserDetails> newUsers,
      @JsonProperty("projects") ConcurrentMap<String, ProjectDetails> newProjects,
      @JsonProperty("permissions") Set<ProjectPermission> newPermissions) {
    return new AutoValue_ArmadilloSettings(newUsers, newProjects, newPermissions);
  }
}
