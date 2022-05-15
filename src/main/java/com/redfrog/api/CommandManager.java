package com.redfrog.api;

import com.redfrog.api.command.CommandExecutor;
import com.redfrog.api.command.CommandExecutorComparator;
import com.redfrog.api.command.presets.CommandHelpListHandler;
import com.redfrog.api.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


class CommandManager implements org.bukkit.command.CommandExecutor, TabCompleter {

    private final RedFrogPlugin plugin;
    private final ArrayList<String> emptyList = new ArrayList<String>(0);
    private final HashMap<String, ArrayList<CommandExecutor>> commands = new HashMap<String, ArrayList<CommandExecutor>>();
    private final CommandBranch rootCommands = new CommandBranch();

    public CommandManager(RedFrogPlugin plugin) {
        this.plugin = plugin;
    }

    private CommandBranch getOrCreateBranch(String name, String[] args) {
        CommandBranch commandBranch = null;
        CommandBranch newCommandBranch = null;

        if (!rootCommands.childCommands.containsKey(name)) {
            commandBranch = new CommandBranch();
            commandBranch.parent = rootCommands;

            rootCommands.childCommands.put(name, commandBranch);
        }

        commandBranch = rootCommands.childCommands.get(name);

        for (String arg : args) {
            if (!commandBranch.childCommands.containsKey(arg)) {
                newCommandBranch = new CommandBranch();
                newCommandBranch.parent = commandBranch;

                commandBranch.childCommands.put(arg, newCommandBranch);
            }

            commandBranch = commandBranch.childCommands.get(arg);
        }

        return commandBranch;
    }

    private CommandBranch getCommand(String name, String[] args) {
        CommandBranch commandBranch = null;

        if (!rootCommands.childCommands.containsKey(name)) {
            return null;
        }

        commandBranch = rootCommands.childCommands.get(name);

        for (String arg : args) {
            if (commandBranch.childCommands.containsKey(arg))
                commandBranch = commandBranch.childCommands.get(arg);
            else
                break;
        }

        return commandBranch;
    }

    public void registerCommand(CommandExecutor cmd) {

        boolean sameCmd = false;

        if (!commands.containsKey(cmd.getName()))
            commands.put(cmd.getName(), new ArrayList<CommandExecutor>());

        CommandBranch commandBranch = getOrCreateBranch(cmd.getName(), cmd.getArgs());
        commandBranch.executor = cmd;

        CommandBranch parentBranch = commandBranch.parent;

        for (int i = 0; i < cmd.getAliases().length; i++) {
            if (parentBranch != null)
                if (!parentBranch.childCommands.containsKey(cmd.getAliases()[i]))
                    parentBranch.childCommands.put(cmd.getAliases()[i], commandBranch);
        }

        ArrayList<CommandExecutor> cmdEx = commands.get(cmd.getName().toLowerCase());

        for (CommandExecutor command : cmdEx) {

            if (command.getArgs().length == cmd.getArgs().length) {
                sameCmd = true;

                for (int j = 0; j < command.getArgs().length; j++) {
                    if (!command.getArgs()[j].equalsIgnoreCase(cmd.getArgs()[j])) {
                        sameCmd = false;
                        break;
                    }
                }

                if (sameCmd) {
                    Exception ex = new Exception("Error registering command. Command already exists: " + cmd.getName() + " " + String.join(" ", cmd.getArgs()));
                    ex.printStackTrace();
                    return;
                }
            }
        }

        commands.get(cmd.getName()).add(cmd);
        commands.get(cmd.getName()).sort(CommandExecutorComparator.getInstance());
    }

    public void registerCommands(List<CommandExecutor> cmds) {
        if (cmds != null)
            cmds.forEach(this::registerCommand);
    }

