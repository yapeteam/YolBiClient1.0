package dev.tenacity;

import com.labymedia.ultralight.UltralightRenderer;
import dev.tenacity.commands.CommandHandler;
import dev.tenacity.config.ConfigManager;
import dev.tenacity.config.DragManager;
import dev.tenacity.event.EventProtocol;
import dev.tenacity.intent.api.account.IntentAccount;
import dev.tenacity.intent.cloud.CloudDataManager;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.scripting.api.ScriptManager;
import dev.tenacity.ui.altmanager.GuiAltManager;
import dev.tenacity.ui.lunar.ui.MainMenu;
import dev.tenacity.ui.searchbar.SearchBar;
import dev.tenacity.ui.sidegui.SideGUI;
import dev.tenacity.ultralight.HTMLGui;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.client.ReleaseType;
import dev.tenacity.utils.misc.DiscordRPC;
import dev.tenacity.utils.objects.DiscordAccount;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Getter
@Setter
public class YolBi implements Utils {

    public static final YolBi INSTANCE = new YolBi();

    public static String NAME = "YolBi";
    public static String VERSION = "1.3";
    public static ReleaseType RELEASE;

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final File DIRECTORY = new File(mc.mcDataDir, NAME);

    private EventProtocol eventProtocol = new EventProtocol();
    private CloudDataManager cloudDataManager;
    private ExecutorService executorService;
    private SideGUI sideGui;
    private SearchBar searchBar;
    private ModuleCollection moduleCollection;
    private ScriptManager scriptManager;
    private IntentAccount intentAccount;
    private ConfigManager configManager;
    private GuiAltManager altManager;
    private CommandHandler commandHandler;
    private PingerUtils pingerUtils;
    private DiscordRPC discordRPC;
    private DiscordAccount discordAccount;

    public static boolean updateGuiScale;
    public static int prevGuiScale;

    public HTMLGui HTMLGui;
    private UltralightRenderer renderer;


    public void Login() {
        mc.displayGuiScreen(new MainMenu());
    }

    public void StartUp() {
    }

    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.PUBLIC ? " (" + RELEASE.getName() + ")" : "");
    }

    public final Color getClientColor() {
        return new Color(236, 133, 209);
    }

    public final Color getAlternateClientColor() {
        return new Color(123, 124, 255);
    }

    public boolean isEnabled(Class<? extends Module> c) {
        Module m = INSTANCE.moduleCollection.get(c);
        return m != null && m.isEnabled();
    }

    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
}
