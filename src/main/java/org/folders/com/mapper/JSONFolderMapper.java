package org.folders.com.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.apache.log4j.Logger;
import org.folders.com.dto.JSONFolderDto;
import org.folders.com.model.JSONFolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JSONFolderMapper {
  Logger log = Logger.getLogger(JSONFolderMapper.class);

  JSONFolderMapper INSTANCE = Mappers.getMapper(JSONFolderMapper.class);

  @Mapping(target = "json",
      expression = "java(getJsonStringFromModel(jsonFolder))")
  JSONFolderDto jsonFolderToJsonFolderDto(JSONFolder jsonFolder);

  List<JSONFolderDto> jsonFoldersToJsonFolderDtos(List<JSONFolder> confirms);

  default String getJsonStringFromModel(JSONFolder folder){
    try {
      return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(folder);
    } catch (JsonProcessingException e) {
      log.error("Cannot create json.");
      return "";
    }
  }
}
