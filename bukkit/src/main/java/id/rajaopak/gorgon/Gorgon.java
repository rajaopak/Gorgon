package id.rajaopak.gorgon;

import cloud.commandframework.CommandTree;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.reflect.ClassPath;
import id.rajaopak.common.OpakLibrary;
import id.rajaopak.common.config.ConfigUpdater;
import id.rajaopak.common.utils.Debug;
import id.rajaopak.common.utils.ChatUtil;
import id.rajaopak.common.utils.VersionChecker;
import id.rajaopak.gorgon.config.ConfigFile;
import id.rajaopak.gorgon.config.LanguageFile;
import id.rajaopak.gorgon.database.Database;
import id.rajaopak.gorgon.database.MySql;
import id.rajaopak.gorgon.listener.JoinListener;
import id.rajaopak.gorgon.manager.HelpMeManager;
import id.rajaopak.gorgon.manager.StaffHelpMeManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;

;

@Getter
public final class Gorgon extends JavaPlugin {

    @Getter
    private static Gorgon instance;
    @Getter
    private static String serverName;

    private final transient Map<String, MessageFormat> messageFormatCache = new HashMap<>();
    @Getter
    private boolean debug;
    private AnnotationParser<CommandSender> annotationParser;
    private PaperCommandManager<CommandSender> manager;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private BukkitAudiences audiences;

    private HelpMeManager helpMeManager;
    private StaffHelpMeManager staffHelpMeManager;

    private Database database;

    private transient ConfigFile configFile;
    private transient ResourceBundle bundle;

    public static String tl(final String path, @Nullable final Object... objects) {
        if (instance == null) {
            return "";
        }

        if (objects.length == 0) {
            return instance.translate(path);
        } else {
            return instance.format(path, objects);
        }
    }

