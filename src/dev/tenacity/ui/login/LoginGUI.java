package dev.tenacity.ui.login;

import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import dev.tenacity.YolBi;
import dev.tenacity.ui.lunar.ui.MainMenu;
import dev.tenacity.utils.client.ReleaseType;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static net.minecraft.client.main.Main.OsMame;


@SuppressWarnings({"DuplicatedCode", "unused"})
public class LoginGUI extends GuiScreen {
    int alpha = 255;

    private boolean i;
    private boolean logined;

    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private String url = "https://gitee.com/yuxiangll/lorraine/raw/master/YolBiClientUser";

    public CustomFont tPAID = tenacityFont24;
    private static String thePlayerAccountID = "";
    boolean lognfaled = false;
    int popxp = 0;
    int popsze = 0;
    long poptime = 0L;
    float test = width / 3f;
    private final ResourceLocation backgroundResource = new ResourceLocation("YolBi/LoginGUI/loginbackground.png");

    public static void setThePlayerAccountID(String thePlayerAccountID1) {
        thePlayerAccountID = thePlayerAccountID1;
    }

    public static String getThePlayerAccountID() {
        return thePlayerAccountID;
    }

    @Override
    public void initGui() {
        //TODO Help macos Dev--yuxiangll,please del this to Release
        if (OsMame.contains("Mac")){
            logined = true;
            lognfaled = false;
            setThePlayerAccountID("yuxiangll");
            YolBi.RELEASE =ReleaseType.MACSPP ;

            this.mc2.displayGuiScreen(new MainMenu());
            return;
        }
        try {
            UIManager.setLookAndFeel(new FlatXcodeDarkIJTheme());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        loginFrame = new LoginFrame(this::login);
        loginFrame.setVisible(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.resetColor();
        mc.getTextureManager().bindTexture(backgroundResource);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
        tenacityBoldFont80.drawCenteredString("YolBi", width / 2f, height / 4f - 30, new Color(255, 255, 255, 255).getRGB());
        //tPAID.drawCenteredString(thePlayerAccountID, width / 2f, height / 3f + 30, new Color(255, 255, 255).getRGB());
        if (lognfaled) {
            if (popxp < test + 240 && poptime + 1000L <= System.currentTimeMillis()) {
                popxp = popxp + 3;
            }
            if (popsze < 200f && poptime + 500L <= System.currentTimeMillis()) {
                popsze = popsze + 3;
            }
            RoundedUtil.drawRoundOutline(popxp, height / 7f, popsze, 30f, 15f, 0.5f, new Color(236, 108, 135, 129), new Color(255, 255, 255));
            tPAID.drawCenteredString("Password ERROR", popxp * 1.5f - 20, height / 7f + 10, new Color(255, 255, 255).getRGB());
        }
        //RenderUtil.drawFastRoundedRect(width / 2f - 70, height / 3f, width / 2f + 70, height / 3f + 60, 20, new Color(255, 255, 255, 79).getRGB());
        if (logined) {
            Gui.drawRect(0, 0, width + 100, height + 100, new Color(0, 0, 0, alpha).getRGB());

            if (i) {
                if (alpha < 255) {
                    alpha += 15;
                }
                if (alpha >= 255) {
                    this.mc2.displayGuiScreen(new MainMenu()); // lunarUI
                }
            } else {
                long startTime = 0L;
                if (alpha > 0 && startTime + 1000L <= System.currentTimeMillis()) {
                    alpha -= 15;
                }
                if (alpha <= 0 && startTime + 5000L <= System.currentTimeMillis()) {
                    i = true;
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        if (YolBi.updateGuiScale) {
            mc.gameSettings.guiScale = YolBi.prevGuiScale;
            YolBi.updateGuiScale = false;
        }
    }


    public void drawBackground(float width, float height, float alpha) throws IOException {
        RenderUtil.resetColor();
        GlStateManager.color(1, 1, 1, alpha);
        mc.getTextureManager().bindTexture(new ResourceLocation("YolBi/LoginGUI/loginbackground_blur.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, width, height, width, height);
    }

    /**
     * Send a GET request to the given URL.
     */
    private static String get(URL url) throws IOException {
        HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
        httpurlconnection.setRequestMethod("GET");
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
        StringBuilder stringbuilder = new StringBuilder();
        String s;

        while ((s = bufferedreader.readLine()) != null) {
            stringbuilder.append(s);
            stringbuilder.append('\r');
        }

        bufferedreader.close();
        return stringbuilder.toString();
    }

    /*Hash+salt**/
    public static String generate(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            assert password != null;
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /*
    校验密码是否正确
    */
    public static boolean verify(String password, String md5) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return Objects.equals(md5Hex(password + salt), new String(cs1));
    }

    /*
    获取十六进制字符串形式的MD5摘要
    */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    private LoginFrame loginFrame;


    private final CloseableHttpClient client = HttpClientBuilder.create().build();

    private boolean requiresRequestBody(String method) {
        return method.equalsIgnoreCase("POST")
                || method.equalsIgnoreCase("PUT")
                || method.equalsIgnoreCase("PATCH")
                || method.equalsIgnoreCase("PROPPATCH")
                || method.equalsIgnoreCase("REPORT");
    }

    /**
     * Reads an {@link InputStream} to a byte array.
     *
     * @param is The InputStream.
     * @return The bytes.
     * @throws IOException If something is bork.
     */
    private byte[] toBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os);
        return os.toByteArray();
    }

    private final ThreadLocal<byte[]> bufferCache = ThreadLocal.withInitial(() -> new byte[32 * 1024]);

    /**
     * Copies the content of an {@link InputStream} to an {@link OutputStream}.
     *
     * @param is The {@link InputStream}.
     * @param os The {@link OutputStream}.
     * @throws IOException If something is bork.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = bufferCache.get();
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected byte[] makeRequest(String method, String url, byte[] body, Map<String, String> headers) throws IOException {
        assert !requiresRequestBody(method) || body != null : "HTTP Method" + method + " requires a body.";

        RequestBuilder builder = RequestBuilder.create(method);
        if (body != null) {
            builder.setEntity(new ByteArrayEntity(body));
        }
        builder.setUri(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        try (CloseableHttpResponse resp = client.execute(builder.build())) {
            int code = resp.getStatusLine().getStatusCode();
            String message = resp.getStatusLine().getReasonPhrase();
            HttpEntity entity = resp.getEntity();
            byte[] respBody = null;
            if (entity != null) {
                try (InputStream is = entity.getContent()) {
                    respBody = toBytes(is);
                }
            }
            return respBody;
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final String APPLICATION_JSON = "application/json";

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

    private static String readResponse(byte[] response) {
        return getStringFromInputStream(new ByteArrayInputStream(response));
    }

    public void login(String username, String password) {
        loginFrame.getLoginButton().setEnabled(false);
        String data;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", APPLICATION_JSON);
            headers.put("Accept", APPLICATION_JSON);
            data = readResponse(makeRequest("GET", url, null, headers));
            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(":")[0].equals(username)) {
                    break;
                }
            }
            if (line == null) {
                JOptionPane.showMessageDialog(loginFrame, "Account Not Found", "Error", JOptionPane.ERROR_MESSAGE);
                loginFrame.getLoginButton().setEnabled(true);
                return;
            }
            String DataPassword = line.split(":")[1];

            if (verify(password, DataPassword)) {
                logined = true;
                lognfaled = false;
                setThePlayerAccountID(username);
                YolBi.RELEASE = line.split(":").length == 3 && line.split(":")[2].equals("Dev") ? ReleaseType.DEV : ReleaseType.PUBLIC;
                loginFrame.setVisible(false);
            } else {
                lognfaled = true;
                JOptionPane.showMessageDialog(loginFrame, "Password Incorrect", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        loginFrame.getLoginButton().setEnabled(true);
    }
}
