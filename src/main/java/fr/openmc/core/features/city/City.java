package fr.openmc.core.features.city;

import fr.openmc.api.cooldown.DynamicCooldownManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.events.*;
import fr.openmc.core.features.city.models.DBCity;
import fr.openmc.core.features.city.models.DBCityRank;
import fr.openmc.core.features.city.sub.bank.CityBankManager;
import fr.openmc.core.features.city.sub.mascots.MascotsManager;
import fr.openmc.core.features.city.sub.mascots.models.Mascot;
import fr.openmc.core.features.city.sub.mayor.ElectionType;
import fr.openmc.core.features.city.sub.mayor.managers.MayorManager;
import fr.openmc.core.features.city.sub.mayor.models.CityLaw;
import fr.openmc.core.features.city.sub.mayor.models.Mayor;
import fr.openmc.core.features.city.sub.milestone.rewards.RankLimitRewards;
import fr.openmc.core.features.city.sub.notation.NotationManager;
import fr.openmc.core.features.city.sub.notation.models.CityNotation;
import fr.openmc.core.features.city.sub.rank.CityRankManager;
import fr.openmc.core.features.city.sub.war.War;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.utils.CacheOfflinePlayer;
import fr.openmc.core.utils.ChunkPos;
import fr.openmc.core.utils.DateUtils;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static fr.openmc.core.features.city.CityManager.citiesByName;
import static fr.openmc.core.features.city.actions.CityCreateAction.FREE_CLAIMS;

public class City {
    @Getter
    private String name;
    @Getter private final UUID uniqueId;
    private Set<UUID> members;
    private Set<ChunkPos> chunks; // Liste des chunks claims par la ville
    private HashMap<UUID, Set<CityPermission>> permissions;
    private Set<DBCityRank> cityRanks;
    private HashMap<Integer, ItemStack[]> chestContent;
    @Getter
    @Setter
    private UUID chestWatcher;
    @Getter
    private double balance;
    @Getter
    private CityType type;
    @Getter
    private int powerPoints;
    @Getter
    private int freeClaims;
    @Getter
    private int level;

