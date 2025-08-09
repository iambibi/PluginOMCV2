package fr.openmc.core.features.leaderboards;

public record ContributorStats(int added, int removed) {

    public int getNetLines() {
        return added - removed;
    }
}