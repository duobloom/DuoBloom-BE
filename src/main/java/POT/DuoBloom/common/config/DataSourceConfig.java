package POT.DuoBloom.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${db.thread-count}")
    private int threadCount; // 전체 Thread 수

    @Value("${db.connections-per-task}")
    private int connectionsPerTask; // Task당 필요한 Connection 수

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(driverClassName);

        // HikariCP Pool Size 계산
        int poolSize = threadCount * (connectionsPerTask - 1) + 1;
        if (poolSize <= 0) {
            poolSize = 10; // 기본값 최소 10
        }
        dataSource.setMaximumPoolSize(poolSize);

        // HikariCP 추가 설정
        dataSource.setMinimumIdle(5); // 최소 유휴 커넥션
        dataSource.setIdleTimeout(30000); // 유휴 커넥션 최대 대기 시간
        dataSource.setConnectionTimeout(30000); // 커넥션 획득 대기 시간
        dataSource.setMaxLifetime(1800000); // 커넥션 최대 수명

        return dataSource;
    }
}
