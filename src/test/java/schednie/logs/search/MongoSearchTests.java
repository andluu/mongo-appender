package schednie.logs.search;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MongoSearchTests {

	private final MongoSearch search =
			new MongoSearch("mongodb://foo", "oo", "bar");

	@Test
	public void search_correctSearchQuerySentToCollection() {
		MongoCollection logCollectionMock = mock(MongoCollection.class);
		when(logCollectionMock.find(any(Bson.class))).thenReturn(mock(FindIterable.class));
		search.setLogCollection(logCollectionMock);

		search.search("foobar", false);

		verify(logCollectionMock, times(1)).find(
				new Document("$text",
						new Document("$search", "foobar")
								.append("$caseSensitive", false)));
	}
}