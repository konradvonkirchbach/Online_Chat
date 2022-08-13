package chat.server.command.implementation;

import chat.server.command.Command;
import chat.server.dtos.StatisticDto;
import chat.server.message.MessageService;
import chat.server.serverresponses.SuccessResponse;
import chat.server.user.User;
import chat.server.utils.ClientHandler;
import chat.server.utils.SocketHandler;

import java.util.logging.Logger;

public class StatsCommand extends SocketHandler implements Command {

    private Logger logger = Logger.getLogger(StatsCommand.class.getName());

    private ClientHandler clientHandler;

    private MessageService messageService = MessageService.getInstance();

    public StatsCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.socket = clientHandler.getSocket();
    }

    @Override
    public void execute() {
        User user = clientHandler.getAddressee().getUser();
        StatisticDto stats = messageService.getStatistic(clientHandler.getUser(), clientHandler.getAddressee().getUser());
        String message = String.format(SuccessResponse.STATS.getText(), user.getName(),
                stats.getCount(), clientHandler.getUsername(), stats.getFromYou(), user.getName(), stats.getFromUser());

        logger.info("Stats message: " + message);

        writeMessage(message);
    }

}
