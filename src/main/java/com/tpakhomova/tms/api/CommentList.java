package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name= "The list of comments for a task")
public record CommentList(List<Comment> comments) {
}
