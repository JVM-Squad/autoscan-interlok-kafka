package com.adaptris.kafka;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.Test;

import com.adaptris.core.CoreException;
import com.adaptris.kafka.ProducerConfigBuilder.CompressionType;
import com.adaptris.security.exc.PasswordException;
import com.adaptris.security.password.Password;
import com.adaptris.util.KeyValuePair;
import com.adaptris.util.KeyValuePairSet;

public class AdvancedProducerConfigBuilderTest {

  @Test
  public void testConfig() {
    AdvancedProducerConfigBuilder builder = new AdvancedProducerConfigBuilder();
    assertNotNull(builder.getConfig());
    KeyValuePairSet myConfig = new KeyValuePairSet();
    myConfig.add(new KeyValuePair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:4242"));
    builder.setConfig(myConfig);
    assertEquals(myConfig, builder.getConfig());
    try {
      builder.setConfig(null);
      fail();
    } catch (IllegalArgumentException expected) {

    }
    assertEquals(myConfig, builder.getConfig());

  }

  @Test
  public void testBuild() throws Exception {
    KeyValuePairSet myConfig = new KeyValuePairSet();
    myConfig.add(new KeyValuePair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:4242"));
    myConfig.add(new KeyValuePair(ProducerConfig.LINGER_MS_CONFIG, "10"));
    myConfig.add(new KeyValuePair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:4242"));
    myConfig.add(new KeyValuePair(ProducerConfig.COMPRESSION_TYPE_CONFIG, CompressionType.lz4.name()));
    myConfig.add(new KeyValuePair(SslConfigs.SSL_KEY_PASSWORD_CONFIG, Password.encode("MyPassword", Password.PORTABLE_PASSWORD)));
    AdvancedProducerConfigBuilder builder = new AdvancedProducerConfigBuilder(myConfig);

    Properties p = builder.build();
    assertEquals("localhost:4242", p.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    assertEquals(CompressionType.lz4.name(), p.getProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG));
    assertNull(p.getProperty(ProducerConfig.ACKS_CONFIG));
    assertEquals("10", p.getProperty(ProducerConfig.LINGER_MS_CONFIG));
    assertEquals("MyPassword", p.getProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG));
  }

  @Test
  public void testBuild_WithDuffPassword() throws Exception {
    KeyValuePairSet myConfig = new KeyValuePairSet();
    myConfig.add(new KeyValuePair(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:4242"));
    myConfig.add(new KeyValuePair(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "PW:ABCDEWF"));

    AdvancedProducerConfigBuilder builder = new AdvancedProducerConfigBuilder(myConfig);
    try {
      Properties p = builder.build();
      fail();
    } catch (CoreException e) {
      assertNotNull(e.getCause());
      assertEquals(PasswordException.class, e.getCause().getClass());
    }
  }
}
