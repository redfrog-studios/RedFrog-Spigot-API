package com.redfrog.api.command;

import com.redfrog.api.RedFrogPlugin;

import java.lang.reflect.Method;

public class CommandExecutor {

    private Command command;
    private Method method;
    private String pluginName;

    private String usage;

    private CommandClass objClass;

    private boolean useParentHelp;

    private String[] args;
    private String[] params;

    public Command getCommand() {
        return command;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return command.name();
    }

    public String[] getArgs() {
        return args;
    }

    public boolean canHaveMoreParams() {
        return command.canHaveMoreParams();
    }


    public String[] getParams() {
        return params;
    }

    public String[] getAliases() {
        return command.aliases();
    }

    public String getDescription() {
        return command.description();
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return command.permission();
    }

    public String getPermissionMessage() {
        return command.permissionMessage();
    }

    public String getPluginName() {
        return pluginName;
    }

    public CommandClass getObjClass() {
        return objClass;
    }

    public boolean useParentHelp() {
        return useParentHelp;
    }

    public CommandExecutor(RedFrogPlugin plugin, Command command, Method method, String pluginName, CommandClass objClass, boolean useParentHelp) {
        this.command = command;
        this.method = method;
        this.pluginName = pluginName;
        this.objClass = objClass;
        this.args = command.args();
        this.params = command.params();
        this.useParentHelp = useParentHelp;

        if (args.length == 1)
            this.args = args[0].split("\\s* \\s*");

        if (params.length == 1)
            this.params = params[0].split("\\s* \\s*");

        StringBuilder paramsText = new StringBuilder();
        StringBuilder argsText = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            argsText.append(args[i]);

            if (i + 1 < args.length || (command.params().length >= 1))
                argsText.append(" ");
        }

        for (int i = 0; i < command.params().length; i++) {
            String param = command.params()[i];

            if (param.startsWith("@"))
                param = param.substring(1);

            if (plugin.placeholderParameterExists(param))
                param = plugin.getPlaceholderDisplayName(param);

            paramsText.append("<").append(param).append(">");

            if (i + 1 < command.params().length)
                paramsText.append(" ");
        }

        this.usage = argsText.toString() + paramsText.toString();
    }
}
