package dropbox;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties ("dropbox")
public class DropboxProperties {

    private String clientIdentifier;
    private String accessToken;

}
