package examplebeans.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JSONFolderDto {

    @Getter
    @Setter
    private String json;
}
