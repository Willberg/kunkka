package fun.johntaylor.kunkka.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
public class MysqlConfiguration extends AbstractR2dbcConfiguration {
    private volatile static ConnectionPool pool;

    @Override
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, "127.0.0.1")
                .option(USER, "root")
                .option(PORT, 3306)  // optional, default 3306
                .option(PASSWORD, "123456") // optional, default null, null means has no password
                .option(DATABASE, "kunkka") // optional, default null, null means not specifying the database
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
//                .option(SSL, true) // optional, default is enabled, it will be ignore if "sslMode" is set
//                .option(Option.valueOf("sslMode"), "verify_identity") // optional, default "preferred"
//                .option(Option.valueOf("sslCa"), "/path/to/mysql/ca.pem") // required when sslMode is verify_ca or verify_identity, default null, null means has no server CA cert
//                .option(Option.valueOf("sslKey"), "/path/to/mysql/client-key.pem") // optional, default null, null means has no client key
//                .option(Option.valueOf("sslCert"), "/path/to/mysql/client-cert.pem") // optional, default null, null means has no client cert
//                .option(Option.valueOf("sslKeyPassword"), "key-pem-password-in-here") // optional, default null, null means has no password for client key (i.e. "sslKey")
//                .option(Option.valueOf("tlsVersion"), "TLSv1.1,TLSv1.2,TLSv1.3") // optional, default is auto-selected by the server
//                .option(Option.valueOf("zeroDate"), "use_null") // optional, default "use_null"
                .option(Option.valueOf("useServerPrepareStatement"), true) // optional, default false
                .build();
        return ConnectionFactories.get(options);
    }


    @Bean
    public Mono<Connection> getConn() {
        if (Objects.isNull(pool)) {
            synchronized (MysqlConfiguration.class) {
                if (Objects.isNull(pool)) {
                    ConnectionFactory connectionFactory = this.connectionFactory();
                    // 使用连接池
                    ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                            .maxIdleTime(Duration.ofMillis(1000))
                            .maxSize(20)
                            .build();
                    pool = new ConnectionPool(configuration);
                }
            }
        }


        // Creating a Mono using Project Reactor
        return pool.create();
    }
}
