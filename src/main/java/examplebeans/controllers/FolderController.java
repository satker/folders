package examplebeans.controllers;

import examplebeans.dao.Folder;
import examplebeans.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FolderController {
    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    private FolderService folderService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView getHello() {
        List<Folder> allForFolder = folderService.getAllForFolder(null);
        List<String> collect = allForFolder.stream().
                map(Folder::getDirectory).
                map(File::getName).
                map(folder -> folder.split("\\\\")).
                map(flatFolder -> flatFolder[flatFolder.length - 1]).
                collect(Collectors.toList());
        ModelAndView model = new ModelAndView("WEB-INF/jsp/index.jsp");
        model.addObject("list", collect);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{text}")
    public String getNextFolder(@PathVariable() String text) {
        String folder1 = text.replaceAll("-", "\\\\");
        System.out.println(folder1);
        List<Folder> allForFolder = folderService.getAllForFolder(folder1);
        if (allForFolder != null) {
            String json = allForFolder.stream().
                    map(Folder::getDirectory).
                    map(File::getName).
                    map(folder -> folder.split("\\\\")).
                    map(flatFolder -> flatFolder[flatFolder.length - 1]).
                    map(folder -> jsonPart1.concat(folder).concat(jsonPart2)).
                    collect(Collectors.joining(", "));
            return "[" + json + "]";
        } else {
            return "";
        }
    }

    String jsonPart1 = "{\"isActive\":false," +
            "\"isFolder\":true," +
            "\"isExpanded\":false," +
            "\"isLazy\":true," +
            "\"iconUrl\":null," +
            "\"id\":null," +
            "\"href\":null," +
            "\"hrefTarget\":null," +
            "\"lazyUrl\":null," +
            "\"lazyUrlJson\":null," +
            "\"liClass\":null," +
            "\"text\":\"";
    String jsonPart2 = " \"," +
            "\"textCss\":null," +
            "\"tooltip\":null," +
            "\"uiIcon\":null," +
            "\"children\":null}";
}
