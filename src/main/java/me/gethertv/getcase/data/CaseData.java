package me.gethertv.getcase.data;

import me.gethertv.getcase.GetCase;
import me.gethertv.getcase.utils.ColorFixer;
import me.gethertv.getcase.utils.CustomHead;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class CaseData {

    private String name;

    private ItemStack caseItem;
    private ItemStack key;
    private List<DropData> dropList;
    private Inventory inventory;

    private ItemStack head;
    private Material bgHead;

    private Sound soundOpenCase;
    private Sound soundNoKey;

    public CaseData(String name, ItemStack caseItem, ItemStack key, List<DropData> dropList) {
        this.name = name;
        this.caseItem = caseItem;
        this.key = key;
        this.dropList = dropList;
        inventory = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Case "+name));
        loadFillBg();
        loadDrop();
        loadBg();
        loadFooter();
        FileConfiguration config = GetCase.getInstance().getConfig();
        soundOpenCase = Sound.valueOf(config.getString("cases."+name+".sound.open-case").toUpperCase());
        soundNoKey = Sound.valueOf(config.getString("cases."+name+".sound.no-key").toUpperCase());
    }

    private void loadFillBg() {
        GetCase.getInstance().getBackgroundItems().forEach(bg -> {
            bg.getSlots().forEach(slot -> inventory.setItem(slot, bg.getItemStack()));
        });
    }

    private void loadBg() {
        FileConfiguration config = GetCase.getInstance().getConfig();
        {
            head = CustomHead.getCustomTextureHead(config.getString("cases."+name+".head"));
        }
        bgHead = Material.valueOf(config.getString("cases."+name+".bg-head").toUpperCase());
    }

    private void loadFooter()
    {
        GetCase.getInstance().getSlotsToOpen().forEach(slot -> {
            inventory.setItem(slot, GetCase.ITEM_OPEN_CASE);
        });
    }
    public void giveDrop(Player player) {

        double y = 100.0D;
        double x = 0.0D;
        double chanceWin = 0;
        Random rand = new Random();
        double winTicket = rand.nextDouble() * (y - x) + x;
        for(DropData dropData : dropList) {
            chanceWin+=dropData.getChance();
            if(winTicket < chanceWin)
            {
                if(isInventoryFull(player))
                    player.getLocation().getWorld().dropItemNaturally(player.getLocation(), dropData.getItemStack().clone());
                else
                    player.getInventory().addItem(dropData.getItemStack().clone());

                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
                openPreview(player, dropData.getItemStack().clone());
                return;
            }
        }
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors("&cERROR! Chance is lower than 100%"));
        itemStack.setItemMeta(itemMeta);
        openPreview(player, itemStack);
    }

    private void openPreview(Player player, ItemStack itemStack)
    {
        Inventory inv = Bukkit.createInventory(null, 9, ColorFixer.addColors(GetCase.getInstance().getConfig().getString("lang.reward-title")));

        GetCase.getInstance().getPreviewDrop().put(player.getUniqueId(), inv);

        ItemStack bg1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg1.getItemMeta();
        bgMeta.setDisplayName(ColorFixer.addColors("&7 "));
        bg1.setItemMeta(bgMeta);
        for(int i =0 ;i < inv.getSize(); i++)
            inv.setItem(i, bg1);

        inv.setItem(4, itemStack);

        player.openInventory(inv);
    }

    public boolean isInventoryFull(Player p)
    {
        return p.getInventory().firstEmpty() == -1;
    }

    private void loadDrop()
    {
        for(DropData dropData : dropList)
        {
            inventory.setItem(dropData.getSlot(), dropData.getItemStack().clone());
        }
    }
    public void setCase(Player player, CaseData caseData) {
        Block targetBlock = player.getTargetBlock(null, 5);
        if(targetBlock==null || targetBlock.getType()==Material.AIR)
        {
            player.sendMessage(ColorFixer.addColors("&cYou have to look at the block!"));
            return;
        }
        addCase(player, caseData, targetBlock.getLocation());

    }

    private void addCase(Player player, CaseData caseData, Location location) {

        Location hologram = location.clone();

        String key = getNumberIndex(caseData, location);
        CaseChestData caseChestData = new CaseChestData(key, caseData, location, hologram);

        addHeadLucky(hologram, caseChestData);
        location.getBlock().setType(bgHead);
        GetCase.getInstance().getDataChest().add(caseChestData);
        player.sendMessage(ColorFixer.addColors("&aChests successfully added!"));



    }

    private String getNumberIndex(CaseData caseData, Location location) {
        FileConfiguration config = GetCase.getInstance().getConfig();
        for (int i = 0; i < 50; i++) {
            if(config.isSet("loc-case."+i))
                continue;

            config.set("loc-case."+i+".name", caseData.getName());
            config.set("loc-case."+i+".loc-chest", location);
            GetCase.getInstance().saveConfig();
            return String.valueOf(i);
        }
        return "0";
    }

    public void loadCaseLocation(String id, CaseData caseData, Location location) {

        Location hologram = location.clone();

        CaseChestData caseChestData = new CaseChestData(id, caseData, location, hologram);

        addHeadLucky(hologram, caseChestData);
        location.getBlock().setType(bgHead);
        GetCase.getInstance().getDataChest().add(caseChestData);
    }

    private void addHeadLucky(Location location, CaseChestData caseData) {

        Location loc = location.clone().add(0.5, -0.4, 0.5);
        ArmorStand hologram = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        hologram.setHelmet(head.clone());
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setSmall(true);
        hologram.setCanPickupItems(false);
        hologram.setInvulnerable(true);
        hologram.setBasePlate(false);
        hologram.setMarker(true);
        hologram.setCustomNameVisible(false);
        caseData.setArmorStand(hologram);
    }

    public void setSoundOpenCase(Sound soundOpenCase) {
        this.soundOpenCase = soundOpenCase;
    }

    public void setSoundNoKey(Sound soundNoKey) {
        this.soundNoKey = soundNoKey;
    }

    public void openPreview(Player player)
    {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public ItemStack getKey() {
        return key;
    }

    public ItemStack getCaseItem() {
        return caseItem;
    }

    public Sound getSoundNoKey() {
        return soundNoKey;
    }

    public Sound getSoundOpenCase() {
        return soundOpenCase;
    }

    public List<DropData> getDropList() {
        return dropList;
    }




}
