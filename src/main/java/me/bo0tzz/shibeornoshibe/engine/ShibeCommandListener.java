package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class ShibeCommandListener implements Listener {

    private final ShibeOrNoShibe main;
    private final ShibeMorphia morphia;

    public ShibeCommandListener(ShibeOrNoShibe main, ShibeMorphia morphia) {
        this.main = main;
        this.morphia = morphia;
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {

    }
}
