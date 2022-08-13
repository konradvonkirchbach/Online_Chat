package chat.server.dtos;

public class StatisticDto {

    private int count;

    private int fromYou;

    private int fromUser;


    public int getCount() {
        return count;
    }

    public int getFromYou() {
        return fromYou;
    }

    public int getFromUser() {
        return fromUser;
    }

    public void increaseCount() {
        count++;
    }

    public void increaseFromYou() {
        fromYou++;
    }

    public void increaseFromUser() {
        fromUser++;
    }
}
