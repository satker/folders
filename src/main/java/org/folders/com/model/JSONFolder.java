package org.folders.com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonPropertyOrder
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JSONFolder {

  private boolean isActive;
  private boolean isFolder;
  private boolean isExpanded;
  private boolean isLazy;
  private String iconUrl;
  private String id;
  private String href;
  private String hrefTarget;
  private String lazyUrl;
  private String lazyUrlJson;
  private String liClass;
  @NonNull
  private String text;
  private String textCss;
  private String tooltip;
  private String uiIcon;
  private String children;

  @JsonProperty("isActive")
  public boolean isActive() {
    return isActive;
  }

  @JsonProperty("isFolder")
  public boolean isFolder() {
    return isFolder;
  }

  @JsonProperty("isExpanded")
  public boolean isExpanded() {
    return isExpanded;
  }

  @JsonProperty("isLazy")
  public boolean isLazy() {
    return isLazy;
  }

  @JsonProperty("iconUrl")
  public String isIconUrl() {
    return iconUrl;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("href")
  public String getHref() {
    return href;
  }

  @JsonProperty("hrefTarget")
  public String getHrefTarget() {
    return hrefTarget;
  }

  @JsonProperty("lazyUrl")
  public String getLazyUrl() {
    return lazyUrl;
  }

  @JsonProperty("lazyUrlJson")
  public String getLazyUrlJson() {
    return lazyUrlJson;
  }

  @JsonProperty("liClass")
  public String getLiClass() {
    return liClass;
  }

  @JsonProperty("text")
  public String getText() {
    return text;
  }

  @JsonProperty("textCss")
  public String getTextCss() {
    return textCss;
  }

  @JsonProperty("tooltip")
  public String getTooltip() {
    return tooltip;
  }

  @JsonProperty("uiIcon")
  public String getUiIcon() {
    return uiIcon;
  }

  @JsonProperty("children")
  public String getChildren() {
    return children;
  }
}
