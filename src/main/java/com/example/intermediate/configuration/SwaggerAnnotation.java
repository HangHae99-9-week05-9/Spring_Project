package com.example.intermediate.configuration;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
        @ApiImplicitParam(
                name = "Refresh_Token",
                required = true,
                dataType = "string",
                paramType = "header"
        )
})
public @interface SwaggerAnnotation {

}
