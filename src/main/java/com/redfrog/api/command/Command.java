package com.redfrog.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Command {

    public String name() default "";
    public String[] args() default {};
    public boolean canHaveMoreParams() default false;
    public String[] params() default {};
    public String description();

    public String[] aliases() default {};
    public String permission() default "";
    public String permissionMessage() default "";
}
