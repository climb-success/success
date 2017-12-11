/*
 * Copyright (c) 2017 Du Tengfei. All Rights Reserved.
 */
package com.controller;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.content.Student;
import com.controller.util.ControllerUtil;
import com.dao.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.ServiceFactory;
import com.util.LogUtil;
import com.util.NumberUtil;
import com.util.TextUtil;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * FindSenior.
 * @author <A HREF="mailto:dtfgongzuo@163.com">Du Tengfei</A>
 * @version 1.0, $Revision: 0$, $Date: Nov 16, 2017$
 * @since 1.0
 */

@Controller
@RequestMapping("/student")
public class StudentController extends ControllerUtil 
{
    private static Logger logger = Logger.getLogger(StudentController.class);
    StudentService studentService = ServiceFactory.getStudentService();
    @RequestMapping(value = "/getStudent", method = RequestMethod.GET)
    public @ResponseBody Student getStudent(@RequestParam String id, @RequestParam String adminName)
    {
        if (validateAdmin(adminName))
        {
            Integer idNow = NumberUtil.parseInteger(id);
            if (idNow == null)
                return null;
            return studentService.getById(idNow);
        }
        return null;
    }
    
    @RequestMapping(value = "/updateStudent", method = RequestMethod.POST)
    public @ResponseBody String updateStudent(@RequestBody String jsonString)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            Student student = mapper.readValue(jsonString, Student.class);
            Student studentdb = studentService.queryStudentByTelePhone(student.getTelePhone());
            
            if (studentdb != null)
            {
                studentdb.setName(student.getName());
                studentdb.setSchoolId(student.getSchoolId());
                studentdb.setProfessionalId(studentdb.getProfessionalId());
                studentdb.setTelePhone(student.getTelePhone());
                studentdb.setQq(student.getQq());
                studentdb.setWeixin(student.getWeixin());
                studentdb.setRequirement(student.getRequirement());
                studentdb.setGrade(student.getGrade());
                studentdb.setStatus(TextUtil.isEmpty(student.getStatus()) ? Student.STATUS_NOT_FINISH : student.getStatus());
                studentdb.setUpdateDate(new Date());
                studentService.updateStudent(studentdb);
            }
            else
            {
                student.setId(new Integer(0));
                student.setStatus(Student.STATUS_NOT_FINISH);
                student.setUpdateDate(new Date());
                studentService.createStudent(student);
            }
            
            return SUCCESS;
        }
        catch (Exception e)
        {
            logger.error(LogUtil.toString(e));
        }
        
        return FAILED;
    }
    
    @RequestMapping(value = "/searchStudent", method = RequestMethod.GET)
    public @ResponseBody Student[] searchStudent(@RequestParam String adminName, String name, 
            String school, String professional, String requirement, String grade, String status)
    {
        Student[] students = null;
        if (validateAdmin(adminName))
        {
            try
            {
                students = studentService.queryStudents(name, school, professional, requirement, NumberUtil.parseInteger(grade), status);
            }
            catch (Exception e)
            {
                logger.error(LogUtil.toString(e));
                return null;
            }
        }
        return students;
    }
}