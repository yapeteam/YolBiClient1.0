package dev.tenacity.module.impl.render;

import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.NumberSetting;

import java.awt.*;

public class ScoreboardMod extends Module {

    public static final NumberSetting yOffset = new NumberSetting("Y Offset", 0, 250, 1, 5);
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", false);
    public static final BooleanSetting textShadow = new BooleanSetting("Text Shadow", true);
    public static final BooleanSetting redNumbers = new BooleanSetting("Red Numbers", false);
    public static final BooleanSetting Round = new BooleanSetting("Round",false);
    public static final BooleanSetting Outline = new BooleanSetting("Outline",false);
    public static final ColorSetting OutlineColor = new ColorSetting("OutlineColor", Color.black);
    public static final NumberSetting Radious = new NumberSetting("Radious",0,100,0,1);
    public static final NumberSetting width = new NumberSetting("Width",0,1000,0,1);
    public static final NumberSetting increase = new NumberSetting("Increase",0,100,0,0.1);
    public static final NumberSetting Outlinethickness = new NumberSetting("Outlinethickness",0,10,-10,0.1);
    public static final NumberSetting Blur = new NumberSetting("Blur",1,100,0,1);


    public ScoreboardMod() {
        super("Scoreboard", Category.RENDER, "Scoreboard preferences");
        Radious.addParent(Round, ParentAttribute.BOOLEAN_CONDITION);
        Outline.addParent(Round,ParentAttribute.BOOLEAN_CONDITION);
        OutlineColor.addParent(Outline,ParentAttribute.BOOLEAN_CONDITION);
        Outlinethickness.addParent(Outline,ParentAttribute.BOOLEAN_CONDITION);
        width.addParent(Outline,ParentAttribute.BOOLEAN_CONDITION);
        increase.addParent(Outline,ParentAttribute.BOOLEAN_CONDITION);
        Blur.addParent(Round,ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(yOffset, customFont, textShadow, redNumbers,Round,Radious,Outline,OutlineColor,Outlinethickness,width,increase,Blur);
        this.setToggled(true);
    }

}
