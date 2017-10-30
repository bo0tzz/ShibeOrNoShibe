package me.bo0tzz.shibeornoshibe;

import pro.zackpollard.telegrambot.api.TelegramBot;

public class ShibeOrNoShibe {
    private final TelegramBot bot;

    public static void main(String[] args) {
        String key = System.getenv("BOT_KEY");
        key = key == null ? args[0] : key;
        new ShibeOrNoShibe(key);
    }

    ShibeOrNoShibe(String key) {
        this.bot = TelegramBot.login(key);
        bot.getEventsManager().register(new ShibeListener(this));
        bot.startUpdates(false);
    }

    public TelegramBot getBot() {
        return bot;
    }
}
