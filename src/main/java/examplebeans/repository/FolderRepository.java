package examplebeans.repository;

import examplebeans.dao.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FolderRepository {
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;
    private boolean isASearchedFromFileSystem;
    private Integer idParent;

    public List<Folder> getAllDirectoriesFromFolder(String directory) {
        List<Folder> result = new ArrayList<>();
        extractDataFromDBorFileSystem(directory, result);
        if (result.size() != 0 && isASearchedFromFileSystem){
                        String sql = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)";
            result.stream().filter(Objects::nonNull).forEach(folder ->
                    jdbcTemplate.update(sql, new Object[] { folder.getDirectory().getAbsolutePath(),
                    idParent
            }));
        }
        return result;
    }

    private void extractDataFromDBorFileSystem(String directory, List<Folder> result) {
        String sqlForChilds = extractSqlForChildFolders(directory);
        if (sqlForChilds != null){
            getChildFoldersFromDatabase(result, sqlForChilds);
        } else {
            isASearchedFromFileSystem = true;
            result.addAll(currentDirectories(directory));
        }
    }

    private void getChildFoldersFromDatabase(List<Folder> result, String sqlForChilds) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlForChilds);
        for (Map row : rows) {
            Folder folder = new Folder();
            String name_folder = (String) (row.get("name_folder"));
            folder.setDirectory(new File(name_folder));
            result.add(folder);
        }
    }

    private String extractSqlForChildFolders(String directory) {
        try {
            idParent = getIdParent(directory);
            return "SELECT * FROM folder WHERE id_parent = " + idParent;
        } catch (EmptyResultDataAccessException | NullPointerException e){
            String sql = "INSERT INTO folder (name_folder) VALUES (?)";
            jdbcTemplate.update(sql, directory);
            idParent = getIdParent(directory);
            return null;
        }
    }

    private Integer getIdParent(String directory) {
        return jdbcTemplate.queryForObject(
                "(SELECT id FROM folder WHERE name_folder = ?)",
                new Object[]{directory}, Integer.class);
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
