package com.redfrog.api.command.presets;

import org.bukkit.command.CommandSender;
import com.redfrog.api.RedFrogPlugin;
import com.redfrog.api.command.Command;
import com.redfrog.api.command.CommandClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class CustomCommand implements Command, CommandClass {


    public RedFrogPlugin plugin;


    private String name = "";
    private String[] args = new String[0];
    private boolean canHaveMoreParams = false;
    private String[] params = new String[0];
    private String description = "";
    private String[] aliases = new String[0];
    private String permission = "";
    private String permissionMessage = "";


    @Override
    public String name() {
        return name;
    }

    @Override
    public String[] args() {
        return args;
    }

    @Override
    public boolean canHaveMoreParams() {
        return canHaveMoreParams;
    }

    @Override
    public String[] params() {
        return params;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String[] aliases() {
        return aliases;
    }

    @Override
    public String permission() {
        return permission;
    }

    @Override
    public String permissionMessage() {
        return permissionMessage;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.getClass();
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setArgs(String[] args) {
        this.args = args;

        if (args.length == 1)
            this.args = args[0].split("\\s* \\s*");
    }

    public void setCanHaveMoreParams(boolean canHaveMoreParams) {
        this.canHaveMoreParams = canHaveMoreParams;
    }

    public void setParams(String[] params) {
        this.params = params;

        if (params.length == 1)
            this.params = params[0].split("\\s* \\s*");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }


    public CustomCommand(RedFrogPlugin plugin) {
        this.plugin = plugin;
    }


    public CustomCommand(RedFrogPlugin plugin, String name, String description, boolean canHaveMoreParams, String... args)
    {
        this.plugin = plugin;
        this.name = name;
        this.description = description;
        this.canHaveMoreParams = canHaveMoreParams;
        this.args = args;
    }


    public CustomCommand(RedFrogPlugin plugin, String name, String description, String... params)
    {
        this.plugin = plugin;
        this.name = name;
        this.params = params;
        this.description = description;
    }


    public CustomCommand(RedFrogPlugin plugin, String name, String[] args, boolean canHaveMoreParams, String[] params, String description, String[] aliases, String permission, String permissionMessage)
    {
        this.plugin = plugin;
        this.name = name;
        this.args = args;
        this.canHaveMoreParams = canHaveMoreParams;
        this.params = params;
        this.description = description;
        this.aliases = aliases;
        this.permission = permission;
        this.permissionMessage = permissionMessage;

        if (args.length == 1)
            this.args = args[0].split("\\s* \\s*");

        if (params.length == 1)
            this.params = params[0].split("\\s* \\s*");
    }


    public Method getOnCommandMethod() {
        try {
            Method m = this.getClass().getMethod("onCommand", CommandSender.class, String.class, String[].class);
            m.setAccessible(true);

            return m;

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Called when the command is executed.
     *
     * @param sender Who executed the command..
     * @param cmd    Executed command name.
     * @param params Params of the command.
     */
    public void onCommand(CommandSender sender, String cmd, String[] params)
    {

    }
}
