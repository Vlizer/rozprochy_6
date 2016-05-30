package watchers;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Mateusz on 29.05.16.
 */
public class TaskCoordinator implements Watcher {

    private final String pathToWatch;
    private final String[] commandToExecute;
    private final Runtime executionContext;

    private Optional<Process> runningProcess = Optional.empty();

    public TaskCoordinator(
            String pathToWatch,
            String[] commandToExecute,
            Runtime executionContext) {

        Preconditions.checkArgument(
                !Strings.isNullOrEmpty(pathToWatch) //sprawdzamy argument
        );
        Preconditions.checkArgument(
                commandToExecute.length != 0        //sprawdzamy argument
        );

        this.pathToWatch = pathToWatch;             //znode
        this.commandToExecute = commandToExecute;   //odpalana aplikacja
        this.executionContext = executionContext;   //Runtime.getRuntime
    }

    public void process(WatchedEvent event) {

        if (!pathToWatch.equals(event.getPath())) { // sprawdzamy czy mamy event o naszym znode
            return;
        }

        final Event.EventType eventType = event.getType(); //pobieramy typ event-u

        switch (eventType) {
            case NodeCreated:               //odpalamy nasza aplikacje
                try {
                    runningProcess = Optional.of(
                            executionContext.exec(commandToExecute)
                    );

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case NodeDeleted:                //usuwamy nasza aplikacje
                if(runningProcess.isPresent()){
                    runningProcess.get().destroy();
                }
                break;
            default:
                break;
        }

    }

}
