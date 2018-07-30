package examplebeans.service;

import examplebeans.dao.Folder;
import examplebeans.repository.FolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FolderService {
    private FolderRepository folderRepository;

    public List<Folder> getAllForFolder(String folder){
        String basicFolder = "C:\\Users\\Artem_Kunats\\IdeaProjects";
        String directory = folder == null ? basicFolder : basicFolder + "\\" + folder;
        return folderRepository.getAllDirectoriesFromFolder(directory);
    }
}
