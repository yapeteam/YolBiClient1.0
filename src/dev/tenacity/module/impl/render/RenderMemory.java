package dev.tenacity.module.impl.render;

import dev.tenacity.YolBi;
import dev.tenacity.event.impl.game.TickEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.Theme;
import dev.tenacity.utils.tuples.Pair;

import java.awt.*;

public class RenderMemory extends Module {
    private final NumberSetting wig = new NumberSetting("Width",1,2,0.01,0.01);
    private Pair<Color, Color> color;
    public RenderMemory() {
        super("RenderMemory",Category.RENDER,"show ur memory on screen");
        addSettings(wig);

    }
    private final Dragging drag = YolBi.INSTANCE.createDrag(this, "RenderMemory", 2, 2);
    private final Animation animation = new DecelerateAnimation(1000, 100);
    private String str;


    @Override
    public void onTickEvent(TickEvent event){
        long usedMemoty = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        long perMem = usedMemoty*100L / Runtime.getRuntime().totalMemory();
        animation.setEndPoint((int)perMem);
        color = Theme.getCurrentTheme().getColors();
    //System.out.println(animation.getOutput().intValue()*100);
    }
    @Override
    public void onEnable(){
        animation.reset();
        super.onEnable();
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        long usedMemoty = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        long perMem = usedMemoty*100L / Runtime.getRuntime().totalMemory();

        str = "Mem:"+ (int)perMem;
        drag.setHeight(20);
        drag.setWidth(200*wig.getValue().floatValue());
        RoundedUtil.drawRoundOutline(drag.getX(),drag.getY(),drag.getWidth(),drag.getHeight(),5,0.01f,new Color(0,0,0,1),Color.WHITE);
        RoundedUtil.drawRoundOutline(drag.getX(),drag.getY(),(int)(animation.getOutput()*2*wig.getValue()),drag.getHeight(),5,0.01f,color.getSecond(),Color.WHITE);
        tenacityFont18.drawCenteredString(str,drag.getX()+20,drag.getY()+6, color.getFirst());
    }

}
