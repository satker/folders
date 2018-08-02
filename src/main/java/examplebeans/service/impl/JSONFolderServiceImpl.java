package examplebeans.service.impl;

import examplebeans.dto.JSONFolderDto;
import examplebeans.mapper.JSONFolderMapper;
import examplebeans.model.JSONFolder;
import examplebeans.service.JSONFolderService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class JSONFolderServiceImpl implements JSONFolderService {

  public String getJSONChildesFromParentDirectory(Set<String> stringCollectionFromFolder) {
    List<JSONFolder> jsonFromStringFolders = getJSONFromStringFolders(
        stringCollectionFromFolder);
    List<JSONFolderDto> jsonFolderDtos = JSONFolderMapper.INSTANCE.jsonFoldersToJsonFolderDtos(
        jsonFromStringFolders);
    return getFormedStringFromListJSON(jsonFolderDtos);
  }

  private List<JSONFolder> getJSONFromStringFolders(Set<String> allForFolder) {
    return allForFolder.stream()
                       .map(folder -> JSONFolder.builder()
                                                .text(folder)
                                                .build())
                       .collect(Collectors.toList());
  }

  private String getFormedStringFromListJSON(List<JSONFolderDto> jsonFolderDtos) {
    return jsonFolderDtos.stream()
                         .map(JSONFolderDto::getJson)
                         .collect(Collectors.joining(",", "[", "]"));
  }
}
