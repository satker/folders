package examplebeans.service.impl;

import examplebeans.dto.JSONFolderDto;
import examplebeans.mapper.JSONFolderMapper;
import examplebeans.model.JSONFolder;
import examplebeans.service.JSONFolderService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class JSONFolderServiceImpl implements JSONFolderService {

  public String getJSONChildesFromParentDirectory(List<String> stringCollectionFromFolder) {
    List<JSONFolder> jsonFromStringFolders = getJSONFromStringFolders(
        stringCollectionFromFolder);
    List<JSONFolderDto> jsonFolderDtos = JSONFolderMapper.INSTANCE.jsonFoldersToJsonFolderDtos(
        jsonFromStringFolders);
    return getFormedStringFromListJSON(jsonFolderDtos);
  }

  private List<JSONFolder> getJSONFromStringFolders(List<String> allForFolder) {
    return allForFolder.stream()
                       .map(folder -> JSONFolder.builder()
                                                .isActive(false)
                                                //.enableDnd(true)
                                                .isFolder(true)
                                                .isExpanded(false)
                                                .isLazy(true)
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
