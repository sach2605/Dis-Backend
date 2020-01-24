package sgsits.cse.dis.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import sgsits.cse.dis.user.constants.RestAPI;
import sgsits.cse.dis.user.message.response.CategorySpecificTaskResponse;
import sgsits.cse.dis.user.serviceImpl.TaskServiceImpl;

@CrossOrigin(origins = "*")
@Api(value = "Task controller")
@RestController
@RequestMapping(path = "/task")
public class TaskController {
	
	@Autowired
	TaskServiceImpl taskServiceImpl;
	
	@ApiOperation(value="Get setting", response = CategorySpecificTaskResponse.class, httpMethod = "GET", produces = "application/json")
	@GetMapping(path=RestAPI.GET_TASKS_FROM_CATEGORY_ID, produces = "application/json")
	public ResponseEntity<List<CategorySpecificTaskResponse>> getTasksFromCategoryId(@PathVariable("categoryId") String categoryId){
		return new ResponseEntity<List<CategorySpecificTaskResponse>>(taskServiceImpl.getTasksFromCategoryId(categoryId),HttpStatus.OK);
	}
}