package examplebeans.repository;

import examplebeans.dao.Folder;
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
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate jdbcTemplate;
    private boolean isASearchedFromFileSystem;
    private Integer idParent;

    public List<Folder> getAllDirectoriesFromFolder(String directory) {
        List<Folder> result = new ArrayList<>();
        extractDataFromDBorFileSystem(directory, result);
        if (result.size() != 0 && isASearchedFromFileSystem) {
            String sql = "INSERT INTO folder (name_folder, id_parent) VALUES (?, ?)";
            result.stream().filter(Objects::nonNull).forEach(folder ->
                    jdbcTemplate.update(sql, new Object[]{folder.getDirectory().getAbsolutePath(),
                            idParent
                    }));
        }
        return result;
    }

    private void createTable() {
        String sql = "CREATE TABLE folder ( " +
                " id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                " name_folder VARCHAR(255) NOT NULL, " +
                " id_parent INTEGER);" +
                " CREATE INDEX idx_folder__id_parent ON folder (id_parent);" +
                "ALTER TABLE folder ADD CONSTRAINT fk_folder__id_parent FOREIGN KEY (id_parent) REFERENCES folder (id)";
        jdbcTemplate.execute(sql);
    }

    private void extractDataFromDBorFileSystem(String directory, List<Folder> result) {
        String sqlForChilds = extractSqlForChildFolders(directory);
        if (sqlForChilds != null) {
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
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            String sql = "INSERT INTO folder (name_folder) VALUES (?)";
            jdbcTemplate.update(sql, directory);
            idParent = getIdParent(directory);
            return null;
        }
    }

    private Integer getIdParent(String directory) {
        Integer result;
        try {
            result = jdbcTemplate.queryForObject(
                    "(SELECT id FROM folder WHERE name_folder = ?)",
                    new Object[]{directory}, Integer.class);
        } catch (BadSqlGrammarException e){
            createTable();
            result = jdbcTemplate.queryForObject(
                    "(SELECT id FROM folder WHERE name_folder = ?)",
                    new Object[]{directory}, Integer.class);
        }
        return result;
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
