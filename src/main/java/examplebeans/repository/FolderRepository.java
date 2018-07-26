package examplebeans.repository;

import examplebeans.dao.Folder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FolderRepository {
    @Autowired
    public void setSearchingInDB(SearchingInDB searchingInDB) {
        this.searchingInDB = searchingInDB;
    }

    private SearchingInDB searchingInDB;

    private boolean isASearchedFromFileSystem;

    public List<Folder> getAllDirectoriesFromFolder(String directory) {
//        List<Folder> result = new ArrayList<>();
//        extractDataFromDBorFileSystem(directory, result);
//        if (result.size() != 0 && isASearchedFromFileSystem) {
//            searchingInDB.writeAllFoundedDirectoriesIntoDB(result);
//        }
//        isASearchedFromFileSystem = false;
//        return result;
        return currentDirectories(directory);
    }

    private void extractDataFromDBorFileSystem(String directory, List<Folder> result) {
        if (searchingInDB.isFolderPresentInDB(directory)) {
            String sqlForChilds = searchingInDB.extractSqlForChildFolders(directory);
            searchingInDB.getChildFoldersFromDatabase(result, sqlForChilds);
        } else {
            isASearchedFromFileSystem = true;
            result.addAll(currentDirectories(directory));
        }
    }

    // Возвращает список директорий в папке
    private List<Folder> currentDirectories(String path) {
        List<Folder> result = null;

        // Список файлов текущей директории
        String[] currentFiles = new File(path).list();
        if (currentFiles != null) {
            result = Arrays.stream(currentFiles)
                    .map(fileOrDirectoryName ->
                            getFileFromFullNameFileOrDirectory(path, fileOrDirectoryName))
                    .filter(File::isDirectory)
                    .map(directory -> Folder.builder().directory(new File(directory.getAbsolutePath())).build())
                    .collect(Collectors.toList());
        }
        return result;
    }

    private File getFileFromFullNameFileOrDirectory(String fileOrDirectoryPath,
                                                    String fileOrDirectoryName) {
        String path = fileOrDirectoryPath + "\\" + fileOrDirectoryName;
        return new File(path);
    }
}
