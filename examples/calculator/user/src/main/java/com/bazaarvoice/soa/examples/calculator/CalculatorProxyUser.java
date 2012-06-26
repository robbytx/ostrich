package com.bazaarvoice.soa.examples.calculator;

import com.bazaarvoice.soa.pool.ServicePoolBuilder;
import com.bazaarvoice.soa.retry.RetryNTimes;
import com.bazaarvoice.soa.zookeeper.ZooKeeperConfiguration;
import com.bazaarvoice.soa.zookeeper.ZooKeeperConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Variation of {@link CalculatorUser} that uses a dynamic service proxy instead of making direct calls to
 * {@link com.bazaarvoice.soa.ServicePool}.
 */
public class CalculatorProxyUser {
    private static final Logger LOG = LoggerFactory.getLogger(CalculatorProxyUser.class);

    private final Random _random = new Random();
    private final CalculatorService _service;

    public CalculatorProxyUser(CalculatorService service) {
        _service = service;
    }

    public void use() throws InterruptedException {
        int i = 0;
        while (++i > 0) {
            try {
                final int a = _random.nextInt(10);
                final int b = 1 + _random.nextInt(9);
                final int op = _random.nextInt(4);
                int result = call(op, a, b);
                LOG.info("i:{}, result:{}", i, result);
            } catch (Exception e) {
                LOG.info("i:{}, {}", i, e.getClass().getCanonicalName());
            }

            Thread.sleep(500);
        }
    }

    private int call(int op, int a, int b) {
        switch (op) {
            case 0:  return _service.add(a, b);
            case 1:  return _service.sub(a, b);
            case 2:  return _service.mul(a, b);
            default: return _service.div(a, b);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        String connectString = (args.length > 0) ? args[0] : "localhost:2181";

        ZooKeeperConnection connection = new ZooKeeperConfiguration()
                .setConnectString(connectString)
                .setRetryNTimes(new com.bazaarvoice.soa.zookeeper.RetryNTimes(3, 100))
                .connect();

        CalculatorService service = ServicePoolBuilder.create(CalculatorService.class)
                .withZooKeeperHostDiscovery(connection)
                .withServiceFactory(new CalculatorServiceFactory())
                .buildProxy(new RetryNTimes(3, 100, TimeUnit.MILLISECONDS));

        CalculatorProxyUser user = new CalculatorProxyUser(service);
        user.use();
        ((Closeable) service).close();
    }
}
