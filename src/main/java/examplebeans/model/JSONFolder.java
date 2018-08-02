package examplebeans.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JSONFolder {
    private boolean isActive = false;
    private boolean enableDnd = true;
    private boolean isFolder = true;
    private boolean isExpanded = false;
    private boolean isLazy = true;
    private boolean iconUrl;
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

    @JsonProperty
    public boolean isActive() {
        return isActive;
    }

    @JsonProperty
    public boolean isEnableDnd() {
        return enableDnd;
    }

    @JsonProperty
    public boolean isFolder() {
        return isFolder;
    }

    @JsonProperty
    public boolean isExpanded() {
        return isExpanded;
    }

    @JsonProperty
    public boolean isLazy() {
        return isLazy;
    }

    @JsonProperty
    public boolean isIconUrl() {
        return iconUrl;
    }

    public String getId() {
        return id;
    }

    @JsonProperty
    public String getHref() {
        return href;
    }

    @JsonProperty
    public String getHrefTarget() {
        return hrefTarget;
    }

    @JsonProperty
    public String getLazyUrl() {
        return lazyUrl;
    }

    @JsonProperty
    public String getLazyUrlJson() {
        return lazyUrlJson;
    }

    @JsonProperty
    public String getLiClass() {
        return liClass;
    }

    @JsonProperty
    public String getText() {
        return text;
    }

    @JsonProperty
    public String getTextCss() {
        return textCss;
    }

    @JsonProperty
    public String getTooltip() {
        return tooltip;
    }

    @JsonProperty
    public String getUiIcon() {
        return uiIcon;
    }

    @JsonProperty
    public String getChildren() {
        return children;
    }
}
