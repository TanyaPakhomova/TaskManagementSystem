package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name= "The list of tasks")
public record TaskList(List<Task> tasks) {
}
