package dev.tenacity.ui.altmanager.helpers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.server.ban.HypixelBan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class Alt {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static int stage = -1;
    @Expose
    @SerializedName("uuid")
    public String uuid;
    @Expose
    @SerializedName("username")
    public String username;
    @Expose
    @SerializedName("altState")
    public AltState altState;
    @Expose
    @SerializedName("altType")
    public AltType altType;
    @Expose
    @SerializedName("hypixelBan")
    public HypixelBan hypixelBan;
    @Expose
    @SerializedName("token")
    public String token;

    public ResourceLocation head;
    public boolean headTexture;
    public int headTries;

    public String getType() {
        return altType == null ? "Not logged in" : altType.getName();
    }

    public void login() {
        mc.session = new Session(username, uuid, token, "msa");
    }

    @Getter
    @RequiredArgsConstructor
    public enum AltState {
        @Expose
        @SerializedName("1")
        LOGIN_FAIL(FontUtil.XMARK),

        @Expose
        @SerializedName("2")
        LOGIN_SUCCESS(FontUtil.CHECKMARK);
        private final String icon;
    }

    @Getter
    @RequiredArgsConstructor
    public enum AltType {
        @Expose
        @SerializedName("1")
        MICROSOFT("Microsoft"),

        @Expose
        @SerializedName("2")
        MOJANG("Mojang"),

        @Expose
        @SerializedName("3")
        CRACKED("Cracked");
        private final String name;
    }
}
