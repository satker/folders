package examplebeans.service;

import examplebeans.dto.FolderManagerDto;
import java.util.Set;

public interface JSONFolderService {
  String getJSONChildesFromParentDirectory(Set<String> stringCollectionFromFolder);
}
