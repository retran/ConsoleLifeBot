package me.retran.consolelifebot;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
interface Dependencies {
    Configuration configuration();

    TelegramService telegramService();

    Library library();
    
    GameState gameState();
    
    GameProcess gameProcess();
}