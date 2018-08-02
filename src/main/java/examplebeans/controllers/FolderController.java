package examplebeans.controllers;

import examplebeans.dto.FolderManagerDto;
import examplebeans.service.FolderService;
import examplebeans.service.JSONFolderService;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
public class FolderController {
    private FolderService folderService;
    private JSONFolderService jsonFolderService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ModelAndView getFirstFolders() {
        Set<FolderManagerDto> allForFolderManager = folderService.getAllForFolder(null);
        Set<String> collect = folderService.getStringCollectionFromFolder(allForFolderManager);
        ModelAndView model = new ModelAndView("WEB-INF/jsp/index.jsp");
        model.addObject("list", collect);
        return model;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{folder}")
    public String getNextFolders(@PathVariable() String folder) {
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
        Set<FolderManagerDto> allForFolderManager = folderService.getAllForFolder(folder);
        if (allForFolderManager != null) {
            Set<String> stringCollectionFromFolder = folderService.getStringCollectionFromFolder(
                allForFolderManager);
            return jsonFolderService.getJSONChildesFromParentDirectory(
                stringCollectionFromFolder);
        } else {
            return "";
        }
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
