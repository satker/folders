package examplebeans.mapper;

import examplebeans.dto.FolderDto;
import examplebeans.model.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface FolderMapper {
    FolderMapper INSTANCE = Mappers.getMapper( FolderMapper.class );

    @Mapping(target = "directory",
            expression = "java(folder.getDirectory().getAbsolutePath())")
    FolderDto folderToFolderDto(Folder folder);

    @Mapping(target = "directory",
            expression = "java(new java.io.File(folderDto.getDirectory()))")
    Folder folderDtoToFolder(FolderDto folderDto);

    List<FolderDto> foldersToFolderDTOs(List<Folder> confirms);
    List<Folder> folderDTOsToFolder(List<FolderDto> confirmDTO);
}
