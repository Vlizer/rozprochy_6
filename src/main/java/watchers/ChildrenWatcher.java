package watchers;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.Objects;

/**
 * Created by Mateusz on 29.05.16.
 */
public class ChildrenWatcher implements AsyncCallback.Children2Callback {

    private final String pathToWatch;
    private final ZooKeeper zooKeeper;

    public ChildrenWatcher(String pathToWatch, ZooKeeper zooKeeper) {

        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(pathToWatch) //sprawdzamy argument
        );

        this.pathToWatch = pathToWatch;
        this.zooKeeper = zooKeeper;
    }

    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        if (!pathToWatch.equals(path) || Objects.isNull(children)) { //sprawdzamy czy przyszla informacja o naszym znode i czy lista dzieci jest null-em
            return;
        }

        System.out.println(
                String.format("Znode %s has %d children: \n", path, countChildren(path))
        );
    }

    private int countChildren(String path) { //zliczamy rekurencyjnie wszystkie dzieci
        int childrenCount = 0;
        List<String> children;
        try {
            children = zooKeeper.getChildren(path,true); //zbieramy dzieci aktualnego znode
        } catch (KeeperException | InterruptedException e) {
            return 0;
        }
        for (String child : children) {
            final String childPath = path + "/" + child;
            childrenCount++;
            childrenCount += countChildren(childPath);
        }
        return childrenCount;
    }


}
