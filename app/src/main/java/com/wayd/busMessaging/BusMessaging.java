package com.wayd.busMessaging;


        import java.util.ArrayList;
        import java.util.List;

public class BusMessaging
{
    private final List<BusMessagesListener> listeListener = new ArrayList<>();

    public BusMessaging()
    {

    }

    public void sendMessage(MessageBus message){

        for (BusMessagesListener listener:listeListener)
            listener.envoiBusMessage(message);
    }


    public interface BusMessagesListener
    {

        void envoiBusMessage(MessageBus message);
    }

    public void addBusMessageListener(BusMessagesListener e)
    {
        listeListener.add(e);

    }

    public void removeBusMessageListener(BusMessagesListener e)
    {
        listeListener.remove(e);

    }

}
