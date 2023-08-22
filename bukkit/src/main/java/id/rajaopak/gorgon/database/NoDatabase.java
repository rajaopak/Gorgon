package id.rajaopak.gorgon.database;

import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.module.helpme.HelpMeData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NoDatabase implements Database {
    @Override
    public void initialize() {

    }

    @Override
    public CompletableFuture<Void> setHelpMeData(HelpMeData data) {
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public CompletableFuture<Optional<HelpMeData>> getHelpMeData(UUID uuid) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public CompletableFuture<Optional<List<HelpMeData>>> getHelpMeDataByPlayer(UUID playeruuid) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public CompletableFuture<Optional<List<HelpMeData>>> getHelpMeData(int limit, int offset, FilterState filter) {
        return CompletableFuture.supplyAsync(Optional::empty);
    }

    @Override
    public CompletableFuture<Void> updateHelpMeData(HelpMeData data) {
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public boolean hasHelpMe(UUID uuid) {
        return false;
    }

    @Override
    public int sizeHelpMe(FilterState filter) {
        return 0;
    }

    @Override
    public CompletableFuture<Void> setStaffPoint(UUID uuid, long point) {
        return CompletableFuture.runAsync(() -> {});
    }

    @Override
    public void close() {

    }
}
