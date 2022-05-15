package com.redfrog.api;

import com.redfrog.api.command.*;
import com.redfrog.api.command.presets.CommandHelpList;
import com.redfrog.api.command.presets.CommandHelpListHandler;
import com.redfrog.api.command.presets.CustomCommand;
import com.redfrog.api.command.presets.ListCommand;
import com.redfrog.api.inventory.InventoryListener;
import com.redfrog.api.inventory.RedFrogInventory;
import com.redfrog.api.utils.CC;
import com.redfrog.api.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RedFrogPlugin extends JavaPlugin implements CommandClass {


    private final ArrayList<String> loadedCommands = new ArrayList<>();
    private final ArrayList<CommandExecutor> commandExecutors = new ArrayList<>();
    private final HashMap<String, ParameterPlaceholder> placeholderParams = new HashMap<>();
    private final List<CommandAnnotationData> commandAnnotations = new ArrayList<>();
    public String DefaultCommandPermissionMessage = CC.RED + "You do not have permission to perform this action.";
    private CommandMap commandMap;
    private Constructor<PluginCommand> commandConstructor;
    private boolean hasInnited;
    private String[] mainAliases;
    private CommandManager commandManager;
    private InventoryListener inventoryListener;

    public InventoryListener getInventoryListener() {
        return inventoryListener;
    }

    private boolean usePlaceholderAPI = false;


    public boolean isUsingPlaceholderAPI() {
        return usePlaceholderAPI;
    }


    @Override
    public void onEnable() {
        super.onEnable();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            usePlaceholderAPI = true;

        Message.init(this);

        inventoryListener = new InventoryListener();
        getServer().getPluginManager().registerEvents(inventoryListener, this);

        if (!hasInnited) {
            init();
            onInit();
            hasInnited = true;
            commandExecutors.sort(CommandExecutorComparator.getInstance());

            CommandExecutor[] executors = new CommandExecutor[commandExecutors.size()];
            executors = commandExecutors.toArray(executors);

            registerCommands(executors);
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                onServerReady();
            }
        };
        runnable.runTaskLater(this, 1);

        saveDefaultConfig();
        reloadConfig();
    }


    private void init() {

        commandManager = new CommandManager(this);

        try {
            Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);

            commandMap = (CommandMap) f.get(Bukkit.getPluginManager());

            commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Register an inventory to handle click events.
     * @param inventory
     */
    public void registerInventory(RedFrogInventory inventory) {
        inventoryListener.registerInventory(inventory);
    }


    /**
     * Unregister a registered inventory.
     * @param inventory
     */
    public void unregisterInventory(RedFrogInventory inventory) {
        inventoryListener.unregisterInventory(inventory);
    }


    /**
     * Called the first time the plugin is loaded.
     * Commands must be registered only here.
     */
    protected void onInit() {
        loadedCommands.clear();
        placeholderParams.clear();
        commandExecutors.clear();
        commandAnnotations.clear();

        onRegisterCommands();
        onRegisterDefaultPlaceholderParameters();
        onRegisterDefaultCommandAnnotations();
    }


    /**
     * Called after the server has loaded all the plugins.
     */
    protected void onServerReady() {
    }


    /**
     * Called when the plugin is about to be reloaded via /"plugin" reload.
     * @param sender CommandSender that executed the command.
     */
    protected void onReloadStart(CommandSender sender) {
    }


    /**
     * Called when the plugin has finished being reloaded via /"plugin" reload.
     * @param sender CommandSender that executed the command.
     */
    protected void onReloadFinish(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + getDescription().getName() + ": Reload complete.");
    }


    /**
     * Called when the plugin is loaded to register default commands Such as help, reload and version.
     * @param aliases Aliases of the plugin commands.
     */
    protected void onRegisterCommands(String... aliases) {

        this.mainAliases = aliases;

        onRegisterPluginCommand("List commands for " + getDescription().getName() + ".");
        onRegisterReloadCommand("Reloads the plugin.", getDescription().getName().toLowerCase() + ".admin");
        onRegisterVersionCommand("Shows the version of the plugin.", getDescription().getName().toLowerCase() + ".admin");
    }


    /**
     * Called when the plugin is loaded to register default placeholder parameters such as "player".
     */
    protected void onRegisterDefaultPlaceholderParameters() {
        registerPlaceholderParameter("player", (sender) ->
        {
            ArrayList<String> result = new ArrayList<>();
            int i = 0;

            for (Player player : getServer().getOnlinePlayers()) {
                result.add(player.getName());
                i++;
            }

            return result;
        });
    }


    protected void onRegisterDefaultCommandAnnotations() {
        registerCommandAnnotation(CommandHelpList.class, CommandHelpListHandler.class);
    }


    /**
     * Called to register the plugin main command /"plugin" and /"plugin" help.
     * @param description Description of the command.
     */
    protected void onRegisterPluginCommand(String description) {
        onRegisterMainCommand(description);
        registerHelpListCommand(getDescription().getName().toLowerCase(), mainAliases, description, true, "help");
    }


    /**
     * Called when registering the main command of the plugin, which shows a help list. Override to change it.
     * @param description Description of the command.
     */
    protected void onRegisterMainCommand(String description) {
        registerHelpListCommand(description);
    }


    /**
     * Register a command tab placeholder parameter that will automatically be assigned to all the registered commands.
     * @param parameterName Name of the parameter. "player" is registered by default.
     * @param method Method being called when a placeholder is typed.
     */
    public void registerPlaceholderParameter(String parameterName, PlaceholderParameterMethod method) {
        registerPlaceholderParameter(parameterName, parameterName, method);
    }


    /**
     * Register a command tab placeholder parameter that will automatically be assigned to all the registered commands.
     * @param parameterName Name of the parameter. "player" is registered by default.
     * @param displayName Displayed name of the parameter.
     * @param method Method being called when a placeholder is typed.
     */
    public void registerPlaceholderParameter(String parameterName, String displayName, PlaceholderParameterMethod method) {

        if (parameterName.startsWith("@"))
            parameterName = parameterName.substring(1);

        if (displayName.startsWith("@"))
            displayName = displayName.substring(1);

        placeholderParams.put(parameterName, new ParameterPlaceholder(displayName, method));
    }


    /**
     * Register an event listener to the plugin.
     * @param listener Listener to be registered.
     */
    public void registerEventListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }


    /**
     * Register a command annotation class to be possible to register commands derived by this class.
     * Commands with @annotation will be handled by @commandClass.
     * @param annotation Annotation class to register.
     * @param commandClass Class to register.
     */
    public void registerCommandAnnotation(Class<? extends Annotation> annotation, Class<? extends CustomCommand> commandClass) {
        commandAnnotations.add(new CommandAnnotationData(annotation, commandClass));
    }


    /**
     * Indicate if a placeholder parameter exists by a given name.
     * @param parameterName Name to check if it is registered.
     * @return
     */
    public boolean placeholderParameterExists(String parameterName) {
        return placeholderParams.containsKey(parameterName);
    }


    /**
     * Returns the displayed name by a placeholder parameter.
     * @param parameterName Parameter name to get its displayed name.
     * @return
     */
    public String getPlaceholderDisplayName(String parameterName) {
        return placeholderParams.get(parameterName).DisplayName;
    }


    /**
     * Used to get possible command placeholder parameters when tabbed by @sender.
     * @param parameterName Name of the parameter.
     * @param sender Sender that executed the command.
     * @return
     */
    public List<String> getPlaceholderParameterValue(String parameterName, CommandSender sender) {
        return placeholderParams.get(parameterName).Method.method(sender);
    }


    /**
     * Register a static help list command. All the child commands will automatically be listed.
     * @param name Base name of the command.
     * @param aliases Aliases that the base name can have.
     * @param description Description of the command.
     * @param args Optional arguments like "help".
     */
    public void registerHelpListCommand(String name, String[] aliases, String description, String... args) {
        registerHelpListCommand(name, aliases, description, false, args);
    }


    /**
     * Register a static help list command. All the child commands will automatically be listed.
     * @param description Description of the command.
     * @param args Optional arguments like "help".
     */
    public void registerHelpListCommand(String description, String... args) {
        registerHelpListCommand("", mainAliases, description, args);
    }


    /**
     * Register a static help list command. All the child commands will automatically be listed.
     * @param name Base name of the command.
     * @param aliases Aliases that the base name can have.
     * @param description Description of the command.
     * @param useParentHelp Indicate if this command will use command childs of the parent command instead.
     *                      As an example /faction help will show child commands of the parent which is "faction".
     * @param args Optional arguments like "help".
     */
    public void registerHelpListCommand(String name, String[] aliases, String description, boolean useParentHelp, String... args) {

        CommandHelpListHandler helpListCommand = new CommandHelpListHandler(this, name, description, args);
        helpListCommand.setAliases(aliases);

        registerCommand(helpListCommand, helpListCommand.getOnCommandMethod(), useParentHelp);
    }


    /**
     * Called to register the reload command when the plugin is being loaded.
     * @param description Description of the command.
     * @param permission Permission of the command.
     */
    protected void onRegisterReloadCommand(String description, String permission) {
        registerLocalCommand(mainAliases, description, permission, "onReloadCommand", "reload");
    }


    /**
     * Called to register the version command when the plugin is being loaded.
     * @param description Description of the command.
     * @param permission Permission of the command.
     */
    protected void onRegisterVersionCommand(String description, String permission) {
        registerLocalCommand(mainAliases, description, permission, "onVersionCommand", "version");
    }


    private void registerLocalCommand(String[] aliases, String description, String permission, String methodName, String... args) {
        registerLocalCommand(aliases, description, permission, methodName, false, args);
    }


    private void registerLocalCommand(String[] aliases, String description, String permission, String methodName, boolean useParentHelp, String... args) {
        CustomCommand command = new CustomCommand(this, getDescription().getName().toLowerCase(), description, true, args);
        command.setAliases(aliases);

        command.setPermission(permission);

        try {
            Method m = RedFrogPlugin.class.getMethod(methodName, CommandSender.class);
            m.setAccessible(true);

            CommandExecutor executor = new CommandExecutor(this, command, m, getDescription().getName(), this, useParentHelp);


            commandExecutors.add(executor);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to register a List Command.
     * @param command Command to be registered.
     * @return
     */
    public CommandExecutor registerCommand(ListCommand command) {
        CommandExecutor executor = new CommandExecutor(this, command, command.getOnCommandMethod(), getDescription().getName(), command, false);
        command.getOnCommandMethod().setAccessible(true);

        if (command.name().equals(""))
            command.setName(getDescription().getName().toLowerCase());

        commandExecutors.add(executor);

        return executor;
    }


    /**
     * Used to register a CustomCommand.
     * @param command Command to be registered.
     * @param method Method that command will execute..
     * @return
     */
    public CommandExecutor registerCommand(CustomCommand command, Method method) {
        return registerCommand(command, method, command, false);
    }


    /**
     * Used to register a CustomCommand.
     * @param command Command to be registered.
     * @param method Method that command will execute..
     * @param useParentHelp Indicate if the command is going to use the parent's help list.
     *                      This is only needed if the command is a help list.
     * @return
     */
    public CommandExecutor registerCommand(CustomCommand command, Method method, boolean useParentHelp) {
        return registerCommand(command, method, command, useParentHelp);
    }

    /**
     * Used to register a command with a different command class.
     * @param command Command to be registered.
     * @param method Method that command will execute.
     * @param cmdClass Class of the command.
     * @return
     */
    public CommandExecutor registerCommand(CustomCommand command, Method method, CommandClass cmdClass) {
        return registerCommand(command, method, cmdClass, false);
    }


    /**
     * Used to register a command.
     * @param command Command to be registered.
     * @param method Method that command will execute.
     * @param cmdClass Class of the command.
     * @param useParentHelp Indicate if the command is going to use the parent's help list.
     *                      This is only needed if the command is a help list.
     * @return
     */
    public CommandExecutor registerCommand(CustomCommand command, Method method, CommandClass cmdClass, boolean useParentHelp) {
        CommandExecutor executor = new CommandExecutor(this, command, method, getDescription().getName(), cmdClass, useParentHelp);
        method.setAccessible(true);

        if (command.name().equals(""))
            command.setName(getDescription().getName().toLowerCase());

        commandExecutors.add(executor);

        return executor;
    }


    /**
     * Register multiple command executors.
     * @param cmds Commands to be registered.
     */
    public void applyCommands(CommandExecutor... cmds) {
        commandExecutors.sort(CommandExecutorComparator.getInstance());
        registerCommands(cmds);
    }


    /**
     * Register a command class.
     * @param commandClass Command class to be registered.
     */
    public final void registerCommandClass(Class<? extends CommandClass> commandClass) {
        Constructor<?> constructor = commandClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        try {
            registerCommandObject((CommandClass) constructor.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Register multiple command classes.
     * @param commandClasses Command classes to be registered.
     */
    public final void registerCommandClasses(Class<? extends CommandClass>... commandClasses) {
        for (int i = 0; i < commandClasses.length; i++) {
            registerCommandClass(commandClasses[i]);
        }
    }



    private ArrayList<String> getEnclosingCommandArgs(Class<? extends CommandClass> commandClass) {
        ArrayList<String> args = new ArrayList<>();
        Boolean isRoot = false;

        if (commandClass.getEnclosingClass() != null && CommandClass.class.isAssignableFrom(commandClass.getEnclosingClass())) {
            args.addAll(getEnclosingCommandArgs((Class<? extends CommandClass>) commandClass.getEnclosingClass()));
        } else {
            isRoot = true;
        }

        if (commandClass.isAnnotationPresent(Command.class)) {
            Command subCommand = commandClass.getAnnotation(Command.class);

            if (!subCommand.name().equals("") || isRoot)
                args.add(subCommand.name());

            args.addAll(Arrays.asList(subCommand.args()));
        } else if (commandClass.isAnnotationPresent(DefaultCommand.class)) {
            args.add("");
        }

        return args;
    }


    private String[] getCommandClassAliases(Class<? extends CommandClass> commandClass) {
        if (commandClass.isAnnotationPresent(Command.class)) {
            Command subCommand = commandClass.getAnnotation(Command.class);
            return subCommand.aliases();
        } else if (commandClass.isAnnotationPresent(DefaultCommand.class)) {
            return mainAliases;
        }

        return new String[0];
    }


    private void registerSubCommandObjects(CommandClass commandObj) {
        Class<?>[] innerClasses = commandObj.getClass().getDeclaredClasses();

        for (Class<?> innerClass : innerClasses) {
            if (CommandClass.class.isAssignableFrom(innerClass)) {
                Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
                constructor.setAccessible(true);

                try {
                    registerCommandObject((CommandClass) constructor.newInstance(commandObj));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Register an instance of a command class.
     * @param commandObj Instance of a command class to be registered.
     */
    public final void registerCommandObject(CommandClass commandObj) {
        Method[] allMethods = commandObj.getClass().getDeclaredMethods();

        String cmdRootName = "";
        boolean hasParent = false;

        ArrayList<String> allArgs = new ArrayList<String>(getEnclosingCommandArgs(commandObj.getClass()));
        String[] classAliases = getCommandClassAliases(commandObj.getClass());

        registerSubCommandObjects(commandObj);

        if (commandObj.getClass().isAnnotationPresent(DefaultCommand.class) || commandObj.getClass().isAnnotationPresent(Command.class)) {
            cmdRootName = allArgs.get(0);
            allArgs.remove(0);
            hasParent = true;
        }

        for (CommandAnnotationData commandAnnotation : commandAnnotations) {
            if (commandObj.getClass().isAnnotationPresent(commandAnnotation.annotation)) {

                Command subCommand = commandObj.getClass().getAnnotation(Command.class);

                if (subCommand == null)
                    subCommand = (Command) commandObj;

                String[] argsArray = allArgs.toArray(new String[0]);

                try {
                    CustomCommand newCommand = commandAnnotation.commandClass.getConstructor(RedFrogPlugin.class).newInstance(this);

                    newCommand.setName(cmdRootName);
                    newCommand.setArgs(argsArray);
                    newCommand.setCanHaveMoreParams(true);
                    newCommand.setParams(subCommand.params());
                    newCommand.setDescription(subCommand.description());
                    newCommand.setAliases(subCommand.aliases());
                    newCommand.setPermission(subCommand.permission());
                    newCommand.setPermissionMessage(subCommand.permissionMessage());

                    registerCommand(newCommand, newCommand.getOnCommandMethod(), commandObj.getClass().isAnnotationPresent(CommandParentHelp.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (final Method method : allMethods) {

            method.setAccessible(true);

            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                ArrayList<String> commandArgs = (ArrayList<String>) allArgs.clone();
                String newName = command.name();
                String aliases[] = command.aliases();

                if (hasParent) {

                    if (!command.name().equals(""))
                        commandArgs.add(command.name());

                    if (aliases.length == 0)
                        aliases = classAliases;

                    newName = cmdRootName;
                }

                commandArgs.addAll(Arrays.asList(command.args()));

                for (int i = 0; i < commandArgs.size(); i++) {
                    if (commandArgs.get(i).trim().equals("")) {
                        commandArgs.remove(i);
                        i--;
                    }
                }

                String[] argsArray = commandArgs.toArray(new String[0]);

                command = new CustomCommand(this, newName, argsArray, command.canHaveMoreParams(), command.params(), command.description(), aliases, command.permission(), command.permissionMessage());

                if (newName.equals(""))
                    ((CustomCommand) command).setName(getDescription().getName().toLowerCase());

                CommandExecutor executor;

                if (Modifier.isStatic(method.getModifiers()))
                    executor = new CommandExecutor(this, command, method, getDescription().getName(), null, false);
                else
                    executor = new CommandExecutor(this, command, method, getDescription().getName(), commandObj, false);

                commandExecutors.add(executor);
            }
        }
    }


    /**
     * Register multiple instances of command classes.
     * @param commandObjs Instances of command classes to be registered.
     */
    public final void registerCommandObjects(CommandClass... commandObjs) {
        for (int i = 0; i < commandObjs.length; i++) {
            registerCommandObject(commandObjs[i]);
        }
    }


    private void registerCommands(CommandExecutor... cmds) {

        List<CommandExecutor> commands = Arrays.asList(cmds);

        try {

            commands.forEach(command -> {
                try {

                    PluginCommand inject = commandConstructor.newInstance(command.getName(), this);
                    inject.setExecutor((who, cmd, label, input) -> {
                        if (input != null && input.length > 0)
                            return commandManager.onCommand(who, cmd, label, input);
                        else
                            return commandManager.onCommand(who, cmd, label);
                    });

                    if (command.getAliases().length > 0)
                        inject.setAliases(Arrays.asList(command.getAliases()));

                    inject.setTabCompleter(commandManager);

                    inject.setDescription(command.getDescription());
                    inject.setUsage(command.getUsage());

                    if (!loadedCommands.contains(command.getName().toLowerCase())) {
                        commandMap.register(getDescription().getName().toLowerCase(), inject);
                        loadedCommands.add(command.getName().toLowerCase());
                    }

                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            });

            commandManager.registerCommands(commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Artificially execute a command by a sender.
     * @param sender Send that will artificially execute a command.
     * @param cmd Command that will be executed.
     */
    public void executeCommand(CommandSender sender, String cmd) {
        commandMap.dispatch(sender, cmd);
    }


    /**
     * Indicates if a command is registered or not.
     * @param cmd Command to check.
     * @return
     */
    public boolean commandExists(String cmd) {
        return commandMap.getCommand(cmd) != null;
    }


    /**
     * Called when the reload command is executed.
     * @param sender Sender that executed the command.
     */
    public void onReloadCommand(CommandSender sender) {
        onReloadStart(sender);

        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);

        onReloadFinish(sender);
    }


    /**
     * Called when the version command is executed.
     * @param sender Sender that executed the command.
     */
    public void onVersionCommand(CommandSender sender) {

        String authorString = "";

        if (getDescription().getAuthors() != null && getDescription().getAuthors().size() > 0)
            authorString = " by " + String.join(", ", getDescription().getAuthors());

        String data = ChatColor.GREEN.toString() + getDescription().getName() +
                ": v" + getDescription().getVersion() + authorString + ".";

        sender.sendMessage(data);
    }


    /**
     * Print to the server console.
     * @param obj Obejct to be printed.
     */
    public final void print(Object obj) {
        getServer().getConsoleSender().sendMessage(CC.format(obj.toString()));
    }


    /**
     * Print to the server console.
     * @param obj Obejct to be printed.
     */
    public final void printSuccess(Object obj) {
        print("&a[" + getName() + "] " + obj.toString());
    }


    /**
     * Print to the server console.
     * @param obj Obejct to be printed.
     */
    public final void printError(Object obj) {
        print("&c[" + getName() + "] " + obj.toString());
    }


    /**
     * Print to the server console.
     * @param obj Obejct to be printed.
     */
    public final void printInfo(Object obj) {
        print("&b[" + getName() + "] " + obj.toString());
    }


    /**
     * Print to the server console.
     * @param obj Obejct to be printed.
     */
    public final void printWarning(Object obj) {
        print("&e[" + getName() + "] " + obj.toString());
    }


    private class CommandAnnotationData {
        public Class<? extends Annotation> annotation;
        public Class<? extends CustomCommand> commandClass;


        public CommandAnnotationData(Class<? extends Annotation> annotation, Class<? extends CustomCommand> commandClass) {
            this.annotation = annotation;
            this.commandClass = commandClass;
        }
    }
}
