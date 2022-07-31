package heading.ground.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStore {

//    @Value("${file.dir}")
//    private String fileDir;
    private final String fileDir = "imageUpload";

    public String getFullPath(String fileName){
        return fileDir + fileName;
    }

    public ImageFile storeFile(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        Path uploadPath = Paths.get(fileDir);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(storeFileName);
            Files.copy(inputStream,filePath, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            throw new IOException("Cannot Save file : " + storeFileName,e);
        }


       // multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new ImageFile(originalFilename,storeFileName);
    }

    public void deleteImage(String imageName){
        File file = new File(fileDir +"/"+ imageName);
        file.delete();
    }


    private String createStoreFileName(String originalName){
        String s = UUID.randomUUID().toString();
        String ext = extractEXT(originalName);

        return s + "." + ext;
    }

    private String extractEXT(String originalName) {
        int pos = originalName.lastIndexOf(".");
        return originalName.substring(pos+1);
    }

}
