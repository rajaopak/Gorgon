package id.rajaopak.gorgon.database;

import id.rajaopak.gorgon.enums.FilterState;
import id.rajaopak.gorgon.enums.HelpMeState;
import id.rajaopak.gorgon.object.HelpMeData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void initialize();

    CompletableFuture<Void> setHelpMeData(HelpMeData data);

    CompletableFuture<Optional<HelpMeData>> getHelpMeData(UUID uuid);

    CompletableFuture<Optional<List<HelpMeData>>> getHelpMeDataByPlayer(UUID playeruuid);

    CompletableFuture<Optional<List<HelpMeData>>> getHelpMeData(int limit, int offset, FilterState filter);

    CompletableFuture<Void> updateHelpMeData(HelpMeData data);

    boolean hasHelpMe(UUID uuid);

    int sizeHelpMe(FilterState filter);

    void close();

}
