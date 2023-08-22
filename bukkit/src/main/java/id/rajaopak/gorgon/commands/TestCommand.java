package id.rajaopak.gorgon.commands;

import id.rajaopak.gorgon.Gorgon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestCommand extends BaseCommand {

    private final Gorgon plugin;
    private List<UUID> test;

    public TestCommand(Gorgon plugin) {
        this.plugin = plugin;
        this.test = new ArrayList<>();
    }

}
