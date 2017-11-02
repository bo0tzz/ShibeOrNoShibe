package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ShibeCommandListener implements Listener {

    private final ShibeOrNoShibe main;
    private final ShibeMorphia morphia;
    private final Map<String, Consumer<CommandMessageReceivedEvent>> commandMap;

    public ShibeCommandListener(ShibeOrNoShibe main, ShibeMorphia morphia) {
        this.main = main;
        this.morphia = morphia;
        this.commandMap = new HashMap<String, Consumer<CommandMessageReceivedEvent>>() {{
            ShibeCommandListener that = ShibeCommandListener.this;
            put("about", that::about);
            put("shibe", that::shibe);
            put("doggo", that::doggo);
        }};
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {

    }

    private void about(CommandMessageReceivedEvent event) {
        event.getChat().sendMessage(SendableTextMessage.markdown(
           ""
        ).build());
    }

    private void shibe(CommandMessageReceivedEvent event) {

    }

    private void doggo(CommandMessageReceivedEvent event) {

    }
}
