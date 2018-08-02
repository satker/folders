package examplebeans.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderManagerDto {
    @Getter
    @Setter
    private String directory;
}
