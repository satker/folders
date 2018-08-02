package examplebeans.mapper;

import examplebeans.dto.FolderManagerDto;
import examplebeans.model.FolderManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface FolderManagerMapper {
    FolderManagerMapper INSTANCE = Mappers.getMapper( FolderManagerMapper.class );

    @Mapping(target = "directory",
            expression = "java(folder.getDirectory().getAbsolutePath())")
    FolderManagerDto folderToFolderDto(FolderManager folder);

    @Mapping(target = "directory",
            expression = "java(new java.io.File(folderDto.getDirectory()))")
    FolderManager folderDtoToFolder(FolderManagerDto folderDto);

    Set<FolderManagerDto> foldersToFolderDTOs(Set<FolderManager> confirms);
    Set<FolderManager> folderDTOsToFolder(Set<FolderManagerDto> confirmDTO);
}
