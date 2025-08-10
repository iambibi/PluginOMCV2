package fr.openmc.core.features.leaderboards;

public record ContributorStats(int added, int removed) {

    public int getTotalLines() {
        System.out.println("Calculating total lines for ContributorStats: added=" + added + ", removed=" + removed);

        System.out.println(Math.abs(added));
        System.out.println(Math.abs(removed));
        System.out.println(Math.abs(added) + Math.abs(removed));

        return Math.abs(added) + Math.abs(removed);
    }
}