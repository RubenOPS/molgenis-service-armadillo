package org.molgenis.armadillo.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import jakarta.validation.constraints.NotEmpty;

@AutoValue
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class FileDetails {

  @JsonProperty("id")
  @NotEmpty
  public abstract String getId();

  @JsonProperty("name")
  @NotEmpty
  public abstract String getName();

  @JsonProperty("content")
  @NotEmpty
  public abstract String getContent();

  @JsonProperty("fetched")
  @NotEmpty
  public abstract String getFetched();

  @JsonCreator
  public static FileDetails create(
      @JsonProperty("id") String newId,
      @JsonProperty("name") String newName,
      @JsonProperty("content") String newContent,
      @JsonProperty("fetched") String newFetched) {
    return new AutoValue_FileDetails(newId, newName, newContent, newFetched);
  }
}
