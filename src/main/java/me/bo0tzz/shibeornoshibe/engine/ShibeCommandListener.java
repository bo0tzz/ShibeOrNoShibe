package me.bo0tzz.shibeornoshibe.engine;

import me.bo0tzz.shibeornoshibe.ShibeOrNoShibe;
import me.bo0tzz.shibeornoshibe.bean.ShibeUser;
import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;

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
        commandMap.get(event.getCommand()).accept(event);
    }

    private void about(CommandMessageReceivedEvent event) {
        event.getChat().sendMessage(SendableTextMessage.markdown(
           "*Welcome to the Shibe or no Shibe bot!*\n" +
                   "This bot uses a neural net to determine the content of images you send.\n" +
                   "You can tell the bot to tag you when an image of a shibe or a doggo is sent, using the respective commands /shibe and /doggo.\n" +
                   "If you have any questions, problems or feature requests, please do not hesitate to contact @bo0tzz!\n" +
                   "The source of this bot is available on [GitHub](https://github.com/bo0tzz/ShibeOrNoShibe)."
        ).build());
    }

    private void shibe(CommandMessageReceivedEvent event) {
        User user = event.getMessage().getSender();
        ShibeUser shibeUser = morphia.getShibeUser(user.getId());
        boolean oldSetting = shibeUser.isPingShibe();
        shibeUser = shibeUser.setPingShibe(!oldSetting);
        boolean success = morphia.updateUser(shibeUser);
        if (!success) morphia.saveShibeUser(shibeUser);
        if (oldSetting)
            event.getChat().sendMessage("You are no longer subscribed to receive mentions when a shibe image is sent!");
        else
            event.getChat().sendMessage("You are now subscribed to receive mentions when a shibe image is sent!");
    }

    private void doggo(CommandMessageReceivedEvent event) {
        User user = event.getMessage().getSender();
        ShibeUser shibeUser = morphia.getShibeUser(user.getId());
        boolean oldSetting = shibeUser.isPingDoggo();
        shibeUser = shibeUser.setPingDoggo(!oldSetting);
        boolean success = morphia.updateUser(shibeUser);
        if (!success) morphia.saveShibeUser(shibeUser);
        if (oldSetting)
            event.getChat().sendMessage("You are no longer subscribed to receive mentions when a doggo image is sent!");
        else
            event.getChat().sendMessage("You are now subscribed to receive mentions when a doggo image is sent!");
    }
}
