package dev.tenacity.ui.altmanager.panels;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.tenacity.YolBi;
import dev.tenacity.ui.altmanager.Panel;
import dev.tenacity.ui.altmanager.helpers.Alt;
import dev.tenacity.ui.altmanager.helpers.AltManagerUtils;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.ui.sidegui.utils.ActionButton;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("DuplicatedCode")
public class LoginPanel extends Panel {

    private final List<ActionButton> actionButtons = new ArrayList<>();
    public final List<TextField> textFields = new ArrayList<>();


    public LoginPanel() {
        setHeight(210);
        actionButtons.add(new ActionButton("Add"));
        actionButtons.add(new ActionButton("MicroLogin"));
        actionButtons.add(new ActionButton("Import from file"));
        textFields.add(new TextField(tenacityFont20));
        textFields.add(new TextField(tenacityFont20));
        for (int i = 0; i < textFields.size(); i++) {
            TextField textField = textFields.get(i);
            switch (i) {
                case 0:
                    textField.setBackgroundText("Name");
                    break;
                case 1:
                    textField.setBackgroundText("uuid");
                    break;
            }
        }
    }

    public static boolean cracked = false;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        textFields.forEach(textField -> textField.keyTyped(typedChar, keyCode));
    }

    private final Animation hoverMicrosoftAnim = new DecelerateAnimation(250, 1);

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        tenacityBoldFont32.drawCenteredString("Login", getX() + getWidth() / 2f, getY() + 3, ColorUtil.applyOpacity(-1, .75f));
        Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0);

        int count = 0;
        int spacing = 8;
        float diff = 35;
        for (TextField textField : textFields) {
            textField.setXPosition(getX() + (diff / 2f));
            textField.setYPosition(getY() + 35 + count);
            textField.setWidth(getWidth() - diff);
            textField.setHeight(22);
            textField.setOutline(noColor);
            textField.setFill(ColorUtil.tripleColor(17));
            textField.setTextAlpha(.35f);
            textField.setMaxStringLength(60);
            textField.drawTextBox();
            count += textField.getHeight() + spacing;
        }

        float actionY = getY() + 98 + 30;
        float actionWidth = 90;
        float buttonSpacing = 10;
        float firstX = getX() + getWidth() / 2f - ((actionButtons.size() * actionWidth) + 20) / 2f;
        int seperation = 0;
        for (ActionButton actionButton : actionButtons) {
            actionButton.setBypass(true);
            actionButton.setColor(ColorUtil.tripleColor(55));
            actionButton.setAlpha(1);
            actionButton.setX(firstX + seperation);
            actionButton.setY(actionY);
            actionButton.setWidth(actionWidth);
            actionButton.setHeight(20);
            actionButton.setFont(tenacityBoldFont22);
            actionButton.setClickAction(() -> {
                switch (actionButton.getName()) {
                    case "MicroLogin":
                        YolBi.INSTANCE.getAltManager().getUtils().MicroLogin();
                        YolBi.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        break;
                    case "Add":
                        Alt alt = new Alt();
                        alt.username = textFields.get(0).getText();
                        alt.uuid = textFields.get(1).getText();
                        alt.altType = Alt.AltType.CRACKED;
                        boolean contain = false;
                        for (Alt alt0 : AltManagerUtils.getAlts()) {
                            if (alt0.username.equals(alt.username)) {
                                contain = true;
                                break;
                            }
                        }
                        if (!contain) {
                            resetTextFields();
                            AltManagerUtils.getAlts().add(alt);
                        } else {
                            NotificationManager.post(NotificationType.WARNING, "Failed to add", "Account already exists", 3);
                        }
                        AltManagerUtils.writeAlts();
                        YolBi.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        break;
                    case "Import from file":
                        Alt alt1 = new Alt();
                        File file = new File(YolBi.DIRECTORY, "token.txt");
                        if (!file.exists()) {
                            NotificationManager.post(NotificationType.WARNING, "File Chooser", "please fiill the file which named -token.txt", 3);
                            break;
                        }
                        try {
                            JsonObject jo = new Gson().fromJson(getStringFromInputStream(new ByteArrayInputStream(Files.readAllBytes(file.toPath()))), JsonObject.class);
                            alt1.username = jo.get("username").getAsString();
                            alt1.uuid = jo.get("uuid").getAsString();
                            alt1.token = jo.get("access_token").getAsString();
                            if (!Objects.equals(alt1.token, ""))
                                alt1.altType = Alt.AltType.MICROSOFT;
                            boolean contain1 = false;
                            for (Alt alt2 : AltManagerUtils.getAlts()) {
                                if (alt2.username.equals(alt1.username)) {
                                    contain1 = true;
                                    break;
                                }
                            }
                            if (!contain1) {
                                AltManagerUtils.getAlts().add(alt1);
                            } else
                                NotificationManager.post(NotificationType.WARNING, "Failed to import", "Account already exists", 3);
                            AltManagerUtils.writeAlts();
                            YolBi.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        } catch (IOException e) {
                            e.printStackTrace();
                            NotificationManager.post(NotificationType.WARNING, "Failed to import", "Failed to import alt from token.txt", 3);
                        }
                        break;
                }
            });


            actionButton.drawScreen(mouseX, mouseY);

            seperation += actionWidth + buttonSpacing;
        }


        float microsoftY = actionY + 35, microWidth = 240, microHeight = 35;
        float microX = getX() + getWidth() / 2f - microWidth / 2f;

        hoverMicrosoftAnim.setDirection(Direction.BACKWARDS);
        mc.getTextureManager().

                bindTexture(new ResourceLocation("YolBi/mc.png"));
        RoundedUtil.drawRoundTextured(microX, microsoftY, microWidth, microHeight, 5, 1);

        RoundedUtil.drawRound(microX, microsoftY, microWidth, microHeight, 5,
                ColorUtil.applyOpacity(Color.BLACK, .2f + (.25f * hoverMicrosoftAnim.getOutput().

                        floatValue())));


        tenacityBoldFont26.drawString("Microsoft Login", microX + 10, microsoftY + 4, -1);

        tenacityFont16.drawString("Login to your migrated account", microX + 10, microsoftY + 23, -1);

        float logoSize = 22;
        RenderUtil.drawMicrosoftLogo(microX + microWidth - (10 + logoSize), microsoftY + (microHeight / 2f) - (logoSize / 2f), logoSize, 1.5f);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textFields.forEach(textField -> textField.mouseClicked(mouseX, mouseY, button));
        actionButtons.forEach(actionButton -> actionButton.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    private void resetTextFields() {
        textFields.forEach(textField -> textField.setText(""));
    }
}
