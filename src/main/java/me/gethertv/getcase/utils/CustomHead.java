package me.gethertv.getcase.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.UUID;


public class CustomHead {

    public static ItemStack getCustomTextureHead(String url) {
        ItemStack head;
        try {
            head = new ItemStack(Material.PLAYER_HEAD);
            head.setDurability((short) 3);
        } catch (NoSuchFieldError noSuchFieldError) {
            head = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        String encode = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        String base64 = Base64.getEncoder().encodeToString(encode.getBytes());
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());

        GameProfile profile = new GameProfile(hashAsId, null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Method method = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(skullMeta, new Object[]{profile});
            head.setItemMeta(skullMeta);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {

            head = Bukkit.getUnsafe().modifyItemStack(head, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
        }
        return head;
    }



    /*public static ItemStack getCustomTextureHead(String url) {
        ItemStack head;
        try {
            head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        } catch (NoSuchFieldError noSuchFieldError) {

            head = new ItemStack(Material.valueOf("PLAYER_HEAD"));
            head.setDurability((short) 3);
        }
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        String encode = "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}";
        String base64 = Base64.getEncoder().encodeToString(encode.getBytes());
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());

        GameProfile profile = new GameProfile(hashAsId, null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Method method = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(skullMeta, new Object[]{profile});
            head.setItemMeta(skullMeta);
        } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException exception) {

            head = Bukkit.getUnsafe().modifyItemStack(head, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
        }
        return head;
    }*/


}
