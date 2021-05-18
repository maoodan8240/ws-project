package ws.gameServer.system.table;

import ws.common.table.container.RefreshableTableContainerListener;

import java.util.List;

public class RootTcListener implements RefreshableTableContainerListener {
    @Override
    public synchronized void preRefresh() {
    }

    @Override
    public void postRefresh(List<Class<? extends ws.common.table.table.interfaces.Row>> arg0) {

    }

}
