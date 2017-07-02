package dropbox;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest(classes = DropboxClientApplication.class)
@RunWith(SpringRunner.class)
public class DropboxClientApplicationTest {

    @Autowired
    private DbxClientV2 client;

    @Test(expected = GetMetadataErrorException.class)
    public void should_throw_an_exception_when_file_does_not_exist() throws Throwable {
        client.files().getMetadata("/" + System.currentTimeMillis());
    }

    @Test
    public void should_list_all_files() throws Throwable {

        String min = "/a-folder" + UUID.randomUUID().toString();
        client.files().createFolder(min);

        String path = "/";
        if (path.equals("/")) path = "";

        ListFolderResult result = client.files().listFolder(path);
        List<Metadata> children = new ArrayList<>();
        do {
            children.addAll(result.getEntries());
            if (result.getHasMore()) {
                result = client.files().listFolderContinue(result.getCursor());
            } else {
                result = null;
            }
            log.info("no more files to load. result = null");
        }
        while (result != null);
        children.forEach(c -> log.info(c.toStringMultiline()));
        BDDAssertions.then(children.size()).isGreaterThan(0);
    }

    @Test
    public void should_create_directory() throws Throwable {
        String path = "/" + System.currentTimeMillis() + "-a";
        Metadata folderMetadata;
        try {
            folderMetadata = client.files().createFolder(path);
        } catch (CreateFolderErrorException e) {
            log.info("could not create the directory. " +
                    "it probably already exists.", e);
            folderMetadata = client.files().getMetadata(path);
        }
        BDDAssertions.then(folderMetadata).isNotNull();
    }

    @Test
    public void should_share_directory() throws Throwable {

        ClassPathResource img = new ClassPathResource("/logo.png");

        // given
        String path = "/a-folder-" + UUID.randomUUID().toString();
        client.files().createFolder(path);
        UploadUploader upload = client.files().upload(path + "/img.png");
        upload.uploadAndFinish(img.getInputStream());

        SharedLinkMetadata sharedLinkWithSettings = client.sharing().createSharedLinkWithSettings(path );
        log.info(sharedLinkWithSettings.getUrl());
    }
}
