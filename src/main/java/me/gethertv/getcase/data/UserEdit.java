package me.gethertv.getcase.data;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserEdit {

    private Player player;
    private Inventory inventory;
    private String nameCase;

    private Inventory utilCase;
    private int pageMusic;

    private HashMap<Integer, Double> chanceItem;
    private Sound lastSound;
    private SoundModifyType soundModifyType;
    public UserEdit(Player player, String nameCase) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Edit - "+nameCase));
        this.utilCase = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Utils - "+nameCase));
        this.nameCase = nameCase;
        this.pageMusic = 0;
        chanceItem = new HashMap<>();
    }

    public void refreshChance()
    {
        ItemStack itemStack = new ItemStack(Material.CLOCK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        double chance = 0.0;
        for (double value : chanceItem.values()) {
            chance += value;
        }
        itemMeta.setDisplayName(ColorFixer.addColors("&fChance: &a{chance}".replace("{chance}", String.valueOf(chance))));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(52, itemStack);
    }

    public void openUtilsInv()
    {
        {
            ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&7 "));
            itemStack.setItemMeta(itemMeta);
            for (int i = 0; i < utilCase.getSize(); i++) {
                utilCase.setItem(i, itemStack);
            }
        }

        {
            ItemStack itemStack = new ItemStack(Material.MUSIC_DISC_STAL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&aOpen case - Sound"));
            itemStack.setItemMeta(itemMeta);

            utilCase.setItem(10, itemStack);
        }

        {

            ItemStack itemStack = new ItemStack(Material.MUSIC_DISC_STAL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&cNo key - Sound"));
            itemStack.setItemMeta(itemMeta);

            utilCase.setItem(19, itemStack);
        }

        {
            CaseData caseData = GetCase.getInstance().getCaseData().get(nameCase);
            utilCase.setItem(15, caseData.getKey());
        }

        {

            ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&aAdd line"));
            itemStack.setItemMeta(itemMeta);
            utilCase.setItem(16, itemStack);
        }
        {

            ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&cRemove line"));
            itemStack.setItemMeta(itemMeta);
            utilCase.setItem(25, itemStack);
        }
        {

            ItemStack itemStack = new ItemStack(Material.NAME_TAG);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&fSet displayname"));
            itemStack.setItemMeta(itemMeta);
            utilCase.setItem(14, itemStack);
        }


        player.openInventory(utilCase);
    }

    public void nextPage()
    {
        pageMusic++;
    }

    public void previousPage()
    {
        pageMusic--;
    }



    public void openInv()
    {
        FileConfiguration config = GetCase.getInstance().getConfig();
        for(String key : config.getConfigurationSection("cases."+nameCase+".drop").getKeys(false))
        {
            double chance = Double.parseDouble(config.getString("cases."+nameCase+".drop."+key+".chance"));
            int slot = Integer.parseInt(config.getString("cases."+nameCase+".drop."+key+".slot"));
            ItemStack itemStack = config.getItemStack("cases."+nameCase+".drop."+key+".item").clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            if(itemMeta.getLore()!=null)
                lore.addAll(itemMeta.getLore());

            lore.add("&fChance: &a{chance}%".replace("{chance}", String.valueOf(chance)));
            lore.add("&fSHIFT + RIGHT CLICK &7- &eSET CHANCE");
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);
            chanceItem.put(slot, chance);
            inventory.setItem(slot, itemStack);
        }

        {
            ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors("&aSave items!"));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(53, itemStack);
        }
        refreshChance();
        player.openInventory(inventory);
    }

    public void saveItem() {
        for(int i = 0; i < inventory.getSize(); i++)
        {
            if(inventory.getItem(i)==null)
            {
                GetCase.getInstance().getConfig().set("cases."+nameCase+".drop."+i, null);
                continue;
            }

            if(i==53)
                continue;

            if(i==52)
                continue;

            ItemStack itemStack = inventory.getItem(i).clone();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = itemMeta.getLore();
            if (lore != null && !lore.isEmpty()) {
                lore.removeIf(loreLine -> loreLine.contains("Chance:"));
                lore.removeIf(loreLine -> loreLine.contains("SHIFT + RIGHT CLICK"));

                itemMeta.setLore(ColorFixer.addColors(lore));
                itemStack.setItemMeta(itemMeta);
            }
            GetCase.getInstance().getConfig().set("cases."+nameCase+".drop."+i+".item", itemStack);
            GetCase.getInstance().getConfig().set("cases."+nameCase+".drop."+i+".slot", i);
            GetCase.getInstance().getConfig().set("cases."+nameCase+".drop."+i+".chance", (chanceItem.get(i)!=null) ? chanceItem.get(i) : 0.00);
        }
        GetCase.getInstance().saveConfig();
    }

    public void playerSound(Sound sound)
    {
        if(lastSound!=null)
            player.stopSound(lastSound);

        player.playSound(player.getLocation(), sound, 1F, 1F);
        lastSound = sound;
    }
    public int getPageMusic() {
        return pageMusic;
    }

    public void setSoundModifyType(SoundModifyType soundModifyType) {
        this.soundModifyType = soundModifyType;
    }

    public SoundModifyType getSoundModifyType() {
        return soundModifyType;
    }

    public HashMap<Integer, Double> getChanceItem() {
        return chanceItem;
    }

    public Inventory getUtilCase() {
        return utilCase;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getNameCase() {
        return nameCase;
    }
}
