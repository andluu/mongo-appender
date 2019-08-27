package schednie.logs.search;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

/**
 * Provides method for fulltext searching within MongoDB collection.
 * Collection must contain 'text' index!!
 *
 * @author schednie
 */
public class MongoSearch {

	private final String mongoUrl;
	private final String dbName;
	private final String collectionName;

	private final MongoClient mongoClient;
	private MongoCollection<Document> logCollection;


	public MongoSearch(final String mongoUrl, final String dbName,
					   final String collectionName) {

		this.mongoUrl = mongoUrl;
		this.dbName = dbName;
		this.collectionName = collectionName;

		this.mongoClient = new MongoClient(new MongoClientURI(mongoUrl));
		this.logCollection = mongoClient.getDatabase(dbName).getCollection(collectionName);
	}

	/**
	 * Performs fulltext searching through collection of documents
	 * using {@code query} and applying options like
	 * {@code caseSensitive} and {@code diacriticSensitive}.
	 *
	 * @param query query for fulltext search.
	 * See <a href="https://docs.mongodb.com/manual/reference/operator/query/text/#text-query-operator-behavior">docs.mongodb.com</a>
	 * @param caseSensitive A boolean flag to enable or disable case sensitive search.
	 * @return MongoCursor to a search Documents result
	 */
	public MongoCursor<Document> search(String query, boolean caseSensitive) {

		return logCollection.find(new Document("$text",
				new Document("$search", query)
						.append("$caseSensitive", caseSensitive))).cursor();
	}

	// default scope for testing purposes
	void setLogCollection(final MongoCollection<Document> logCollection) {
		this.logCollection = logCollection;
	}

	/**
	 * Merely calls {@linkplain MongoClient#close()}
	 */
	public void close() {
		mongoClient.close();
	}
}
