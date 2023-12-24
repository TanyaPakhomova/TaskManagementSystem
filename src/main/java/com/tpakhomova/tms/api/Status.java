package com.tpakhomova.tms.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name= "Task status: WAIT, PROCESS, DONE")
public enum Status {
    WAIT, PROCESS, DONE;
}
