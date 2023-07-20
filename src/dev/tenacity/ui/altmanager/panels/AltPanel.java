package dev.tenacity.ui.altmanager.panels;

import dev.tenacity.YolBi;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.altmanager.Panel;
import dev.tenacity.ui.altmanager.helpers.Alt;
import dev.tenacity.ui.altmanager.helpers.AltManagerUtils;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.*;
import dev.tenacity.utils.server.ban.HypixelBan;
import dev.tenacity.utils.tuples.Pair;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class AltPanel extends Panel {
    private final CopyOnWriteArrayList<AltRect> altRects = new CopyOnWriteArrayList<>(), visibleAltRects = new CopyOnWriteArrayList<>();
    private final Scroll scroll = new Scroll();
    private static boolean canDrag = false;
    private boolean altsSelected = false;
    private static final boolean select = false;
    public static AltRect loadingAltRect;
    private static Pair<AltRect, AltRect> shiftClickRange;
    private boolean hoveringScrollBar = false;
    private boolean draggingScrollBar = false;
    public static boolean refreshing = false;

    @Override
    public void initGui() {
        refreshAlts();
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (YolBi.INSTANCE.getAltManager().isTyping()) return;
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            altsSelected = !altsSelected;
            visibleAltRects.forEach(altRect -> altRect.setSelected(altsSelected));
            return;
        }
        if (!altsSelected) return;
        if (keyCode == Keyboard.KEY_DELETE) {
            int count = 0;
            for (AltRect altRect : visibleAltRects) {
                if (!altRect.isSelected()) continue;
                Alt alt = altRect.alt;
                AltManagerUtils.removeAlt(alt);
                count++;
            }
            AltManagerUtils.writeAlts();
            refreshAlts();
            NotificationManager.post(NotificationType.SUCCESS, "Success", "Removed " + count + " alts");
        }
    }

    private boolean needsScrollBar = false;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        setWidth(sr.getScaledWidth() - (getX() + 16));
        setHeight(sr.getScaledHeight() - (16 + 75));
        setY(75);

        super.drawScreen(mouseX, mouseY);


        performShiftClick();

        TextField searchField = YolBi.INSTANCE.getAltManager().searchField;
        boolean filterBanned = YolBi.INSTANCE.getAltManager().filterBanned.isEnabled();
        boolean searching = searchField.isFocused() || !searchField.getText().trim().isEmpty();
        String text = searchField.getText().toLowerCase();

        visibleAltRects.clear();
        altRects.stream().filter(altRect -> {
            Alt alt = altRect.getAlt();
            if (filterBanned && (alt.hypixelBan != null && alt.hypixelBan.getUnbanDate() > System.currentTimeMillis()))
                return false;
            if (searching) {
                return (alt.username != null && alt.username.toLowerCase().contains(text))
                        || (alt.uuid != null && alt.uuid.toLowerCase().contains(text));
            } else {
                return true;
            }
        }).forEach(visibleAltRects::add);

        altsSelected = visibleAltRects.stream().anyMatch(AltRect::isSelected);

        if (refreshing) {
            return;
        }

        if (HoveringUtil.isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY)) {
            scroll.onScroll(35);
        }


        float testWidth = getWidth() - 30;
        int minWidth = 150;
        int startX = 10;
        int altRowCount = -1;
        for (int i = startX; i <= testWidth; i += minWidth) {
            altRowCount++;
        }

        // add 10 cause spacing
        float spaceLeft = testWidth - (altRowCount * (minWidth + 10));

        float altRectWidth = minWidth + (spaceLeft / altRowCount);
        float altRectHeight = 40;

        float altX = getX() + 10;
        float altY = getY() + 10;

        if (needsScrollBar) {
            float scrollHeight = (getHeight() - (10));
            float scrollX = getX() + getWidth() - 15;
            float scrollWidth = 7;
            RoundedUtil.drawRound(scrollX, getY() + 5, scrollWidth, scrollHeight, 3.5f, ColorUtil.tripleColor(37));
            float percent = scroll.getScroll() / scroll.getMaxScroll();

            hoveringScrollBar = HoveringUtil.isHovering(scrollX, getY() + 5 - ((scrollHeight - 60) * percent), scrollWidth, 60, mouseX, mouseY);


            if (draggingScrollBar) {
                float dragPercent = Math.min(1, Math.max(0, (mouseY - (getY() + 5)) / scrollHeight));
                scroll.setRawScroll(-scroll.getMaxScroll() * dragPercent);
            }

            RoundedUtil.drawRound(scrollX, getY() + 5 - ((scrollHeight - 60) * percent), scrollWidth, 60, 3.5f,
                    ColorUtil.tripleColor(hoveringScrollBar ? 65 : 55));
        }


        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(getX(), getY(), getWidth(), getHeight(), 6, Color.BLACK);
        StencilUtil.readStencilBuffer(1);

        for (AltRect altRect : visibleAltRects) {
            altRect.setWidth(altRectWidth);
            altRect.setHeight(altRectHeight);

            if (altX + altRectWidth > getX() + (getWidth() - 10)) {
                altX = getX() + 10;
                altY += altRect.getHeight() + 10;
            }

            altRect.setX(altX);
            altRect.setY((float) (altY + MathUtils.roundToHalf(scroll.getScroll())));

            if (altRect.getY() + altRect.getHeight() > getY() && altRect.getY() < getY() + getHeight()) {
                altRect.setClickable(true);
                altRect.drawScreen(mouseX, mouseY);
            } else {
                altRect.setClickable(false);
            }

            altX += altRectWidth + 10;
        }


        scroll.setMaxScroll(Math.max(0, altY - getY() - getHeight() + altRectHeight + 10));

        needsScrollBar = altY + altRectHeight > getY() + getHeight();

        StencilUtil.uninitStencilBuffer();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!HoveringUtil.isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) && altsSelected) {
            visibleAltRects.forEach(altRect -> altRect.setSelected(false));
            altsSelected = false;
            return;
        }
        if (hoveringScrollBar) {
            draggingScrollBar = true;
        }

        visibleAltRects.forEach(altRect -> altRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        draggingScrollBar = false;
    }

    public void refreshAlts() {
        refreshing = true;
        altRects.clear();
        visibleAltRects.clear();
        for (Alt alt : AltManagerUtils.getAlts()) {
            altRects.add(new AltRect(alt));
        }
        Collections.reverse(altRects);
        refreshing = false;
    }


    @Getter
    @Setter
    public static class AltRect implements Screen {
        private float x, y, width, height;
        private Color backgroundColor = ColorUtil.tripleColor(37);
        private boolean selected, currentAccount, hovering, clickable = true, removeShit;
        private Alt alt;
        private final Animation hoverAnimation = new DecelerateAnimation(250, 1);
        private final Animation selectAnimation = new DecelerateAnimation(100, 1);

        private boolean hoveringCreds = false, showCreds = false;
        private Animation credsAnimation = new DecelerateAnimation(200, 1);

        public AltRect(Alt alt) {
            this.alt = alt;
        }


        @Override
        public void initGui() {

        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }

        @Override
        public void drawScreen(int mouseX, int mouseY) {
            hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
            hoverAnimation.setDuration(hovering ? 200 : 350);

            dragSelection(this, hovering);

            Color rectColor = backgroundColor;
            selectAnimation.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
            if (selected || !selectAnimation.isDone()) {
                float outlineWidth = selectAnimation.getOutput().floatValue();
                Color outlineColor = ColorUtil.interpolateColorC(rectColor.brighter().brighter().brighter(), rectColor.brighter().brighter().brighter(), hoverAnimation.getOutput().floatValue());
                RoundedUtil.drawRound(x - outlineWidth, y - outlineWidth, width + (outlineWidth * 2), height + (outlineWidth * 2), 6, outlineColor);
            }
            RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.interpolateColorC(rectColor, rectColor.brighter(), hoverAnimation.getOutput().floatValue()));

            float credsAnim = credsAnimation.getOutput().floatValue();
            float altSize = height - 10;
            drawAltHead(x + 3, y + height / 2f - altSize / 2f, altSize);

            RenderUtil.scaleStart(x + width / 2f, y + height / 2f, 1 - (.5f * credsAnim));

            int textColor = ColorUtil.applyOpacity(-1, 1 - credsAnim);
            String usernameText = "No username";
            if (alt.username != null) {
                usernameText = tenacityBoldFont16.trimStringToWidth(alt.username, (int) (width - (altSize + 10)));
            }
            float totalHeight = tenacityBoldFont16.getHeight() + 6 + tenacityFont14.getHeight();
            boolean hasBan = false;
            if (alt.hypixelBan != null) {
                HypixelBan ban = alt.hypixelBan;
                long diff = ban.getUnbanDate() - System.currentTimeMillis();
                if (diff > 0 || ban.getUnbanDate() == 0) {
                    hasBan = true;
                    totalHeight += tenacityBoldFont14.getHeight() + 4;
                }
            }


            float usernameY = y + getHeight() / 2f - totalHeight / 2f;
            RenderUtil.resetColor();
            tenacityBoldFont16.drawString(usernameText, x + 3 + altSize + 3.5f, usernameY, textColor);

            float typeX = x + 7 + altSize + tenacityBoldFont16.getStringWidth(usernameText);
            float size = tenacityBoldFont16.getHeight() + 2;
            switch (alt.getType()) {
                case "Microsoft":
                    RenderUtil.drawMicrosoftLogo(typeX + 2, usernameY - 1, size, 1, 1 - credsAnim);
                    break;
                case "Cracked":
                    Color red = YolBi.INSTANCE.getSideGui().getRedBadColor();
                    tenacityBoldFont14.drawString(" Cracked", typeX, usernameY + 1, ColorUtil.applyOpacity(red, 1 - credsAnim));
                    break;
            }

            drawBan(x + altSize + 6.5f, usernameY + tenacityBoldFont16.getHeight() + 6, 1 - credsAnim);
            if (hasBan) {
                usernameY += tenacityBoldFont14.getHeight() + 6;
            }

            RenderUtil.scaleEnd();


            String credsText = (showCreds ? "Hide" : "Show") + " credentials";

            hoveringCreds = HoveringUtil.isHovering(x + 3 + altSize + 1.5f, (usernameY + tenacityBoldFont16.getHeight() + 6) - 2,
                    tenacityFont14.getStringWidth("§n" + credsText) + 4, tenacityFont14.getHeight() + 4, mouseX, mouseY);

            float opacity = showCreds ? (hoveringCreds ? .75f : 0.5f) : hoveringCreds ? .5f : .35f;
            tenacityFont14.drawString("§n" + credsText, x + 3 + altSize + 3.5f,
                    usernameY + tenacityBoldFont16.getHeight() + 6, ColorUtil.applyOpacity(-1, opacity));

            credsAnimation.setDirection(showCreds ? Direction.FORWARDS : Direction.BACKWARDS);

            if (removeShit) return;
            if (YolBi.INSTANCE.getAltManager().currentSessionAlt == alt) {
                Color green = YolBi.INSTANCE.getSideGui().getGreenEnabledColor();
                tenacityBoldFont14.drawString("Logged in", x + width - (tenacityBoldFont14.getStringWidth("Logged in") + 5),
                        y + height - (tenacityBoldFont14.getHeight() + 5), green);
            } else {
                if (loadingAltRect == this) {
                    float iconWidth = iconFont16.getStringWidth(FontUtil.REFRESH);
                    float iconHeight = iconFont16.getHeight();
                    float iconX = x + width - (iconWidth + 5);
                    float iconY = y + height - (iconHeight + 5);
                    RenderUtil.rotateStartReal(iconX + iconWidth / 2f, iconY + iconHeight / 2f - 1, iconWidth, iconHeight, (System.currentTimeMillis() % 1080) / 3f);
                    iconFont16.drawString(FontUtil.REFRESH, iconX, iconY, ColorUtil.applyOpacity(-1, .5f));
                    RenderUtil.rotateEnd();
                } else {
                    Alt.AltState altState = alt.altState;
                    if (altState == null) return;
                    String text = altState.getIcon();
                    iconFont16.drawString(text, x + width - (iconFont16.getStringWidth(text) + 5), y + height - (iconFont16.getHeight() + 5),
                            ColorUtil.applyOpacity(-1, .5f));
                }
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            if (clickable) {
                if (alt == null)
                    return;
                if (hoveringCreds) {
                    showCreds = !showCreds;
                    return;
                }
                if (HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY)) {
                    if (button == 0) {
                        alt.login();
                        YolBi.INSTANCE.getAltManager().currentSessionAlt = alt;
                        YolBi.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                    } else selected = !selected;
                }
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {

        }

        public void drawAltHead(float x, float y, float size) {
            YolBi.INSTANCE.getAltManager().getUtils().getHead(alt);
            GLUtil.startBlend();
            RenderUtil.setAlphaLimit(0);
            mc.getTextureManager().bindTexture(alt.head != null ? alt.head : new ResourceLocation("YolBi/X-Steve.png"));
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
            GLUtil.endBlend();
        }

        public void drawBan(float x, float y, float alpha) {
            if (alt.hypixelBan != null) {
                HypixelBan ban = alt.hypixelBan;
                long diff = ban.getUnbanDate() - System.currentTimeMillis();
                if (diff > 0) {
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000) % 24;
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    String str = diffSeconds + "s";
                    if (diffMinutes > 0) str = diffMinutes + "m " + str;
                    if (diffHours > 0) str = diffHours + "h " + str;
                    if (diffDays > 0) str = diffDays + "d " + str;
                    tenacityFont14.drawString("§lBanned §ron Hypixel for §l" + str, x, y, ColorUtil.applyOpacity(-1, alpha * .5f));
                } else if (ban.getUnbanDate() == 0) {
                    tenacityFont14.drawString("§lBanned §ron Hypixel permanently", x, y, ColorUtil.applyOpacity(-1, alpha * .5f));
                }
            }
        }
    }


    private static AltRect hoveringAlt;

    private static void dragSelection(AltRect altRect, boolean hovering) {
        boolean dragging = Mouse.isButtonDown(0) && canDrag;

        if (!Mouse.isButtonDown(0)) {
            canDrag = false;
        }

        if (dragging && hoveringAlt != altRect && hovering) {
            altRect.selected = !altRect.selected;
            hoveringAlt = altRect;
        }
    }

    private void performShiftClick() {
        if (shiftClickRange != null) {
            AltRect start = shiftClickRange.getFirst();
            AltRect end = shiftClickRange.getSecond();
            int startIndex = visibleAltRects.indexOf(start);
            int endIndex = visibleAltRects.indexOf(end);

            for (int i = Math.min(startIndex, endIndex); i <= Math.max(startIndex, endIndex); i++) {
                if (i != -1) {
                    visibleAltRects.get(i).setSelected(select);
                }
            }

            shiftClickRange = null;
        }
    }
}
