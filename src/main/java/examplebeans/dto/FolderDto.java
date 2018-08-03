package examplebeans.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderDto {
    @Getter
    @Setter
    private String directory;
}
