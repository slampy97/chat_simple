import org.eclipse.jetty.util.ConcurrentArrayQueue;
import spark.Request;
import spark.Response;

import java.util.Queue;
import java.util.StringJoiner;

import static spark.Spark.*;

public class Server {
    private static final Queue<String> messages = new ConcurrentArrayQueue<>();

    public static String send(Request req, Response res){
        var user = req.queryMap().value("user");
        var msg = req.queryMap().value("message").replace("%20", " ");

        var message = user + ": " + msg;
        messages.add(message);

        return message;
    }


    public static String getAll(Request req, Response res){
        var fromIndex = Integer.parseInt(req.queryMap().value("from"));

        StringJoiner sj = new StringJoiner("\n");
        messages.stream().skip(fromIndex).forEach(sj::add);
        return sj.toString();
    }


    public static void main(String[] argv) {
        get("/send", Server::send);
        get("/getAll", Server::getAll);
    }
}
