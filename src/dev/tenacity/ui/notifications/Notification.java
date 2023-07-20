package dev.tenacity.ui.notifications;

import dev.tenacity.YolBi;
import dev.tenacity.module.impl.render.PostProcessing;
import dev.tenacity.utils.Utils;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.ShaderUtil;
import dev.tenacity.utils.render.blur.GaussianBlur;
import dev.tenacity.utils.time.TimerUtil;
import dev.tenacity.utils.ui.render.RenderUtils;
import lombok.Getter;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1);
    }


    public void drawDefault(float x, float y, float width, float height, float alpha, boolean onlyTitle) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), .65f), .7f * alpha);


        RoundedUtil.drawRound(x, y, width, height, 4, color);

        Color notificationColor = ColorUtil.applyOpacity(getNotificationType().getColor(), alpha);
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);


        //Icon
        String icon = getNotificationType().getIcon();
        FontUtil.iconFont35.drawString(getNotificationType().getIcon(), x + 5, (y + FontUtil.iconFont35.getMiddleOfBox(height) + 1), notificationColor);

        if (onlyTitle) {
            tenacityBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()),
                    y + tenacityBoldFont22.getMiddleOfBox(height), textColor);
        } else {
            tenacityBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 4, textColor);
            tenacityFont18.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 7 + tenacityBoldFont22.getHeight(), textColor);
        }

    }
    public void drawnewYolBiMode(float x, float y, float width, float height, float alpha, boolean onlyTitle) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(getNotificationType().getColor(), new Color(255, 255, 255), .65f), .7f * alpha);
        //Color color2 = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, new Color(79, 78, 78), .65f), .7f * alpha);
        GaussianBlur.startBlur();
        RoundedUtil.drawRoundOutline(x, y, width, height,alpha,0.01f,new Color(0,0,0,1),color);
        GaussianBlur.endBlur(10*alpha,1);
        RoundedUtil.drawRoundOutline(x, y, width, height,alpha,0.01f,new Color(0,0,0,0),color);
        //RoundedUtil.drawRound(x, y, width, height, 4, color);
        //ShaderUtil.drawQuads(x, y, width, height);
        //RenderUtil.drawFastRoundedRect(x-2, y-2,x+width+2, y+height+2, 0, color2.getRGB());
        //RenderUtil.drawFastRoundedRect(x, y,x+width, y+height, 0, color.getRGB());
        //ShaderUtil.dr

        Color notificationColor =  ColorUtil.applyOpacity(getNotificationType().getColor(), alpha);
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);


        //Icon
        String icon = getNotificationType().getIcon();
        FontUtil.iconFont35.drawString(getNotificationType().getIcon(), x + 5, (y + FontUtil.iconFont35.getMiddleOfBox(height) + 1), notificationColor);

        if (false) {
            tenacityBoldFont22.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()),
                    y + tenacityBoldFont22.getMiddleOfBox(height), textColor);
        } else {
            tenacityBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 4, textColor);
            tenacityFont18.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 7 + tenacityBoldFont22.getHeight(), textColor);
        }

    }
    public void drawYolBiMode(float x, float y, float width, float height, float alpha, boolean onlyTitle) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.WHITE, new Color(255, 255, 255), .65f), .7f * alpha);
        //Color color2 = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, new Color(79, 78, 78), .65f), .7f * alpha);
        GaussianBlur.startBlur();
        RoundedUtil.drawRoundOutline(x, y, width, height,10,0.01f,new Color(0,0,0,1),color);
        GaussianBlur.endBlur(10*alpha,1);
        RoundedUtil.drawRoundOutline(x, y, width, height,10,0.01f,new Color(0,0,0,0),color);
        //RoundedUtil.drawRound(x, y, width, height, 4, color);
        //ShaderUtil.drawQuads(x, y, width, height);
        //RenderUtil.drawFastRoundedRect(x-2, y-2,x+width+2, y+height+2, 0, color2.getRGB());
        //RenderUtil.drawFastRoundedRect(x, y,x+width, y+height, 0, color.getRGB());
        //ShaderUtil.dr

        Color notificationColor =  ColorUtil.applyOpacity(getNotificationType().getColor(), alpha);
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);


        //Icon
        String icon = getNotificationType().getIcon();
        FontUtil.iconFont35.drawString(getNotificationType().getIcon(), x + 5, (y + FontUtil.iconFont35.getMiddleOfBox(height) + 1), notificationColor);

        if (false) {
            tenacityBoldFont22.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()),
                    y + tenacityBoldFont22.getMiddleOfBox(height), textColor);
        } else {
            tenacityBoldFont22.drawString(getTitle(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 4, textColor);
            tenacityFont18.drawString(getDescription(), x + 10 + FontUtil.iconFont35.getStringWidth(getNotificationType().getIcon()), y + 7 + tenacityBoldFont22.getHeight(), textColor);
        }

    }

    public void blurDefault(float x, float y, float width, float height, float alpha, boolean glow) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, getNotificationType().getColor(), glow ? .65f : 0), alpha);
        RoundedUtil.drawRound(x, y, width, height, 4, color);
    }


    public void drawExhi(float x, float y, float width, float height) {
        boolean lowerAlpha = YolBi.INSTANCE.getModuleCollection().getModule(PostProcessing.class).isEnabled();
        Gui.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, lowerAlpha ? 0.4F : .75f).getRGB());
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);
        Gui.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, getNotificationType().getColor().getRGB());
        FontUtil.iconFont40.drawString(getNotificationType().getIcon(), x + 3, (y + FontUtil.iconFont40.getMiddleOfBox(height) + 1), getNotificationType().getColor());

        CustomFont tahomaFont18 = tahomaFont.size(18);
        tahomaFont18.drawString(getTitle(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 4, Color.WHITE);
        tahomaFont.size(14).drawString(getDescription(), x + 7 + FontUtil.iconFont40.getStringWidth(getNotificationType().getIcon()), y + 8.5f + tahomaFont18.getHeight(), Color.WHITE);
    }

    public void blurExhi(float x, float y, float width, float height) {
        Gui.drawRect2(x, y, width, height, Color.BLACK.getRGB());
        RenderUtil.resetColor();
    }

    public void drawSuicideX(float x, float y, float width, float height, float animation) {
        float heightVal = height * animation <= 6 ? 0 : height * animation;
        float yVal = (y + height) - heightVal;

        String editTitle = getTitle() + (getTitle().endsWith(".") || getTitle().endsWith("/") ? " " : ". ") + getDescription();

        tenacityBoldFont22.drawCenteredString(editTitle, x + width /2f,
                yVal + tenacityBoldFont22.getMiddleOfBox(heightVal), ColorUtil.applyOpacity(Color.WHITE, animation - .5f));

    }

    public void blurSuicideX(float x, float y, float width, float height, float animation) {
        float heightVal = height * animation <= 6 ? 0 : height * animation;
        float yVal = (y + height) - heightVal;
        RoundedUtil.drawRound(x, yVal, width, heightVal, 4, Color.BLACK);
    }

}
