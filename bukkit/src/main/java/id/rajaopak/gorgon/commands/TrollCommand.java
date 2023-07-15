package id.rajaopak.gorgon.commands;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TrollCommand extends BaseCommand {

    @CommandMethod("")
    @CommandPermission("gorgon.troll.banitem")
    public void giveBanItemCommand(@NonNull CommandSender sender, String item) {



    }

}
