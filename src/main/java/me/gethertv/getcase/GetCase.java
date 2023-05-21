package me.gethertv.getcase;

import me.gethertv.getcase.cmd.CaseCmd;
import me.gethertv.getcase.data.*;
import me.gethertv.getcase.listeners.BreakBlock;
import me.gethertv.getcase.listeners.InteractEvent;
import me.gethertv.getcase.listeners.InventoryClick;
import me.gethertv.getcase.task.RotateHeadCase;
import me.gethertv.getcase.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class GetCase extends JavaPlugin {

    private static GetCase instance;

    private HashMap<UUID, UserEdit> dataModify = new HashMap<>();
    private Inventory globalModifyInv;

    private HashMap<ItemStack, String> itemData = new HashMap<>();

    private HashMap<String, CaseData> caseData = new HashMap<>();

    private HashMap<UUID, Inventory> previewDrop = new HashMap<>();

    private List<CaseChestData> dataChest = new ArrayList<>();

    private List<Integer> slotsToOpen = new ArrayList<>();
    public static ItemStack ITEM_OPEN_CASE;

    private List<ItemBackground> backgroundItems = new ArrayList<>();

    private HashMap<Integer, Inventory> pageMusicInv = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        loadBackground();
        loadDataPreview();
        loadCases();
        loadCaseLocation();
        loadMusicData();

        getCommand("getcase").setExecutor(new CaseCmd());
        getCommand("getcase").setTabCompleter(new CaseCmd());

        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new InteractEvent(), this);
        getServer().getPluginManager().registerEvents(new BreakBlock(), this);

        new RotateHeadCase().runTaskTimer(this, 0L, 1L);

    }

    @Override
    public void onDisable() {

        for(CaseChestData chestData : GetCase.getInstance().getDataChest())
        {
            chestData.getLocation().getChunk().load(true);

            chestData.getLocation().getBlock().setType(Material.AIR);
            chestData.getArmorStand().remove();
        }

        for(Player player : Bukkit.getOnlinePlayers())
        {
            caseData.forEach((key, value) -> {
                if(player.getOpenInventory().equals(value.getInventory()))
                    player.closeInventory();
            });
            getPreviewDrop().forEach((uuid, inv) -> {
                if(player.getOpenInventory().equals(inv))
                    player.closeInventory();
            });
        }

        HandlerList.unregisterAll(this);
    }

    private void loadMusicData()
    {
//        pageMusicInv
        int slot = 0;
        int page = 0;
        Inventory inv = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Music "+page));
        for (Sound sound : Sound.values()) {
            if(slot==53)
            {
                inv.setItem(slot, getItem(Material.ARROW, "&aNastepna strona"));
                pageMusicInv.put(page, inv);
                page++;
                slot = 0;
                inv = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Music "+page));
            }
            if(slot==45) {
                if(page!=0)
                {
                    inv.setItem(slot, getItem(Material.ARROW, "&ePoprzednia strona"));
                    slot++;
                }
            }

            ItemStack itemStack = new ItemStack(Material.MUSIC_DISC_STAL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(sound.name().toUpperCase());
            List<String> lore = new ArrayList<>();
            lore.add("&7 ");
            lore.add("&fLEFT CLICK - choose sound");
            lore.add("&fRIGHT CLICK - play sound");
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);

            inv.setItem(slot, itemStack);

            slot++;
        }
        if(!GetCase.getInstance().pageMusicInv.containsKey(page))
        {
            pageMusicInv.put(page, inv);
        }


    }

    private ItemStack getItem(Material material, String name)
    {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(name));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    private void loadDataPreview() {

        slotsToOpen.addAll(getConfig().getIntegerList("preview.items.exec.slots"));

        ITEM_OPEN_CASE = new ItemStack(Material.valueOf(getConfig().getString("preview.items.exec.material").toUpperCase()));
        ItemMeta itemMeta = ITEM_OPEN_CASE.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(getConfig().getString("preview.items.exec.displayname")));
        List<String> lore = new ArrayList<>();
        lore.addAll(getConfig().getStringList("preview.items.exec.lore"));
        itemMeta.setLore(ColorFixer.addColors(lore));
        ITEM_OPEN_CASE.setItemMeta(itemMeta);
    }

    private void loadBackground()
    {
        for(String key : getConfig().getConfigurationSection("preview.items.background").getKeys(false))
        {
            ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("preview.items.background."+key+".material").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors(getConfig().getString("preview.items.background."+key+".displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(getConfig().getStringList("preview.items.background."+key+".lore"));
            itemMeta.setLore(ColorFixer.addColors(lore));
            itemStack.setItemMeta(itemMeta);

            List<Integer> slots = new ArrayList<>();
            slots.addAll(getConfig().getIntegerList("preview.items.background."+key+".slots"));

            backgroundItems.add(new ItemBackground(itemStack, slots));
        }
    }

    public void reloadPluginCase()
    {
        reloadConfig();
        for(CaseChestData chestData : GetCase.getInstance().getDataChest())
        {
            chestData.getLocation().getBlock().setType(Material.AIR);
            chestData.getArmorStand().remove();
        }
        for(Player player : Bukkit.getOnlinePlayers())
        {
            caseData.forEach((key, value) -> {
                if(player.getOpenInventory().equals(value.getInventory()))
                    player.closeInventory();
            });
        }
        globalModifyInv.clear();
        itemData.clear();
        caseData.clear();
        previewDrop.clear();
        dataChest.clear();
        slotsToOpen.clear();
        backgroundItems.clear();
        loadBackground();
        loadDataPreview();
        loadCases();
        loadCaseLocation();
    }





    private void loadCaseLocation() {
        if(!getConfig().isSet("loc-case"))
            return;

        for(String key : getConfig().getConfigurationSection("loc-case").getKeys(false))
        {
            CaseData caseInfo = getCaseData().get(getConfig().getString("loc-case."+key+".name"));
            if(caseInfo==null)
                continue;

            caseInfo.loadCaseLocation(key, caseInfo, getConfig().getLocation("loc-case."+key+".loc-chest"));

        }
        return;
    }
    private void loadCases()
    {
        globalModifyInv = Bukkit.createInventory(null, 54, ColorFixer.addColors("&0Cases"));
        for(String nameCase : getConfig().getConfigurationSection("cases").getKeys(false))
        {
            /*
                dodanie do hashmapy itemu ktory bedzie mial przypisana nazwe skrzynii
                przyklad: ItemStack == skrzynia_xyz
             */
            ItemStack caseItem;
            {
                ItemStack itemStack = new ItemStack(Material.valueOf(getConfig().getString("cases."+nameCase+".edit.material").toUpperCase()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ColorFixer.addColors(getConfig().getString("cases."+nameCase+".edit.displayname")));
                List<String> lore = new ArrayList<>();
                lore.addAll(getConfig().getStringList("cases."+nameCase+".edit.lore"));
                itemMeta.setLore(ColorFixer.addColors(lore));
                itemStack.setItemMeta(itemMeta);

                itemData.put(itemStack, nameCase);
                globalModifyInv.addItem(itemStack);
                caseItem = itemStack;
            }

            List<DropData> dropCase = new ArrayList<>();

            /*
                wczytanie dropu
             */
            {
                if(getConfig().isSet("cases."+nameCase+".drop")) {
                    for (String key : getConfig().getConfigurationSection("cases." + nameCase + ".drop").getKeys(false)) {

                        double chance = Double.parseDouble(getConfig().getString("cases." + nameCase + ".drop." + key + ".chance"));
                        int slot = Integer.parseInt(getConfig().getString("cases." + nameCase + ".drop." + key + ".slot"));
                        ItemStack itemStack = getConfig().getItemStack("cases." + nameCase + ".drop." + key + ".item").clone();
                        dropCase.add(new DropData(itemStack, chance, slot));
                    }
                }
            }
            // wczytanie kluczyka
            ItemStack key = new ItemStack(Material.valueOf(getConfig().getString("cases."+nameCase+".key.material").toUpperCase()));
            ItemMeta keyMeta = key.getItemMeta();
            keyMeta.setDisplayName(ColorFixer.addColors(getConfig().getString("cases."+nameCase+".key.displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(getConfig().getStringList("cases."+nameCase+".key.lore"));
            keyMeta.setLore(ColorFixer.addColors(lore));
            key.setItemMeta(keyMeta);

            Collections.sort(dropCase, new Comparator<DropData>() {
                @Override
                public int compare(DropData drop1, DropData drop2) {
                    return Double.compare(drop1.getChance(), drop2.getChance());
                }
            });

            // dodanie do daty
            caseData.put(nameCase, new CaseData(nameCase, caseItem, key, dropCase));
        }
    }


    public HashMap<Integer, Inventory> getPageMusicInv() {
        return pageMusicInv;
    }

    public List<Integer> getSlotsToOpen() {
        return slotsToOpen;
    }

    public List<ItemBackground> getBackgroundItems() {
        return backgroundItems;
    }

    public HashMap<ItemStack, String> getItemData() {
        return itemData;
    }

    public HashMap<String, CaseData> getCaseData() {
        return caseData;
    }

    public HashMap<UUID, UserEdit> getDataModify() {
        return dataModify;
    }

    public Inventory getGlobalModifyInv() {
        return globalModifyInv;
    }

    public static GetCase getInstance() {
        return instance;
    }

    public List<CaseChestData> getDataChest() {
        return dataChest;
    }

    public HashMap<UUID, Inventory> getPreviewDrop() {
        return previewDrop;
    }
}
