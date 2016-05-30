import com.google.common.base.Strings;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;


/**
 * Created by Mateusz on 29.05.16.
 */
public class StructurePrinter {

    private final ZooKeeper zooKeeper;

    public StructurePrinter(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void printStructure(String znode) throws KeeperException, InterruptedException {
        printStructure(znode, 0);
        System.out.println();
    }

    private void printStructure(String path, int indent) throws KeeperException, InterruptedException {

        System.out.print(
                Strings.repeat("\t", indent)
        );
        try {
            for (String child : zooKeeper.getChildren(path, false)) {
                printStructure(path + "/" + child, indent + 1);
            }
            System.out.println(path);
        }catch (Exception e){
            System.out.println("BRAK ZNODA");
        }
    }
}
