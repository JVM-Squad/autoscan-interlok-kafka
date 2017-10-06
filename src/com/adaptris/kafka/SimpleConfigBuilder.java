package com.adaptris.kafka;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.hibernate.validator.constraints.NotBlank;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.CoreException;
import com.adaptris.core.util.Args;
import com.adaptris.core.util.ExceptionHelper;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Simple implementation of {@link ConfigBuilder} for use with {@link KafkaConnection}.
 * 
 * 
 * @author lchan
 * @config kafka-simple-config-builder
 */
@XStreamAlias("kafka-simple-config-builder")
@DisplayOrder(order = {"bootstrapServers", "groupId", "acks", "retries", "compressionType", "bufferMemory"})
public class SimpleConfigBuilder extends ConfigBuilderImpl {

  private static final long DEFAULT_BUFFER_MEM = 33554432L;
  private static final int DEFAULT_RETRIES = 0;
  private static final CompressionType DEFAULT_COMPRESSION_TYPE = ConfigBuilder.CompressionType.none;
  private static final Acks DEFAULT_ACKS = ConfigBuilder.Acks.all;

  @NotBlank
  private String bootstrapServers;

  private String groupId;
  @AdvancedConfig
  private Long bufferMemory;
  @AdvancedConfig
  private CompressionType compressionType;
  @AdvancedConfig
  private Acks acks;
  @AdvancedConfig
  @Min(0)
  private Integer retries;

  public SimpleConfigBuilder() {

  }

  public SimpleConfigBuilder(String bootstrapServers) {
    this();
    setBootstrapServers(bootstrapServers);
  }


  @Override
  public Map<String, Object> build() throws CoreException {
    Map<String, Object> props = new HashMap<>();
    try {
      Args.notBlank(getBootstrapServers(), "bootstrapServers");
      addEntry(props, CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
      addEntry(props, ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
      addEntry(props, ProducerConfig.ACKS_CONFIG, Acks.toConfigValue(getAcks()));
      addEntry(props, ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.toConfigValue(getCompressionType()));
      addEntry(props, ProducerConfig.RETRIES_CONFIG, getRetries());
      addEntry(props, ProducerConfig.BUFFER_MEMORY_CONFIG, getBufferMemory());
      addEntry(props, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, DEFAULT_KEY_SERIALIZER);
      addEntry(props, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DEFAULT_VALUE_SERIALIZER);
      addEntry(props, ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, DEFAULT_KEY_DESERIALIZER);
      addEntry(props, ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DEFAULT_VALUE_DESERIALIZER);
    }
    catch (IllegalArgumentException e) {
      throw ExceptionHelper.wrapCoreException(e);
    }
    return props;
  }


  public String getBootstrapServers() {
    return bootstrapServers;
  }


  /**
   * Set the {@code bootstrap.servers} property.
   * <p>
   * A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. The client will make use of all
   * servers irrespective of which servers are specified here for bootstrapping; this list only impacts the initial hosts used to
   * discover the full set of servers. This list should be in the form {@code host1:port1,host2:port2,....}. Since these servers are
   * just used for the initial connection to discover the full cluster membership (which may change dynamically), this list need not
   * contain the full set of servers (you may want more than one, though, in case a server is down).
   * </p>
   * 
   * @param s the bootstrap servers
   */
  public void setBootstrapServers(String s) {
    this.bootstrapServers = Args.notBlank(s, "bootstrap-servers");
  }

  public Long getBufferMemory() {
    return bufferMemory;
  }

  /**
   * Set the {@code buffer.memory} property.
   * <p>
   * The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records are sent faster
   * than they can be delivered to the server the producer will either block or throw an exception based on the preference specified
   * by block.on.buffer.full ({code block.on.buffer.full} defaults to false, so an exception will be thrown).
   * </p>
   * <p>
   * This setting should correspond roughly to the total memory the producer will use, but is not a hard bound since not all memory
   * the producer uses is used for buffering. Some additional memory will be used for compression (if compression is enabled) as
   * well as for maintaining in-flight requests.
   * </p>
   * 
   * @param m the buffer memory
   */
  public void setBufferMemory(Long m) {
    this.bufferMemory = m;
  }


  public CompressionType getCompressionType() {
    return compressionType;
  }

  /**
   * Set the {@code compression.type} property.
   * <p>
   * The compression type for all data generated by the producer. The default is none (i.e. no compression). Valid values are none,
   * gzip, snappy, or lz4. Compression is of full batches of data, so the efficacy of batching will also impact the compression
   * ratio (more batching means better compression).
   * </p>
   * 
   * @param t the compression type
   */
  public void setCompressionType(CompressionType t) {
    this.compressionType = t;
  }

  public Integer getRetries() {
    return retries;
  }


  /**
   * Set the {@code retries} property.
   * <p>
   * Setting a value greater than zero will cause the client to resend any record whose send fails with a potentially transient
   * error. Note that this retry is no different than if the client resent the record upon receiving the error. Allowing retries
   * will potentially change the ordering of records because if two records are sent to a single partition, and the first fails and
   * is retried but the second succeeds, then the second record may appear first.
   * </p>
   * 
   * @param i the number of retries, default is 0 if not specified.
   */
  public void setRetries(Integer i) {
    this.retries = i;
  }

  public Acks getAcks() {
    return acks;
  }

  /**
   * Set the {@code acks} property.
   * 
   * <p>
   * This specifies number of acknowledgments the producer requires the leader to have received before considering a request
   * complete
   * </p>
   * 
   * @param a the number of acks; default is {@link Acks#all} if not specified for the strongest available guarantee.
   */
  public void setAcks(Acks a) {
    this.acks = a;
  }

  /**
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Set the {@code group.id} property.
   * <p>
   * A unique string that identifies the consumer group this consumer belongs to. This property is required if the consumer uses
   * either the group management functionality by using subscribe(topic) or the Kafka-based offset management strategy.
   * </p>
   * 
   * @param groupId the groupId to set
   */
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

}