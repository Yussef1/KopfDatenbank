package de.yussef.headdatabase.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class SkullBuilder {

    private Field skullMetaProfileField;
    private Method skullMetaSetProfileMethod;

    public ItemStack createSkull(String displayName, String textureValue, boolean loreVisible, String lore) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName(displayName);

        if(loreVisible) {
            skullMeta.setLore(Arrays.asList(lore));
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", textureValue));

        if(skullMetaProfileField == null && skullMetaSetProfileMethod == null) {
            try {
                skullMetaSetProfileMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                skullMetaSetProfileMethod.setAccessible(true);
            } catch(NoSuchMethodException noSuchMethodException) {

                try {
                    skullMetaProfileField = skullMeta.getClass().getDeclaredField("profile");
                    skullMetaProfileField.setAccessible(true);
                } catch(NoSuchFieldException noSuchFieldException) {
                    noSuchMethodException.printStackTrace();
                }
            }
        }
        try {
            if(skullMetaSetProfileMethod != null) {
                skullMetaSetProfileMethod.invoke(skullMeta, gameProfile);
            }
            if (skullMetaProfileField != null) {
                skullMetaProfileField.set(skullMeta, gameProfile);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }

        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}