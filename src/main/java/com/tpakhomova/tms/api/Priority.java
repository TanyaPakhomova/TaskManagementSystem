package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name= "Task priority: HIGH, MEDIUM, LOW")
public enum Priority {
    HIGH, MEDIUM, LOW;
}
