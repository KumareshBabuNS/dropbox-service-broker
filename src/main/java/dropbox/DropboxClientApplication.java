package dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableConfigurationProperties(DropboxProperties.class)
@SpringBootApplication
public class DropboxClientApplication {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(DropboxClientApplication.class, args);
    }
}

@Configuration
class DropboxClientConfiguration {

    @Bean
    DropboxProperties dropboxProperties() {
        return new DropboxProperties();
    }

    @Bean
    DbxClientV2 dropbox(DropboxProperties dropboxProperties) {
        DbxRequestConfig config = new DbxRequestConfig(dropboxProperties.getClientIdentifier());
        return new DbxClientV2(config, dropboxProperties.getAccessToken());
    }
}