package fr.kubys.matchmaking.controller;

/**
 * Called when matchmaking stats change (join, cancel, match found).
 */
public interface StatsListener {
    void onStatsChanged(int waitingCount, int activeMatchCount);
}
