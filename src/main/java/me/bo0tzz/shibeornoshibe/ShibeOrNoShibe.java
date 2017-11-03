package me.bo0tzz.shibeornoshibe;

import me.bo0tzz.shibeornoshibe.db.ShibeMorphia;
import me.bo0tzz.shibeornoshibe.engine.ShibeCommandListener;
import me.bo0tzz.shibeornoshibe.engine.ShibeListener;
import pro.zackpollard.telegrambot.api.TelegramBot;

public class ShibeOrNoShibe {
    private final TelegramBot bot;
    private final ShibeMorphia morphia;

    public static void main(String[] args) {
        String key = System.getenv("BOT_KEY");
        key = key == null ? args[0] : key;
        new ShibeOrNoShibe(key);
    }

    ShibeOrNoShibe(String key) {
        this.bot = TelegramBot.login(key);
        morphia = new ShibeMorphia();
        bot.getEventsManager().register(new ShibeListener(this, morphia));
        bot.getEventsManager().register(new ShibeCommandListener(this, morphia));
        bot.startUpdates(false);
    }

    public TelegramBot getBot() {
        return bot;
    }
}
