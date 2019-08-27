package schednie.logs.appender;

import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.FormattedMessage;
import org.bson.Document;
import org.junit.After;
import org.junit.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

public class MongoAppenderTests {

	private final MongoAppender mongoAppender =
			new MongoAppender("foo", null, null, false, null);

	@Test
	@SuppressWarnings("unchecked")
	public void append_correctInsertPerformed() {

		MongoCollection collectionMock = mock(MongoCollection.class);
		mongoAppender.setLogCollection(collectionMock);
		long epochMilli = Instant.now().toEpochMilli();

		mongoAppender.append(new Log4jLogEvent.Builder()
				.setLevel(Level.DEBUG)
				.setMessage(new FormattedMessage("foobar"))
				.setTimeMillis(epochMilli).build());

		verify(collectionMock, times(1)).insertOne(
				new Document("level", "DEBUG")
						.append("epochMillis", epochMilli)
						.append("message", "foobar")
						.append("throwable", null));
	}

	@After
	public void tearDown() throws Exception {
		LogManager.shutdown();
	}
}