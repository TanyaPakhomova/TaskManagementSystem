package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.CreateTaskReq;
import com.tpakhomova.tms.api.Task;
import com.tpakhomova.tms.api.TaskList;
import com.tpakhomova.tms.data.Priority;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.service.TaskManagementService;
import com.tpakhomova.tms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("tasks")
public class TaskController {
    private final UserService userService;

    private final TaskManagementService taskManagementService;

    public TaskController(UserService userService, TaskManagementService taskManagementService) {
        this.userService = userService;
        this.taskManagementService = taskManagementService;
    }


    @PostMapping
    String create(@RequestBody @Valid CreateTaskReq task) {
       Long taskId = taskManagementService.createTask(convert(null, task));

       if (taskId != null) {
           return taskId.toString();
       } else {
           throw new RuntimeException();
       }
    }

    @PutMapping("{id}")
    void edit(@PathVariable Long id, @RequestBody @Valid CreateTaskReq task) {
        if (taskManagementService.editTask(convert(id, task))) {
            return;
        } else {
            throw new RuntimeException();
        }
    }

    @GetMapping("{id}")
    Task getById(@PathVariable Long id) {
        var task = taskManagementService.findTask(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return convertToApiTask(task);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        taskManagementService.deleteTask(id);
    }

    @GetMapping("author/{email}")
    TaskList getByAuthor(@PathVariable @Valid @Email String email) {
        var tasks = taskManagementService.findTasksByAuthor(email);
        var apiTasks = tasks.stream()
                .map(this::convertToApiTask)
                .toList();

        return new TaskList(apiTasks);
    }

    @GetMapping("assignee/{email}")
    TaskList getByAssignee(@PathVariable @Valid @Email String email) {
        var tasks = taskManagementService.findTasksByAssignee(email);
        var apiTasks = tasks.stream()
                .map(this::convertToApiTask)
                .toList();

        return new TaskList(apiTasks);
    }

    private Task convertToApiTask(com.tpakhomova.tms.data.Task t) {
        return new Task(
                t.getId(),
                t.getHeader(),
                t.getDescription(),
                com.tpakhomova.tms.api.Status.valueOf(t.getStatus().name()),
                com.tpakhomova.tms.api.Priority.valueOf(t.getPriority().name()),
                t.getAuthorEmail(),
                t.getAssigneeEmail()
        );
    }

    private static com.tpakhomova.tms.data.Task convert(Long id, CreateTaskReq task) {
        return new com.tpakhomova.tms.data.Task(
                id,
                task.header(),
                task.description(),
                Status.valueOf(task.status().name()),
                Priority.valueOf(task.priority().name()),
                task.authorEmail(),
                task.assigneeEmail()
        );
    }
}

