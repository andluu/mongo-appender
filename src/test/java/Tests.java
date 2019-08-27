import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.IndexOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import schednie.logs.search.MongoSearch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;

public class Tests {

	private final MongoSearch mongoSearch = new MongoSearch(MONGO_URL, MONGO_DATABASE, MONGO_COLLECTION);
	private Logger logger = LogManager.getLogger("schednie");
	private final MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGO_URL));

	private static final String MONGO_URL = "mongodb://coral:coral@localhost";
	private static final String MONGO_DATABASE = "mongoAppender";
	private static final String MONGO_COLLECTION = "logs";

	@Before
	public void setUp() throws Exception {

		mongoClient.getDatabase(MONGO_DATABASE).getCollection(MONGO_COLLECTION).drop();
		mongoClient.getDatabase(MONGO_DATABASE).getCollection(MONGO_COLLECTION)
				.createIndex(new Document("message", "text"), new IndexOptions());
	}

	@After
	public void tearDown() throws Exception {

		mongoClient.getDatabase(MONGO_DATABASE).getCollection(MONGO_COLLECTION).drop();
		mongoSearch.close();
		mongoClient.close();
		LogManager.shutdown();
	}

	@Test
	public void search_correctResultReturned() {

		logger.info("foo");
		logger.error("bar");
		logger.warn("foo bar");

		MongoCursor<Document> foo = mongoSearch.search("foo", false);
		MongoCursor<Document> bar = mongoSearch.search("bar", false);
		MongoCursor<Document> foobar = mongoSearch.search("foo bar", false);

		assertTrue(cursorToMessages(foo).containsAll(Arrays.asList("foo", "foo bar")));
		assertTrue(cursorToMessages(bar).containsAll(Arrays.asList("bar", "foo bar")));
		assertTrue(cursorToMessages(foobar).containsAll(Arrays.asList("foo bar")));
	}

	private Collection<String> cursorToMessages(MongoCursor<Document> cursor) {

		Collection<String> messages = new HashSet<>();
		while (cursor.hasNext())
			messages.add(cursor.next().getString("message"));
		return messages;
	}

}
