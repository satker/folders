package examplebeans.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
    private int id;
    private File directory;
    private int idParent;
}
