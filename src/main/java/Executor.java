import watchers.ChildrenWatcher;
import watchers.TaskCoordinator;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by Mateusz on 29.05.16.
 */
public class Executor implements Runnable, Watcher {

    private final ZooKeeper zooKeeper;
    private final String znode;
    private TaskCoordinator taskCoordinator;
    private ChildrenWatcher childrenWatcher;

    public Executor(String znode,
                    ZooKeeper zooKeeper,
                    String commandToExecute[],
                    Runtime executionContext) throws KeeperException, IOException {

        this.znode = znode;
        this.zooKeeper = zooKeeper;
        zooKeeper.register(this);
        taskCoordinator = new TaskCoordinator(znode, commandToExecute, executionContext);
        childrenWatcher = new ChildrenWatcher(znode,zooKeeper);
        zooKeeper.exists(znode, true, null, this);
    }

    public void run() {
        while (true) {
        }
    }

    public void process(WatchedEvent event) {
        taskCoordinator.process(event); //przekazujemy event do uruchomienia Aplikacji mozna tez zrobic jako StatCallback w .exists ale nie czaje jak jest informacja przekazana o utworzeniu znode
        zooKeeper.exists(znode, true, null, this);//zwracan informacje o znode
        zooKeeper.getChildren(znode, true, childrenWatcher, this); //zwracan liste dzieci, asynchronicznie
    }
}
