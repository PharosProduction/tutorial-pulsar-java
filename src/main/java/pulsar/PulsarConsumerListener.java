package pulsar;

import org.apache.pulsar.client.api.PulsarClientException;

public interface PulsarConsumerListener {
  void messageFetched(String msg) throws PulsarClientException;
}

