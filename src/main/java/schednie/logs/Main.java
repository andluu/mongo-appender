package schednie.logs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import schednie.logs.search.MongoSearch;

public class Main {
	private static final Logger log = LogManager.getLogger("schednie");
	private static final MongoSearch mongoSearch =
			new MongoSearch("mongodb://coral:coral@localhost", "mongoAppender", "logs");

	public static void main(String[] args) {
		log.trace("Trace message");
		log.debug("Debug message");
		log.info("Info message");
		log.warn("Warn message");
		log.error("Error message");
		log.fatal("Fatal message");

		System.out.println("TRACE");
		mongoSearch.search("trace", false).forEachRemaining(System.out::println);
		System.out.println("DEBUG");
		mongoSearch.search("debug", false).forEachRemaining(System.out::println);
		System.out.println("INFO");
		mongoSearch.search("info", false).forEachRemaining(System.out::println);
		System.out.println("WARN");
		mongoSearch.search("warn", false).forEachRemaining(System.out::println);
		System.out.println("ERROR");
		mongoSearch.search("error", false).forEachRemaining(System.out::println);
		System.out.println("FATAL");
		mongoSearch.search("fatal", false).forEachRemaining(System.out::println);
		System.out.println("MESSAGE");
		mongoSearch.search("message", true).forEachRemaining(System.out::println);
	}
}
