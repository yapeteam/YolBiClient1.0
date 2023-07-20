package dev.tenacity.module.impl.misc;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.MacOS;


public class Macos extends Module {
    MacOS macOS = new MacOS();

    public Macos() {
        super("MacOS",Category.MISC,"MacOS");
    }
    @Override
    public void onEnable() {
        mc.displayGuiScreen(macOS);
        this.setEnabled(false);
    }
}
