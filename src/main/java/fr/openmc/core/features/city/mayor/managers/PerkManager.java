package fr.openmc.core.features.city.mayor.managers;

import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.mayor.PerkType;
import fr.openmc.core.features.city.mayor.Perks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PerkManager {
    private static final Random RANDOM = new Random();

    public static Perks getPerkById(int id) {
        for (Perks perks : Perks.values()) {
            if (perks.getId() == id) return perks;
        }
        return null;
    }

    public static List<Perks> getRandomPerksAll() {
        List<Perks> eventPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .toList();

        List<Perks> basicPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .toList();

        Perks selectedEventPerk = eventPerks.get(RANDOM.nextInt(eventPerks.size()));

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        List<Perks> finalSelection = new ArrayList<>();
        finalSelection.add(selectedEventPerk);
        finalSelection.addAll(selectedBasicPerks);

        return finalSelection;
    }

    public static List<Perks> getRandomPerksBasic() {
        List<Perks> basicPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.BASIC)
                .toList();

        List<Perks> selectedBasicPerks = new ArrayList<>();
        while (selectedBasicPerks.size() < 2) {
            Perks randomPerk = basicPerks.get(RANDOM.nextInt(basicPerks.size()));
            if (!selectedBasicPerks.contains(randomPerk)) {
                selectedBasicPerks.add(randomPerk);
            }
        }

        List<Perks> finalSelection = new ArrayList<>();
        finalSelection.addAll(selectedBasicPerks);

        return finalSelection;
    }

    public static Perks getRandomPerkEvent() {
        List<Perks> eventPerks = List.of(Perks.values()).stream()
                .filter(perk -> perk.getType() == PerkType.EVENT)
                .toList();

        return eventPerks.get(RANDOM.nextInt(eventPerks.size()));
    }

    public static boolean hasPerk(City city, int idPerk) {
        if (city.getMayor().getIdPerk1() == idPerk) {
            return true;
        } else if (city.getMayor().getIdPerk2() == idPerk) {
            return true;
        } else if (city.getMayor().getIdPerk3() == idPerk) {
            return true;
        }
        return false;
    }
}