    public boolean onCommand(CommandSender sender, Command command, String cmd) {
        return onCommand(sender, command, cmd, new String[0]);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String cmd, String[] args) {
        cmd = cmd.toLowerCase();

        if (!commands.containsKey(command.getName().toLowerCase()))
            return emptyList;

        boolean foundAny;
        CommandBranch commandBranch = getCommand(cmd, args);

        if (commandBranch.executor == null)
            return emptyList;

        CommandExecutor executor = commandBranch.executor;

        if (executor.getMethod() != null) {
            if (executor.getPermission() != null && !executor.getPermission().equals("")) {
                if (!sender.hasPermission(executor.getPermission())) {
                    return emptyList;
                }
            }

            if (executor.getCommand() instanceof CommandHelpListHandler || commandBranch.childCommands.size() > 0) {

                ArrayList<CommandExecutor> helpCommands = getCommandChilds(commands.get(command.getName().toLowerCase()), sender, executor);

                List<String> subCommands = new ArrayList<>();

                commandBranch.childCommands.forEach((k, v) -> {
                    subCommands.add(k);
                });

                return subCommands;
            }

            if (executor.getParams().length >= args.length - executor.getArgs().length) {

                if (args.length - executor.getArgs().length == 0)
                    return emptyList;

                String param = executor.getParams()[args.length - executor.getArgs().length - 1];

                if (param.startsWith("@")) {

                    param = param.substring(1);

                    if (plugin.placeholderParameterExists(param))
                        return plugin.getPlaceholderParameterValue(param, sender);
                    else {
                        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

                        System.out.println("Placeholder parameter '" + param + "' does not exist. in");
                        for (int i = 1; i < elements.length; i++) {
                            StackTraceElement s = elements[i];
                            System.out.println("\tat " + s.getClassName() + "." + s.getMethodName()
                                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
                        }
                    }
                }
            }
        }

        return emptyList;
    }

    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {

        cmd = cmd.toLowerCase();

        if (!commands.containsKey(command.getName().toLowerCase()))
            return false;

        CommandBranch commandBranch = getCommand(cmd, args);

        if (commandBranch == null) {
            sender.sendMessage(CC.RED + "Command not found.");
            return false;
        }

        if (commandBranch.executor == null) {
            sender.sendMessage(CC.RED + "Usage: /" + cmd);
            return false;
        }

        CommandExecutor executor = commandBranch.executor;

        if (executor.getMethod() != null) {
            String[] finalArgs = new String[args.length - executor.getArgs().length];

            if (executor.getPermission() != null && !executor.getPermission().equals("")) {
                if (!sender.hasPermission(executor.getPermission())) {
                    if (!executor.getPermissionMessage().equals(""))
                        sender.sendMessage(executor.getPermissionMessage());
                    else
                        sender.sendMessage(CC.format(plugin.DefaultCommandPermissionMessage));
                    return true;
                }
            }

            for (int j = executor.getArgs().length; j < args.length; j++) {
                finalArgs[j - executor.getArgs().length] = args[j];
            }

            if (finalArgs.length != executor.getParams().length && !executor.canHaveMoreParams() || (executor.canHaveMoreParams() && finalArgs.length < executor.getParams().length)) {
                sender.sendMessage(CC.RED + "Usage: /" + cmd + " " + executor.getUsage());
                return true;
            }

            if (executor.getCommand() instanceof CommandHelpListHandler) {


                ArrayList<CommandExecutor> helpCommands = getCommandChilds(commands.get(command.getName().toLowerCase()), sender, executor);

                ((CommandHelpListHandler) executor.getCommand()).setCommands(helpCommands);
            }

            CommandResult result = invokeCommand(executor, sender, command, cmd, finalArgs);

            if (result == CommandResult.SUCCESS)
                return true;
            else if (result == CommandResult.FAIL)
                return false;

            sender.sendMessage(CC.RED + "Usage: /" + cmd + " " + executor.getUsage());
            return true;
        }

        return false;
    }

    private CommandResult invokeCommand(CommandExecutor executor, CommandSender sender, Command command, String
            cmd, String[] args) {
        try {
            Class<?>[] types = executor.getMethod().getParameterTypes();
            Object[] params = new Object[types.length];

            for (int i = 0; i < types.length; i++) {
                if (types[i].equals(CommandSender.class))
                    params[i] = sender;
                else if (types[i].equals(Player.class))
                    params[i] = sender instanceof Player ? (Player) sender : null;
                else if (types[i].equals(JavaPlugin.class) || types[i].equals(RedFrogPlugin.class))
                    params[i] = plugin;
                else if (types[i].equals(Command.class))
                    params[i] = command;
                else if (types[i].equals(String.class))
                    params[i] = cmd;
                else if (types[i].equals(String[].class))
                    params[i] = args;
                else
                    params[i] = null;
            }

            if (executor.getMethod().getReturnType().equals(boolean.class)) {
                return ((boolean) executor.getMethod().invoke(executor.getObjClass(), params)) ? CommandResult.SUCCESS : CommandResult.FAIL;
            } else if (executor.getMethod().getReturnType().equals(CommandResult.class)) {

                try {
                    return (CommandResult) executor.getMethod().invoke(executor.getObjClass(), params);
                } catch (Exception e) {
                    return CommandResult.FAIL;
                }
            } else {
                executor.getMethod().invoke(executor.getObjClass(), params);
                return CommandResult.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommandResult.FAIL;
        }
    }

    private ArrayList<CommandExecutor> getCommandChilds(ArrayList<CommandExecutor> commands, CommandSender sender, CommandExecutor cmd) {
        ArrayList<CommandExecutor> list = new ArrayList<>();
        list.add(cmd);

        return getCommandChilds(commands, sender, cmd, list);
    }

    private ArrayList<CommandExecutor> getCommandChilds(ArrayList<CommandExecutor> commands, CommandSender sender, CommandExecutor cmd, ArrayList<CommandExecutor> multiCommands) {
        return getCommandChilds(commands, sender, cmd, multiCommands, true);
    }

    private ArrayList<CommandExecutor> getCommandChilds(ArrayList<CommandExecutor> commands, CommandSender sender, CommandExecutor cmd, ArrayList<CommandExecutor> multiCommands, boolean isRoot) {

        ArrayList<CommandExecutor> cmds = (ArrayList<CommandExecutor>) commands.clone();

        int cmdArgsLength = cmd.useParentHelp() && isRoot ? cmd.getArgs().length - 1 : cmd.getArgs().length;

        if (cmd.useParentHelp() && isRoot) {

            String[] cmdArgs = cmd.getArgs();

            boolean isParent = true;

            for (CommandExecutor c : cmds) {
                if (c.getArgs().length == cmdArgs.length - 1) {
                    for (int i = 0; i < cmdArgs.length - 1; i++) {
                        if (!cmdArgs[i].equals(c.getArgs()[i]))
                            isParent = false;
                    }

                    if (isParent) {
                        return getCommandChilds(commands, sender, c, multiCommands, true);
                    }
                }
            }
        }

        if (cmdArgsLength < 0)
            cmdArgsLength = 0;

        if (cmd.getCommand() instanceof CommandHelpListHandler || isRoot) {

            ArrayList<CommandExecutor> localMultiCommands = new ArrayList<>();

            for (CommandExecutor commandExecutor : cmds) {
                if (!multiCommands.contains(commandExecutor) && commandExecutor.getCommand() instanceof CommandHelpListHandler && commandExecutor != cmd) {
                    multiCommands.add(commandExecutor);
                    localMultiCommands.add(commandExecutor);
                }
            }

            for (CommandExecutor localMultiCommand : localMultiCommands) {
                if (localMultiCommand.getArgs().length >= cmdArgsLength) {
                    cmds.removeAll(getCommandChilds(cmds, sender, localMultiCommand, multiCommands, false));
                }
            }

            if (!isRoot)
                cmds.remove(cmd);

            boolean isChildCommand;

            for (int j = 0; j < cmds.size(); j++) {
                if (!sender.hasPermission(cmds.get(j).getPermission())) {
                    cmds.remove(j);
                    j--;
                }
            }

            for (int j = 0; j < cmds.size(); j++) {

                isChildCommand = false;

                if (cmdArgsLength <= cmds.get(j).getArgs().length) {

                    isChildCommand = true;

                    for (int k = 0; k < cmdArgsLength; k++) {
                        if (!cmd.getArgs()[k].equalsIgnoreCase(cmds.get(j).getArgs()[k])) {
                            isChildCommand = false;
                        }
                    }
                }

                if (!isChildCommand) {
                    cmds.remove(j);
                    j--;
                }
            }
        } else {
            cmds.clear();
            cmds.add(cmd);
        }

        return cmds;
    }

    enum CommandResult {
        SUCCESS, FAIL
    }

    private class CommandBranch {
        public Hashtable<String, CommandBranch> childCommands;
        public CommandExecutor executor;
        public CommandBranch parent;

        CommandBranch() {
            childCommands = new Hashtable<>();
        }
    }
}
