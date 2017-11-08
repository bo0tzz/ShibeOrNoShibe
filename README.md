# ShibeOrNoShibe

This is a Telegram bot that will notify you if an image of a shibe or a dog is sent! You can find it at [@ShibeOrNoShibot](t.me/shibeornoshibot) on Telegram.
The easiest way to run it for yourself is by using docker-compose, for example like this:

```version: '2'  
services:  
  ShibeOrNoShibe:  
    image: bo0tzz/shibeornoshibe  
    restart: always  
    links:  
     - mongo  
    environment:  
     - "BOT_KEY=xxxxxxxxx"  
     - "MONGO_IP=mongo"  
  mongo:  
    image: mongo  
    restart: always  
    volumes:  
     - ./mongodb/data:/data/db  
```

## Credits
This bot uses a neural net created by [Vilsol](https://github.com/vilsol), and the [JavaTelegramBot-API](https://github.com/zackpollard/JavaTelegramBot-API).