    @Override
    public void onEnable() {
        // Initialize static variable.
        instance = this;

        // Initialize Library
        OpakLibrary.register(this);

        // Initialize config
        this.configFile = new ConfigFile("config.yml", null);
        this.debug = this.configFile.isDebug();

        serverName = this.configFile.getServerName();

        try {
            ConfigUpdater.update(this, "config.yml", new File(this.getDataFolder(), "config.yml"), Collections.emptyList());
            Debug.info("Config is up to date!", true);
        } catch (IOException e) {
            Debug.error("Error while updating config file", e, true);
        }

        if (this.debug) {
            Debug.enable();
        } else {
            Debug.disable();
        }

        // Initialize message service.
        this.bundle = ResourceBundle.getBundle("lang.messages", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        TranslationRegistry registry = TranslationRegistry.create(Key.key("gorgon", "translation"));

        registry.registerAll(Locale.ENGLISH, bundle, false);

        // Initialize database
        MySql mysql = new MySql(this);

        Debug.info("Initialize database...", true);
        long time = System.currentTimeMillis();
        if (mysql.connect()) {
            this.database = mysql;
            this.database.initialize();
            Debug.info("Successfully connect into database! took " + (System.currentTimeMillis() - time) + " ms.", true);
        } else {
            Debug.error("Failed when trying to connect into database!", true);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        // Initialize command and listener
        this.register();

        // Initialize Manager
        this.helpMeManager = new HelpMeManager(this);
        this.staffHelpMeManager = new StaffHelpMeManager(this);

        // Initialize Version Support
        //this.loadMultiVersion();

        // Initialize Events
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);

        Debug.info("Plugin Successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);

        // closing database
        this.database.close();

        // clear message cache
        this.messageFormatCache.clear();

        // clear data manager cache
        this.helpMeManager.clear();
        this.staffHelpMeManager.clear();
    }

    @SneakyThrows
    public void register() {
        Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction = CommandExecutionCoordinator.simpleCoordinator();
        Function<CommandSender, CommandSender> mapperFunction = Function.identity();

        try {
            this.manager = new PaperCommandManager<>(this, executionCoordinatorFunction, mapperFunction, mapperFunction);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Function<ParserParameters, CommandMeta> commandMetaFunction = p ->
                CommandMeta.simple().with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description")).build();

        this.annotationParser = new AnnotationParser<>(this.manager,
                CommandSender.class, commandMetaFunction);

        this.audiences = BukkitAudiences.create(this);

        this.minecraftHelp = new MinecraftHelp<>("/gorgon help",
                this.audiences::sender,
                this.manager);

        if (this.manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.manager.registerBrigadier();
        }

        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }

        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withArgumentParsingHandler()
                .withCommandExecutionHandler()
                .withDecorator(
                        component -> text()
                                .append(text(ChatUtil.color(LanguageFile.getPrefix())))
                                .append(component).build()
                ).apply(this.manager, this.audiences::sender);

        this.minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(5592405),
                TextColor.color(16777045),
                TextColor.color(11184810),
                TextColor.color(5635925),
                TextColor.color(5592405)));

        this.commandRegister();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void commandRegister() {
        Debug.info("Loading and registering commands...");
        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
            for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("id.rajaopak.gorgon.commands")) {
                try {
                    Class<?> commandClass = Class.forName(classInfo.getName());

                    Constructor<?>[] cons = commandClass.getConstructors();

                    for (Constructor<?> constructor : cons) {
                        if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0].isAssignableFrom(Gorgon.class)) {
                            this.parseAnnotationCommands(constructor.newInstance(this));
                        }
                    }
                } catch (Exception e) {
                    Debug.error("Failed loading command class: " + classInfo.getName(), e);
                }
            }
            Debug.info("Finish! " + manager.commands().size() + " Commands has been registered.");
        } catch (IOException e) {
            Debug.error("Failed loading command classes!", e);
        }
    }

    private void loadMultiVersion() {
        this.getLogger().info("Checking for the server version....");

        String version = VersionChecker.getNmsVersion();
        this.getLogger().info("Detecting server version");
        this.getLogger().info("Your server is using version: " + version);
        /*try {
            Class<?> support;
            switch (version) {
                case "v1_16_R1", "v1_16_R2", "v1_16_R3" -> {
                    support = Class.forName("id.rajaopak.versionsupport.VS_16");
                    this.getLogger().info("Loaded multi version 1.16");
                }
                case "v1_17_R1" -> {
                    support = Class.forName("id.rajaopak.versionsupport.VS_17");
                    this.getLogger().info("Loaded multi version 1.17");
                }
                case "v1_18_R1", "v1_18_R2" -> {
                    support = Class.forName("id.rajaopak.versionsupport.VS_18");
                    this.getLogger().info("Loaded multi version 1.18");
                }
                case "1_19_R1", "v1_19_R2", "v1_19_R3" -> {
                    support = Class.forName("id.rajaopak.versionsupport.VS_19");
                    this.getLogger().info("Loaded multi version 1.19");
                }
                default -> {
                    this.getLogger().info("Unsupported server version!");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }

            this.versionSupport = (VersionSupport) support.getConstructor(Class.forName("org.bukkit.plugin.Plugin")).newInstance(this);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }*/
    }

    private void parseAnnotationCommands(Object... clazz) {
        Arrays.stream(clazz).forEach(this.annotationParser::parse);
    }

    private String translate(final String string) {
        return bundle.getString(string);
    }

    public String format(final String string, final Object... objects) {
        String format = translate(string);
        MessageFormat messageFormat = this.messageFormatCache.get(format);

        if (messageFormat == null) {
            try {
                messageFormat = new MessageFormat(format);
            } catch (final IllegalArgumentException e) {
                Debug.error("Invalid Translation key for '" + string + "': " + e.getMessage());
                format = format.replaceAll("\\{(\\D*?)}", "[$1]");
                messageFormat = new MessageFormat(format);
            }
            this.messageFormatCache.put(format, messageFormat);
        }

        return messageFormat.format(objects);
    }

}