    /**
     * Constructor used for City creation
     */
    public City(UUID uniqueId, String name, Player owner, CityType type, Chunk chunk) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.type = type;
        this.freeClaims = FREE_CLAIMS;
        this.level = 1;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );

        CityManager.registerCity(this);

        this.members = new HashSet<>();
        this.permissions = new HashMap<>();
        this.cityRanks = new HashSet<>();
        this.chunks = new HashSet<>();
        this.chestContent = new HashMap<>();
      
        addChunk(chunk.getX(), chunk.getZ());
      
        addPlayer(owner.getUniqueId());
        addPermission(owner.getUniqueId(), CityPermission.OWNER);
        saveChestContent(1, null);
        CityRankManager.loadCityRanks(this);

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityCreationEvent(this, owner))
        );
    }

    /**
     * Constructor used to deserialize a City database object
     */
    public City(UUID uniqueId, String name, double balance, String type, int power, int freeClaims, int level) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.balance = balance;
        this.freeClaims = freeClaims;
        this.powerPoints = power;
        this.type = CityType.valueOf(type.toUpperCase());
        this.level = level;

        CityManager.registerCity(this);
    }

    /**
     * Serialize a city to be saved in the database
     */
    public DBCity serialize() {
        return new DBCity(uniqueId, name, balance, type.name(), powerPoints, freeClaims, level);
    }

    // ==================== Global Methods ====================

    /**
     * Gets all the member of this city
     */
    public Set<UUID> getMembers() {
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        return this.members;
    }

    /**
     * Gets all the member of this city
     */
    public Set<ChunkPos> getChunks() {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        return this.chunks;
    }

    public void rename(String newName) {

        citiesByName.remove(name);
        citiesByName.put(newName, this);

        this.name = newName;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );
    }

    public void changeType() {
        if (this.type == CityType.WAR) this.type = CityType.PEACE;
        else if (this.type == CityType.PEACE) this.type = CityType.WAR;

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );
    }

    // ==================== Members Methods ====================

    /**
     * Gets the list of online members (UUIDs) of a specific city.
     *
     * @return A list of UUIDs representing the online members of the city.
     */
    public Set<UUID> getOnlineMembers() {
        Set<UUID> allMembers = getMembers();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .filter(allMembers::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Checks if a player is a member of the city.
     *
     * @param player The player to check.
     * @return True if the player is a member, false otherwise.
     */
    public boolean isMember(Player player) {
        return this.getMembers().contains(player.getUniqueId());
    }

    /**
     * Adds a player as a member of a specific city.
     *
     * @param player The UUID of the player to add.
     */
    public void addPlayer(UUID player) {
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        members.add(player);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new MemberJoinEvent(CacheOfflinePlayer.getOfflinePlayer(player), this))
        );
        CityManager.addPlayerToCity(this, player);
    }

    /**
     * Allows a player to leave a city and updates the database and region
     * permissions.
     *
     * @param playerUUID The UUID of the player leaving the city.
     */
    public void removePlayer(UUID playerUUID) {
        if (this.members == null)
            this.members = CityManager.getCityMembers(this);

        OfflinePlayer offlinePlayer = CacheOfflinePlayer.getOfflinePlayer(playerUUID);

        if (offlinePlayer.isOnline() && offlinePlayer instanceof Player player)
            player.closeInventory();

        members.remove(playerUUID);
        permissions.remove(playerUUID);
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
            Bukkit.getPluginManager()
                    .callEvent(new MemberLeaveEvent(CacheOfflinePlayer.getOfflinePlayer(playerUUID), this))
        );
        CityManager.removePlayerFromCity(this, playerUUID);
    }

    /**
     * Changes the owner of a city.
     *
     * @param player The UUID of the new owner.
     */
    public void changeOwner(UUID player) {
        removePermission(getPlayerWithPermission(CityPermission.OWNER), CityPermission.OWNER);
        addPermission(player, CityPermission.OWNER);
    }

    /**
     * Updates the number of free claims of the city
     *
     * @param diff The amount to be added or removed to the existing free claims.
     */
    public void updateFreeClaims(int diff) {
        freeClaims += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );
    }

    // ==================== Chest Methods ====================

    /**
     * Gets the content of a specific chest page for a city.
     *
     * @param page The page number of the chest.
     * @return The content of the chest page as an array of ItemStack.
     */
    public ItemStack[] getChestContent(int page) {
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

        if (page > getChestPages())
            page = getChestPages();

        return chestContent.get(page);
    }

    /**
     * Saves the content of a specific chest page for a city.
     *
     * @param page    The page number of the chest.
     * @param content The content to save as an array of ItemStack.
     */
    public void saveChestContent(int page, ItemStack[] content) {
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

        chestContent.put(page, content);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveChestPage(this, page, content)
        );
    }

    /**
     * Retrieves the number of pages for a city's chest.
     *
     * @return The number of pages for the city's chest.
     */
    public @NotNull Integer getChestPages() {
        if (this.chestContent == null)
            this.chestContent = CityManager.getCityChestContent(this);

        if (this.chestContent.isEmpty())
            saveChestContent(1, null);

        return chestContent.size();
    }

    // ==================== Chunk Methods ====================

    /**
     * Adds a chunk to the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param x The chunk X to be added.
     * @param z The chunk Z to be added.
     */
    public void addChunk(int x, int z) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        ChunkPos chunkPos = new ChunkPos(x, z);
        if (chunks.contains(chunkPos))
            return;
        chunks.add(chunkPos);

        CityManager.claimChunk(this, chunkPos);
    }

    /**
     * Removes a chunk from the city's claimed chunks and updates the database
     * asynchronously.
     *
     * @param chunkX The X coordinate of the chunk to be removed.
     * @param chunkZ The Z coordinate of the chunk to be removed.
     */
    public void removeChunk(int chunkX, int chunkZ) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        chunks.remove(chunkPos);

        CityManager.unclaimChunk(this, chunkPos);
    }

    /**
     * Checks if a specific chunk is claimed by the city.
     *
     * @param x The X coordinate of the chunk to check.
     * @param z The Z coordinate of the chunk to check.
     * @return True if the chunk is claimed, false otherwise.
     */
    public boolean hasChunk(int x, int z) {
        if (this.chunks == null)
            this.chunks = CityManager.getCityChunks(this);

        return chunks.contains(new ChunkPos(x, z));
    }

    // ==================== Economy Methods ====================

    /**
     * Sets the balance for a given City and updates it in the database
     * asynchronously.
     *
     * @param value The new balance value to be set.
     */
    public void setBalance(double value) {
        double before = this.balance;
        this.balance = value;

        // Sauvegarde async
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );

        // Event sync
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityMoneyUpdateEvent(this, before, this.balance))
        );
    }


    /**
     * Updates the balance for a given City by adding a difference amount and
     * updating it in the database asynchronously.
     *
     * @param diff The amount to be added to the existing balance.
     */
    public void updateBalance(double diff) {
        setBalance(balance + diff);
    }

    /**
     * Adds money to the city bank and removes it from {@link Player}
     *
     * @param player The player depositing into the bank
     * @param input  The input string to get the money value
     */
    public void depositCityBank(Player player, String input) {
        CityBankManager.depositCityBank(this, player, input);
    }

    /**
     * Removes money from the city bank and add it to {@link Player}
     *
     * @param player The player withdrawing from the bank
     * @param input  The input string to get the money value
     */
    public void withdrawCityBank(Player player, String input) {
        CityBankManager.withdrawCityBank(this, player, input);
    }

    /**
     * Calculates the interest for the city
     * Interests calculated as proportion not percentage (e.g.: 0.01 = 1%)
     *
     * @return The calculated interest as a double.
     */
    public double calculateCityInterest() {
        return CityBankManager.calculateCityInterest(this);
    }

    /**
     * Applies the interest to the city balance and updates it in the database.
     */
    public void applyCityInterest() {
        CityBankManager.applyCityInterest(this);
    }

    // ==================== Permissions Methods ====================

    /**
     * Retrieves the player with a specific permission.
     *
     * @param permission The permission to check for.
     * @return The UUID of the player with the permission, or null if not found.
     */
    public UUID getPlayerWithPermission(CityPermission permission) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        for (UUID player : permissions.keySet()) {
            if (permissions.get(player).contains(permission)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Retrieves the permissions for a specific player.
     *
     * @param player The UUID of the player to retrieve permissions for.
     * @return A set of permissions for the player.
     */
    public Set<CityPermission> getPermissions(UUID player) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        return permissions.get(player);
    }

    /**
     * Checks if a player has a specific permission.
     *
     * @param uuid       The UUID of the player to check.
     * @param permission The permission to check for.
     * @return True if the player has the permission, false otherwise.
     */
    public boolean hasPermission(UUID uuid, CityPermission permission) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.getOrDefault(uuid, new HashSet<>());
        
        if (playerPerms.contains(CityPermission.OWNER)) {
            return true;
        }

        return playerPerms.contains(CityPermission.OWNER) || playerPerms.contains(permission);
    }

    /**
     * Adds a specific permission to a player and updates the database
     * asynchronously.
     *
     * @param playerUUID       The UUID of the player to add the permission to.
     * @param permission The permission to add.
     */
    public void addPermission(UUID playerUUID, CityPermission permission) {
        if (this.permissions == null)
            this.permissions = CityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.getOrDefault(playerUUID, new HashSet<>());

        if (playerPerms.contains(permission))
            return;

        playerPerms.add(permission);
        permissions.put(playerUUID, playerPerms);

        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.addPlayerPermission(this, playerUUID, permission)
        );

        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
            Bukkit.getPluginManager().callEvent(
                    new CityPermissionChangeEvent(this, CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, true))
        );
    }

    /**
     * Removes a specific permission from a player and updates the database
     * asynchronously.
     *
     * @param playerUUID       The UUID of the player to remove the permission from.
     * @param permission The permission to remove.
     */
    public void removePermission(UUID playerUUID, CityPermission permission) {
        if (this.permissions == null) this.permissions = CityManager.getCityPermissions(this);

        Set<CityPermission> playerPerms = permissions.get(playerUUID);
        
        if (playerPerms == null) return;
        
        if (!playerPerms.contains(permission)) return;

        playerPerms.remove(permission);
        permissions.put(playerUUID, playerPerms);
        
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.removePlayerPermission(this, playerUUID, permission));
        Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                Bukkit.getPluginManager().callEvent(new CityPermissionChangeEvent(this,
                        CacheOfflinePlayer.getOfflinePlayer(playerUUID), permission, false)));
    }

    // ==================== Mascots Methods ====================

    public Mascot getMascot() {
        return MascotsManager.mascotsByCityUUID.get(this.getUniqueId());
    }

    // ==================== Mayor Methods ====================

    /**
     * Retrieves the mayor of the city.
     *
     * @return The mayor of the city, or null if not found.
     */
    public Mayor getMayor() {
        return MayorManager.cityMayor.get(this.getUniqueId());
    }

    /**
     * Checks if the city has a mayor.
     *
     * @return True if the city has a mayor, false otherwise.
     */
    public boolean hasMayor() {
        Mayor mayor = MayorManager.cityMayor.get(this.getUniqueId());
        if (mayor == null) return false;

        return mayor.getMayorUUID() != null;
    }

    /**
     * Retrieves the election type of the city.
     *
     * @return The election type of the city, or null if not found.
     */
    public ElectionType getElectionType() {
        Mayor mayor = MayorManager.cityMayor.get(this.getUniqueId());
        if (mayor == null) return null;

        return mayor.getElectionType();
    }

    /**
     * Retrieves the law of the city.
     *
     * @return The law of the city, or null if not found.
     */
    public CityLaw getLaw() {
        return MayorManager.cityLaws.get(this.getUniqueId());
    }

    // ==================== War Methods ====================

    /**
     * Checks if the city is currently in a war.
     *
     * @return True if the city is in war, false otherwise.
     */
    public boolean isInWar() {
        return WarManager.isCityInWar(this.getUniqueId());
    }

    /**
     * Retrieves the war associated with the city.
     *
     * @return The War object associated with the city or null if not in war.
     */
    public War getWar() {
        return WarManager.getWarByCity(this.getUniqueId());
    }

    /**
     * Checks if the city is immune.
     *
     * @return True if the city is immune, false otherwise.
     */
    public boolean isImmune() {
        if (this.getMascot() == null) return false;

        return this.getMascot().isImmunity() && !DynamicCooldownManager.isReady(this.getUniqueId(), "city:immunity");
    }


    /**
     * Updates the power of a City by adding or removing points.
     *
     * @param diff The amount to be added or remove to the existing power.
     */
    public void updatePowerPoints(int diff) {
        powerPoints += diff;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );
    }
    
    /* =================== RANKS =================== */
    
    /**
     * Retrieves the ranks of the city.
     *
     * @return A set of CityRank objects representing the ranks of the city.
     */
    public Set<DBCityRank> getRanks() {
        return cityRanks;
    }
    
    /**
     * Checks if the city ranks are full.
     *
     * @return True if the city ranks are full, false otherwise.
     */
    public boolean isRanksFull() {
        return cityRanks.size() >= RankLimitRewards.getRankLimit(this.getLevel());
    }
    
    public DBCityRank getRankByName(String rankName) {
        for (DBCityRank rank : cityRanks) {
            if (rank.getName().equalsIgnoreCase(rankName)) {
                return rank;
            }
        }
        return null;
    }
    
    /**
     * Checks if a specific rank exists in the city's ranks.
     *
     * @param rank The CityRank object to check.
     * @return True if the rank exists, false otherwise.
     */
    public boolean isRankExists(DBCityRank rank) {
        return cityRanks.contains(rank);
    }
    
    /**
     * Checks if a specific rank exists by the name in the city's ranks.
     *
     * @param rankName The name of the rank to check.
     * @return True if the rank name exists, false otherwise.
     */
    public boolean isRankExists(String rankName) {
        for (DBCityRank rank : cityRanks) {
            if (rank.getName().equalsIgnoreCase(rankName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Initializes the ranks of the city if they are not already initialized.
     */
    public void initializeRanks() {
        if (cityRanks == null) {
            cityRanks = new HashSet<>();
        }
    }
    
    /**
     * Creates a new rank for the city and saves it asynchronously.
     *
     * @param rank The CityRank object to be created.
     * @throws IllegalStateException if the city already has 18 ranks.
     */
    public void createRank(DBCityRank rank) {
        if (isRanksFull()) {
            throw new IllegalStateException("Cannot add more than 18 ranks to a city.");
        }
        cityRanks.add(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> CityRankManager.addCityRank(rank));
    }
    
    /**
     * Deletes a rank from the city and updates the database asynchronously.
     *
     * @param rank The CityRank object to be deleted.
     * @throws IllegalArgumentException if the rank is not found, or if it is the default rank (priority 0).
     */
    public void deleteRank(DBCityRank rank) {
        if (!cityRanks.contains(rank)) {
            throw new IllegalArgumentException("Rank not found in the city's ranks.");
        }
        if (rank.getPriority() == 0) {
            throw new IllegalArgumentException("Cannot delete the default rank (priority 0).");
        }
        cityRanks.remove(rank);
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> CityRankManager.removeCityRank(rank));
    }
    
    /**
     * Updates a rank in the city and saves it asynchronously.
     *
     * @param oldRank The old CityRank object to be replaced.
     * @param newRank The new CityRank object to replace the old one.
     * @throws IllegalArgumentException if the old rank is not found in the city's ranks.
     */
    public void updateRank(DBCityRank oldRank, DBCityRank newRank) {
        if (cityRanks.contains(oldRank)) {
            Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> CityRankManager.updateCityRank(newRank));
            cityRanks.remove(oldRank);
            cityRanks.add(newRank);
        } else {
            throw new IllegalArgumentException("Old rank not found in the city's ranks.");
        }
    }
    
    /**
     * Retrieves the rank of a specific member in the city.
     *
     * @param member The UUID of the member to check.
     * @return The CityRank object representing the member's rank, or null if not found.
     */
    public DBCityRank getRankOfMember(UUID member) {
        for (DBCityRank rank : cityRanks) {
            if (rank.getMembersSet().contains(member)) {
                return rank;
            }
        }
        return null;
    }

    /**
     * Retrieves the rank of a specific member in the city. (Propriétaire, Maire, ou un grade personalisé)
     *
     * @param member The UUID of the member to check.
     * @return The CityRank object representing the member's rank, or null if not found.
     */
    public String getRankName(UUID member) {
        if (this.hasPermission(member, CityPermission.OWNER)) {
            return "Propriétaire";
        } else if (this.hasMayor() && this.getMayor().getMayorUUID().equals(member)) {
            return "Maire";
        } else {
            for (DBCityRank rank : cityRanks) {
                if (rank.getMembersSet().contains(member)) {
                    return rank.getName();
                }
            }
        }

        return "Membre";
    }
    
    /**
     * Changes the rank of a member in the city.
     *
     * @param sender     The player who is changing the rank.
     * @param playerUUID The UUID of the player whose rank is being changed.
     * @param newRank    The new CityRank to assign to the player.
     * @throws IllegalArgumentException if the specified rank does not exist in the city's ranks.
     */
    public void changeRank(Player sender, UUID playerUUID, DBCityRank newRank) {
        if (!cityRanks.contains(newRank)) {
            throw new IllegalArgumentException("The specified rank does not exist in the city's ranks.");
        }
        
        if (hasPermission(playerUUID, CityPermission.OWNER)) {
            MessagesManager.sendMessage(sender, MessagesManager.Message.PLAYER_IS_OWNER.getMessage(), Prefix.CITY, MessageType.ERROR, false);
            return;
        }
        
        DBCityRank currentRank = getRankOfMember(playerUUID);
        OfflinePlayer player = CacheOfflinePlayer.getOfflinePlayer(playerUUID);
        
        if (currentRank != null) {
            currentRank.removeMember(playerUUID);
            for (CityPermission permission : currentRank.getPermissionsSet()) {
                removePermission(playerUUID, permission);
            }
            MessagesManager.sendMessage(sender, Component.text("§cVous avez retiré le grade §e" + currentRank.getName() + "§c de §6" + player.getName()), Prefix.CITY, MessageType.SUCCESS, true);
        }
        
        if (currentRank != newRank) {
            newRank.addMember(playerUUID);
            for (CityPermission permission : newRank.getPermissionsSet()) {
                addPermission(playerUUID, permission);
            }
            MessagesManager.sendMessage(sender, Component.text("§aVous avez assigné le grade §e" + newRank.getName() + "§a à §6" + player.getName()), Prefix.CITY, MessageType.SUCCESS, true);
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () -> {
            if (currentRank != null) {
                CityRankManager.updateCityRank(currentRank);
            }

            CityRankManager.updateCityRank(newRank);
        });
    }

    /* =================== NOTATION =================== */

    /**
     * Retrieves the notation of the city for a specific week.
     * * @param weekStr The week string in the format "YYYY-WW" (e.g., "2023-01").
     *
     * @return The CityNotation object representing the city's notation for the specified week, or null if not found.
     */
    public CityNotation getNotationOfWeek(String weekStr) {
        if (!NotationManager.notationPerWeek.containsKey(weekStr)) {
            return null;
        }
        return NotationManager.notationPerWeek.get(weekStr).stream()
                .filter(notation -> notation.getCityUUID().equals(this.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public List<CityNotation> getAvailableNotation() {
        if (NotationManager.cityNotations.get(this.getUniqueId()) == null) return List.of();

        return NotationManager.cityNotations.get(this.getUniqueId()).stream()
                .filter(notation -> notation.getCityUUID().equals(this.getUniqueId()))
                .filter(notation -> DateUtils.isBefore(notation.getWeekStr(), DateUtils.getWeekFormat()))
                .toList();
    }

    /**
     * Sets the notation of the city for a specific week.
     *
     * @param weekStr          The week string in the format "YYYY-WW" (e.g., "2023-01").
     * @param architecturalNote The architectural note for the city.
     * @param coherenceNote    The coherence note for the city.
     * @param description      A description of the notation.
     */
    public void setNotationOfWeek(String weekStr, double architecturalNote, double coherenceNote, String description) {
        NotationManager.createOrUpdateNotation(new CityNotation(this.getUniqueId(), architecturalNote, coherenceNote, description, weekStr));
    }

    public void setLevel(int newLevel) {
        this.level = newLevel;
        Bukkit.getScheduler().runTaskAsynchronously(OMCPlugin.getInstance(), () ->
                CityManager.saveCity(this)
        );
    }
}
