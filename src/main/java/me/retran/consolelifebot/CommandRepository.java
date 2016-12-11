package me.retran.consolelifebot;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by retran on 12/11/16.
 */
public class CommandRepository {
    private final String userName;
    private List<Command> commands;

    @Inject
    public CommandRepository(Configuration configuration,
                             RulesCommand rulesCommand,
                             ListCommand listCommand,
                             AboutCommand aboutCommand) {
        this.userName = configuration.telegramUserName().toLowerCase();
        commands = new ArrayList<Command>();
        commands.add(rulesCommand);
        commands.add(listCommand);
        commands.add(aboutCommand);
    }

    public Command getCommand(String text) {
        text = text.toLowerCase().trim().split(" ")[0];
        if (text.contains("@")) {
            text = text.replace("@" + userName, "");
        }

        for (Command c : commands) {
            if (c.getTemplate().equalsIgnoreCase(text)) {
                return c;
            }
        }

        return null;
    }
}
