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
import java.util.Optional;

@Service
public class ConfigServiceImp implements ConfigService {

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    ObjectsDataMapper  objectsDataMapper;
    String uploadDir = "uploads/";
    @Override
    public ConfigResponse createConfig(ConfigRequest configRequest) {
        try {


            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
            String base64File = configRequest.getImg();
            byte[] decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
            String Ext=configRequest.getFileExt().split("/")[1];
            String hashedFilename=generateHash( configRequest.getName());
            String filePath=uploadDir + hashedFilename+"."+Ext;
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/" + hashedFilename+"."+Ext)
                    .toUriString();
            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }

            ConfigScreen configScreen=objectsDataMapper.configScreenRequestToConfigScreen(configRequest,fileUrl,hashedFilename);
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

            Optional<ConfigScreen> configScreenObj = configRepository.findById(Long.getLong(configRequest.getId()));

            if(configScreenObj.isEmpty()){
                return createConfig(configRequest);
            }

            // Save each file from Base64
            Files.createDirectories(Paths.get(uploadDir));
            String base64File = configRequest.getImg();
            byte[] decodedBytes = Base64.getDecoder().decode(base64File.split(",")[1]); // Removes data prefix
            String Ext=configRequest.getFileExt().split(".")[1];
            String hashedFilename=generateHash( configRequest.getName());
            String filePath=uploadDir + hashedFilename+"."+Ext;
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/" + hashedFilename+"."+Ext)
                    .toUriString();
            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }

            ConfigScreen configScreen= configScreenObj.get();
            configScreen.setName(hashedFilename);
            configScreen.setContent(configRequest.getEditor());
            configScreen.setPath(fileUrl);
            configScreen.setOriginalName(configRequest.getName());
            configScreen.setOriginalName(configRequest.getName());
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
    public ConfigResponse getConfigByType(String configType) {
     Optional<ConfigScreen> configScreen =  configRepository.findByConfigType(configType);
     if(configScreen.isEmpty())
     {
         return null;

     }
     ConfigResponse configResponse = objectsDataMapper.configScreenToConfigScreenResponse(configScreen.get());
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
