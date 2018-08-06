package org.folders.com.controllers;

import org.folders.com.dto.FolderDto;
import org.folders.com.service.FolderService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@AllArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @PostMapping(value = "/enter-directory")
    public ModelAndView addDirectoryForSearch (@ModelAttribute("folder") FolderDto folderDto) {
        String directory = folderDto.getDirectory();
        if (folderService.setDirectoryForSearch(directory)) {
            List<String> resultFolders = folderService.getChildFoldersByParent(null);
            ModelAndView model = new ModelAndView("index");
            model.addObject("list", resultFolders);
            return model;
        } else {
            return getFirstFolders();
        }
    }

    @GetMapping
    public ModelAndView getFirstFolders() {
        ModelAndView modelAndView = new ModelAndView("mainPage");
        modelAndView.addObject("folder", new FolderDto());
        return modelAndView;
    }

    @PostMapping(value = "/{folder}")
    public String getNextFolders(@PathVariable("folder") String folder) {
        return folderService.getJsonOfChildsByParent(folder);
    }

    @DeleteMapping(value = "/{folder}")
    public void removeFolder(@PathVariable("folder") String folder) {
        folderService.removeNode(folder);
    }

    @PutMapping(value = "/{from}/{to}")
    public void moveFolder(@PathVariable("from") String from,
                           @PathVariable("to") String to) {
        folderService.moveNode(from, to);
    }

    @PostMapping(value = "/{oldFolder}/{newFolder}")
    public void editFolderName(@PathVariable("oldFolder") String oldFolder,
                               @PathVariable("newFolder") String newFolder) {
        folderService.editFolderName(oldFolder, newFolder);
    }

    @PutMapping(value = "/{folder}")
    public void addNewNode(@PathVariable("folder") String folder) {
        folderService.addNewFolder(folder);
    }
}
