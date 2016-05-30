import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateusz on 29.05.16.
 */
public class Main {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        if (args.length < 3) {
            System.err.println("USAGE: Main connectionString znode program [args ...]");
            System.exit(2);
        }
        String connectionString = args[0];
        String znode = args[1];
        String exec[] = new String[args.length - 2];
        System.arraycopy(args, 2, exec, 0, exec.length);
        final ZooKeeper zooKeeper = new ZooKeeper(connectionString, 3000, null);

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(
                new Executor(znode, zooKeeper, exec, Runtime.getRuntime())
        ); //zadanie z asynchronicznym wykonaniem

        final StructurePrinter structurePrinter = new StructurePrinter(zooKeeper); //wypisywanie struktury drzewa
        final Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("What would like to do?\n[P] print structure\n[Q] quit");
            final String input = scanner.next();

            switch (input) {
                case "P":
                    structurePrinter.printStructure(znode);
                    break;
                case "Q":
                    executorService.shutdownNow();//zamykamy asynchronicznego executora
                    executorService.awaitTermination(2, TimeUnit.SECONDS); // czekamy 2 sek
                    System.exit(0); //wychodzimy
                default:
                    System.out.println("Incorrect choice");
            }
        }

    }
}
