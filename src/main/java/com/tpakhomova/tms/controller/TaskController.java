package com.tpakhomova.tms.controller;

import com.tpakhomova.tms.api.CreateTaskReq;
import com.tpakhomova.tms.api.Task;
import com.tpakhomova.tms.api.TaskList;
import com.tpakhomova.tms.data.Priority;
import com.tpakhomova.tms.data.Status;
import com.tpakhomova.tms.service.TaskManagementService;
import com.tpakhomova.tms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Tasks", description = "Tasks management APIs.")
@RestController
@RequestMapping("tasks")
public class TaskController {

    private final TaskManagementService taskManagementService;

    public TaskController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }
    @Operation(summary = "Create a task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad task parameters.",
                    content = @Content),
    })
    @PostMapping
    String create(@RequestBody @Valid CreateTaskReq task) {
       Long taskId = taskManagementService.createTask(convert(null, task));

       if (taskId != null) {
           return taskId.toString();
       } else {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
       }
    }

    @Operation(summary = "Edit a task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task edited",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Invalid task supplied",
                    content = @Content())
    })
    @PutMapping("{id}")
    void edit(
            @PathVariable Long id,
              @RequestBody @Valid CreateTaskReq task,
              @AuthenticationPrincipal UserDetails userDetails) {

        checkValidRequest(id, userDetails.getUsername());

        if (taskManagementService.editTask(convert(id, task))) {
            return;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkValidRequest(Long id, String email) {
        var originalTask = taskManagementService.findTask(id);
        if (originalTask == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!email.equals(originalTask.getAuthorEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Change task status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content),
    })
    @PutMapping("status/{id}")
    void changeStatus(
            @PathVariable Long id,
            @RequestBody String status,
            @AuthenticationPrincipal UserDetails userDetails) {

        checkValidRequest(id, userDetails.getUsername());
        if (taskManagementService.changeStatus(id, parseStatusOrThrow(status))) {
            return;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Status parseStatusOrThrow(String status) {
        Status statusEnum;
        try {
            statusEnum = Status.valueOf(status);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return statusEnum;
    }

    @Operation(summary = "Change task priority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Priority changed",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content),
    })
    @PutMapping("priority/{id}")
    void changePriority(
            @PathVariable Long id,
            @RequestBody String priority,
            @AuthenticationPrincipal UserDetails userDetails) {

        checkValidRequest(id, userDetails.getUsername());

        if (taskManagementService.changePriority(id, parcePriorityOrThrow(priority))) {
            return;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Priority parcePriorityOrThrow(String priority) {
        Priority priorityEnum;
        try {
            priorityEnum = Priority.valueOf(priority);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return priorityEnum;
    }

    @Operation(summary = "Get task by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task returned.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)) }),
            @ApiResponse(responseCode = "404", description = "No task with such id.",
                    content = @Content),
    })
    @GetMapping("{id}")
    Task getById(@PathVariable Long id) {
        var task = taskManagementService.findTask(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return convertToApiTask(task);
    }

    @Operation(summary = "Delete task by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted",
                    content = @Content()),
    })
    @DeleteMapping("{id}")
    void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        checkValidRequest(id, userDetails.getUsername());
        taskManagementService.deleteTask(id);
    }

    @Operation(summary = "Get the list of tasks by author email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskList.class)) }),
    })
    @GetMapping("author/{email}")
    TaskList getByAuthor(@PathVariable @Valid @Email String email) {
        var tasks = taskManagementService.findTasksByAuthor(email);
        var apiTasks = tasks.stream()
                .map(this::convertToApiTask)
                .toList();

        return new TaskList(apiTasks);
    }

    @Operation(summary = "Get the list of tasks by assignee email.")
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

