package examplebeans.controllers;

import examplebeans.dao.Folder;
import examplebeans.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

@RestController
public class FolderController {
    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    private FolderService folderService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView getFirstFolders() {
        Set<Folder> allForFolder = folderService.getAllForFolder(null);
        Set<String> collect = folderService.getStringCollectionFromFolder(allForFolder);
        ModelAndView model = new ModelAndView("WEB-INF/jsp/index.jsp");
        model.addObject("list", collect);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{folder}")
    public String getNextFolders(@PathVariable() String folder) {
        Set<Folder> allForFolder = folderService.getAllForFolder(folder);
        return folderService.getJSONChildesFromParentDirectory(allForFolder);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{folder}")
    public void removeFolder(@PathVariable() String folder) {
        folderService.removeNode(folder);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{from}/{to}")
    public void moveFolder(@PathVariable(value = "from") String from,
                       @PathVariable(value = "to") String to) {
        folderService.moveNode(from, to);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{oldFolder}/{newFolder}")
    public void editFolderName(@PathVariable() String oldFolder,
                               @PathVariable() String newFolder) {
        folderService.editFolderName(oldFolder, newFolder);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{folder}")
    public void addNewNode(@PathVariable() String folder) {
        folderService.addNewNode(folder);
    }
}
