package dev.tenacity.module.impl.misc;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.module.settings.impl.StringSetting;

public class IQBoost extends Module {
    private final StringSetting iq = new StringSetting("IQ","NONE");
    //private final NumberSetting iq = new NumberSetting("IQ",0,100000000,-10000000,1);
    public IQBoost() {
        super("IQBoost",Category.MISC,"Boost your IQ");
        addSettings(iq);
    }

    @Override
    public void onEnable(){
        this.setSuffix(iq.getString());

    }



}
