/*
 * Copyright (c) 2017 Du Tengfei. All Rights Reserved.
 */
package com.controller;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.content.School;
import com.content.SchoolProfessional;
import com.controller.util.ControllerUtil;
import com.dao.SchoolProfessionalService;
import com.dao.SchoolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.ServiceFactory;
import com.util.LogUtil;
import com.util.NumberUtil;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FindSenior.
 * @author <A HREF="mailto:dtfgongzuo@163.com">Du Tengfei</A>
 * @version 1.0, $Revision: 0$, $Date: Nov 16, 2017$
 * @since 1.0
 */

@Controller
@RequestMapping("/school")
public class SchoolController extends ControllerUtil 
{
    private static Logger logger = Logger.getLogger(SchoolController.class);
    SchoolService schoolService = ServiceFactory.getSchoolService();
    SchoolProfessionalService schoolProfessionalService = ServiceFactory.getSchoolProfessionalService();
    @RequestMapping(value = "/getSchool", method = RequestMethod.GET)
    public @ResponseBody School getSchool(@RequestParam String id)
    {
        Integer idNow = NumberUtil.parseInteger(id);
        if (idNow == null)
            return null;
        School school = schoolService.getById(idNow);
        if (school == null)
            return null;
        
        SchoolProfessional[] sps = schoolProfessionalService.getSchoolProfessionalsBySchoolId(school.getId());
        if (sps != null && sps.length != 0)
            school.setSchoolProfessionals(Arrays.asList(sps));
        return school;
    }
    
    @RequestMapping(value = "/updateSchool", method = RequestMethod.POST)
    public @ResponseBody String updateSchool(@RequestBody String jsonString)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            School school = mapper.readValue(jsonString, School.class);
            if (school.getId() == 0)
                schoolService.createSchool(school);
            else
                schoolService.updateSchool(school);
            
            schoolProfessionalService.updateSchoolProfessionals(school.getId(), school.getSchoolProfessionals());
            
            return SUCCESS;
        }
        catch (Exception e)
        {
            logger.error(LogUtil.toString(e));
        }
        
        return FAILED;
    }
    
    @RequestMapping(value = "/getAllSchool", method = RequestMethod.GET)
    public @ResponseBody School[] getAllSchool()
    {
        School[] schools = null;
        try
        {
            schools = schoolService.getAllSchools();
        }
        catch (Exception e)
        {
            logger.error(LogUtil.toString(e));
            return null;
        }
        
        return schools;
    }
    
    @RequestMapping(value = "/searchSchool", method = RequestMethod.GET)
    public @ResponseBody School[] searchSchool(String name, String province)
    {
        School[] schools = null;
        try
        {
            schools = schoolService.querySchools(name, province);
        }
        catch (Exception e)
        {
            logger.error(LogUtil.toString(e));
            return null;
        }
        
        return schools;
    }
}