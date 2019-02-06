package pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PulsarConsumer {

  private static final Logger mLogger = LoggerFactory.getLogger(PulsarProducer.class);

  public static void main(String[] args) throws PulsarClientException {
    ArrayList<String> mMessages = new ArrayList<>();
    String serviceUrl = "pulsar://localhost:6650";
    String topicName = "test-topic";

    PulsarConsumer consumer = new PulsarConsumer(serviceUrl, topicName);
    consumer.addListener(msg -> {
      mMessages.add(msg);

      if (mMessages.size() >= 10) {
        mLogger.info(mMessages.toString());

        consumer.close();
      }
    });
    consumer.run();
  }

  // Variables

  private final String mServiceUrl;
  private final String mTopicName;
  private Consumer<byte[]> mConsumer;
  private PulsarConsumerListener mListener;

  // Constructor

  PulsarConsumer(String serviceUrl, String topicName) {
    mServiceUrl = serviceUrl;
    mTopicName = topicName;
  }

  // Public

  void addListener(PulsarConsumerListener listener) {
    mListener = listener;
  }

  void run() throws PulsarClientException {
    assert mListener == null;

    mLogger.info("Instantiating consumer...");

    mConsumer = initClient()
        .newConsumer()
        .topic(mTopicName)
        .subscriptionType(SubscriptionType.Shared)
        .subscriptionName(mTopicName)
        .messageListener(this::readMessage)
        .subscribe();
  }

  void close() throws PulsarClientException {
    mConsumer.close();

    mLogger.info("Consuner is closed");
  }

  // Private

  private PulsarClient initClient() throws PulsarClientException {
    return PulsarClient.builder()
        .serviceUrl(mServiceUrl)
        .build();
  }

  private void readMessage(Consumer<byte[]> consumer, Message msg) {
    try {
      consumer.acknowledge(msg);

      String content = new String(msg.getData());
      mListener.messageFetched(content);

      mLogger.info("Consumer: " + content);
    } catch (PulsarClientException e) {
      mLogger.info("Consumer error: " + e.getMessage());
    }
  }
}
