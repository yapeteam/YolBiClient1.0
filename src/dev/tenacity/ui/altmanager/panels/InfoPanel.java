package dev.tenacity.ui.altmanager.panels;

import dev.tenacity.ui.altmanager.Panel;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends Panel {

    private final List<Pair<String, String>> controlInfo = new ArrayList<>();

    public InfoPanel() {
        setHeight(95);
        controlInfo.add(Pair.of("LEFT-CLICK", "Login the alt"));
        controlInfo.add(Pair.of("RIGHT-CLICK", "select the alt"));
        controlInfo.add(Pair.of("DELETE", "When an alt is selected, you can delete it by pressing the delete key"));
        controlInfo.add(Pair.of("CTRL+A", "Selects the entire alt list"));
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        tenacityBoldFont32.drawCenteredString("Information", getX() + getWidth() / 2f, getY() + 3, ColorUtil.applyOpacity(-1, .75f));

        float controlY = getY() + tenacityBoldFont32.getHeight() + 8;
        for (Pair<String, String> control : controlInfo) {
            tenacityBoldFont18.drawString(control.getFirst() + " -", getX() + 12, controlY, ColorUtil.applyOpacity(-1, .5f));
            tenacityFont18.drawString(control.getSecond(), getX() +
                    tenacityBoldFont18.getStringWidth(control.getFirst() + " -") + 14, controlY, ColorUtil.applyOpacity(-1, .35f));

            controlY += tenacityBoldFont18.getHeight() + 6;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
