package pulsar;

import org.apache.pulsar.client.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarProducer {

  public static void main(String[] args) throws PulsarClientException {
    String serviceUrl = "pulsar://localhost:6650";
    String topicName = "test-topic";

    PulsarProducer producer = new PulsarProducer(serviceUrl, topicName);
    producer.init();

    for (int i = 1; i <= 5; i++) {
      String msg = "Message number " + i;
      producer.send(msg);
    }

    producer.close();
  }

  // Variables

  private final String mServiceUrl;
  private final String mTopicName;
  private Producer<byte[]> mProducer;
  private final Logger mLogger = LoggerFactory.getLogger(PulsarProducer.class);

  // Constructor

  PulsarProducer(String serviceUrl, String topicName) {
    mServiceUrl = serviceUrl;
    mTopicName = topicName;
  }

  // Public

  void init() throws PulsarClientException {
    mLogger.info("Instantiating producer...");

    mProducer = initClient()
        .newProducer()
        .topic(mTopicName)
        .compressionType(CompressionType.LZ4)
        .create();
  }

  void send(String msg) {
    mLogger.info("Producer sending message: " + msg);

    try {
      byte[] msgBytes = msg.getBytes();
      MessageId msgId = mProducer.send(msgBytes);
      mLogger.info("Producer sent message ID: " + msgId);
    } catch (PulsarClientException e) {
      mLogger.info("Producer message error: " + e.getMessage());
    }
  }

  void close() throws PulsarClientException {
    mProducer.close();

    mLogger.info("Producer is closed");
  }

  // Private

  private PulsarClient initClient() throws PulsarClientException {
    return PulsarClient.builder()
        .serviceUrl(mServiceUrl)
        .build();
  }
}
