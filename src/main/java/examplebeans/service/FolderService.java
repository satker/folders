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
        String basicFolder = "D:\\java_projects\\folders\\src\\main\\resources\\examplefolders";
        String directory = folder == null ? basicFolder : basicFolder + "\\" + folder;
        System.out.println(directory);
        return folderRepository.getAllDirectoriesFromFolder(directory);
    }
}
