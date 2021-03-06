package tapsi.com.user;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import tapsi.com.data.Client;
import tapsi.com.data.DataHandler;
import tapsi.com.socket.SocketConnector;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class ClientDataProvider
        extends AbstractDataProvider<Client, String> {

    /** Text filter that can be changed separately. */
    private String filterText = "";

    /**
     * Store given client to the backing data service.
     *
     * @param client
     *            the updated or new client
     */
    public void save(Client client) {
        boolean newClient = client.getId() == -1;

        DataHandler.save(client);
        if (newClient) {
            refreshAll();
        } else {
            refreshItem(client);
        }

    }

    /**
     * Delete given client from the backing data service.
     *
     * @param client
     *            the client to be deleted
     */
    public void delete(Client client) {
        DataHandler.delete(client);
        //DataService.get().deleteProduct(client.getId());
        refreshAll();
    }

    /**
     * Sets the filter to use for the this data provider and refreshes data.
     * <p>
     * Filter is compared for client name, availability and category.
     *
     * @param filterText
     *           the text to filter by, never null
     */
    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null");
        //filterText = filterText.toLowerCase();
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        refreshAll();
    }

    @Override
    public Integer getId(Client client) {
        Objects.requireNonNull(client, "Cannot provide an id for a null client.");

        return client.getId();
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<Client, String> t) {
        return (int) fetch(t).count();
    }

    @Override
    public Stream<Client> fetch(Query<Client, String> query) {
        if (filterText.isEmpty()) {
            return DataHandler.getUsers().stream();
        }

        if (filterText.length() == 1) {
            return DataHandler.getUsers().stream().filter(
                    client -> passesFilter(client.getName(), filterText)
                            || passesFilter(client.getAllowed(), filterText));

        } else {
            return DataHandler.getUsers().stream().filter(
                    client -> passesFilter(client.getName(), filterText)
                            || passesFilter(client.getPhoneID(), filterText));
        }
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}

