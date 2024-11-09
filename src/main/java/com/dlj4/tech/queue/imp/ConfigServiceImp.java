package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.ConfigRequest;
import com.dlj4.tech.queue.dao.response.ConfigResponse;
import com.dlj4.tech.queue.entity.ConfigScreen;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.ConfigRepository;
import com.dlj4.tech.queue.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigServiceImp implements ConfigService {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    ObjectsDataMapper  objectsDataMapper;
    String uploadDir = "src/main/resources/static/uploads/";
    @Override
    public ConfigResponse createConfig(ConfigRequest configRequest) {
        try {


            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
            String base64File = configRequest.getLogoImg();
            byte[] decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
            String Ext=configRequest.getLogoFileExtension().split("/")[1];
            String hashedFilename=generateHash( configRequest.getLogoOriginalName())+"."+Ext;
            String filePath=uploadDir + hashedFilename;

            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }
            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
             base64File = configRequest.getMainScreenImg();
            decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
             Ext=configRequest.getMainScreenFileExtension().split("/")[1];
            String hashedMainFilename=generateHash( configRequest.getMainScreenOriginalName())+"."+Ext;
            String mainfilePath=uploadDir + hashedMainFilename;

             file = new File(mainfilePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }

            ConfigScreen configScreen=objectsDataMapper.configScreenRequestToConfigScreen(configRequest,hashedFilename,hashedMainFilename);
           // ConfigScreen configScreen=objectsDataMapper.configScreenRequestToConfigScreen(configRequest,file.getAbsolutePath(),hashedFilename);

           configScreen= configRepository.save(configScreen);

           ConfigResponse configResponse=objectsDataMapper.configScreenToConfigScreenResponse((configScreen));
           return  configResponse;
        } catch (IOException e) {
           throw new RuntimeException("Error");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ConfigResponse updateConfig(ConfigRequest configRequest) {
        try {

            Optional<ConfigScreen> configScreenObj = configRepository.findById(configRequest.getId());

            if(configScreenObj.isEmpty()){
                return createConfig(configRequest);
            }

            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
            String base64File = configRequest.getLogoImg();
            byte[] decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
            String Ext=configRequest.getLogoFileExtension().split("/")[1];
            String hashedFilename=generateHash( configRequest.getLogoOriginalName())+"."+Ext;
            String filePath=uploadDir + hashedFilename;

            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }
            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
            base64File = configRequest.getMainScreenImg();
            decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
            Ext=configRequest.getMainScreenFileExtension().split("/")[1];
            String hashedMainFilename=generateHash( configRequest.getMainScreenOriginalName())+"."+Ext;
            String mainfilePath=uploadDir + hashedMainFilename;

            file = new File(mainfilePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }


            ConfigScreen configScreen= configScreenObj.get();
            configScreen.setLogoName(hashedFilename);
            configScreen.setMainScreenMessage(configRequest.getMainScreenMessage());

            configScreen.setLogoOriginalName(configRequest.getLogoOriginalName());
            configScreen.setMainScreenName(hashedMainFilename);

            configScreen.setTicketScreenMessage(configRequest.getTicketScreenMessage());
            configScreen.setMainScreenOriginalName(configRequest.getMainScreenOriginalName());
            configRepository.save(configScreen);

            ConfigResponse configResponse=objectsDataMapper.configScreenToConfigScreenResponse((configScreen));
            return  configResponse;
        } catch (IOException e) {
            throw new RuntimeException("Error");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigResponse getConfig() {
     List<ConfigScreen> configScreen =  configRepository.findAll();
     if(configScreen.isEmpty())
     {
         return null;

     }
     ConfigResponse configResponse = objectsDataMapper.configScreenToConfigScreenResponse(configScreen.get(0));
     return  configResponse;
    }

    // Method to generate SHA-256 hash of a string
    private String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
