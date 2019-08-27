package schednie.logs.appender;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bson.Document;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simple appender. Inserts each logEvent as Document in MongoDB.
 *
 * @author schednie
 */
@Plugin(name = "MongoAppender",
		category = Core.CATEGORY_NAME,
		elementType = Appender.ELEMENT_TYPE)
public class MongoAppender extends AbstractAppender {

	private final MongoClient mongoClient;
	private MongoCollection<Document> logCollection;
	private final String mongoUrl;

	private final Lock lock = new ReentrantLock();


	protected MongoAppender(final String name, final Filter filter,
							final Layout<? extends Serializable> layout,
							final boolean ignoreExceptions,
							final Property[] properties,
							final String mongoUrl,
							final String mongoDb,
							final String mongoCollection) {

		super(name, filter, layout, ignoreExceptions, properties);

		this.mongoUrl = mongoUrl;
		this.mongoClient = new MongoClient(new MongoClientURI(mongoUrl));
		this.logCollection = this.mongoClient.getDatabase(mongoDb).getCollection(mongoCollection);

		logCollection.createIndex(new Document("message", "text"), new IndexOptions());
	}

	// use this constructor only for class testing. Skips index creation
	MongoAppender(final String name, final Filter filter,
				  final Layout<? extends Serializable> layout,
				  final boolean ignoreExceptions,
				  final Property[] properties) {

		super(name, filter, layout, ignoreExceptions, properties);

		this.mongoUrl = null;
		this.mongoClient = null;
		this.logCollection = null;
	}

	@PluginFactory
	public static MongoAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
			@PluginElement("Filter") Filter filter,
			@PluginAttribute("mongoUrl") String mongoUrl,
			@PluginAttribute("mongoUrl") String mongoDb,
			@PluginAttribute("mongoUrl") String mongoCollection) {

		if (name == null) {
			LOGGER.error("No name provided for MongoAppender");
			return null;
		}
		if (mongoUrl == null) {
			LOGGER.error("No url provided for MongoAppender: mongoUrl");
			return null;
		}
		if (mongoDb == null) {
			LOGGER.error("No database name provided for MongoAppender: mongoDb");
			return null;
		}
		if (mongoCollection == null) {
			LOGGER.error("No collection name provided for MongoAppender: mongoCollection");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}

		return new MongoAppender(name, filter, layout, ignoreExceptions,
				null, mongoUrl, mongoDb, mongoCollection);
	}

	@Override
	public void append(final LogEvent event) {

		lock.lock();
		try {
			this.logCollection.insertOne(
					new Document("level", event.getLevel().toString())
							.append("epochMillis", event.getTimeMillis())
							.append("message", event.getMessage().getFormattedMessage())
							.append("throwable", event.getMessage().getThrowable()));
		}
		catch (Exception e) {
			LOGGER.error("Failed to write to MongoDB", e);
		}
		finally {
			lock.unlock();
		}
	}

	// default scope for testing purposes
	void setLogCollection(final MongoCollection<Document> logCollection) {

		this.logCollection = logCollection;
	}

	public String getMongoUrl() {
		return mongoUrl;
	}

	@Override
	public void stop() {

		super.stop();

		mongoClient.close();
	}
}
