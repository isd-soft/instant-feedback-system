package com.inther.controller;

import com.inther.domain.Presentation;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class PresentationController
{
    @RequestMapping(value = "/api/user/addPresentation", method = RequestMethod.PUT)
    public Map<String, Object> addPresentation(@RequestBody Presentation presentationToAdd)
    {
        return null;
    }

    @RequestMapping(value = "/api/user/getPresentations", method = RequestMethod.GET)
    public Map<String, Object> getPresentations()
    {
        return null;
    }

    @RequestMapping(value = "/api/user/editSelfPresentation", method = RequestMethod.PATCH)
    public Map<String, Object> editSelfPresentation(@RequestBody Presentation presentationToEdit)
    {
        return null;
    }

    @RequestMapping(value = "/api/user/deleteSelfPresentation", method = RequestMethod.DELETE)
    public Map<String, Object> deleteSelfPresentation(@RequestParam(value = "presentationId") Integer presentationId)
    {
        return null;
    }

    @RequestMapping(value = "/api/admin/editAnyPresentation", method = RequestMethod.PATCH)
    public Map<String, Object> editAnyPresentation(@RequestBody Presentation presentationToEdit)
    {
        return null;
    }

    @RequestMapping(value = "/api/admin/deleteAnyPresentation", method = RequestMethod.DELETE)
    public Map<String, Object> deleteAnyPresentation(@RequestParam(value = "presentationId") Integer presentationId)
    {
        return null;
    }
}