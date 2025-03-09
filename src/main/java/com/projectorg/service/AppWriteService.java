package com.projectorg.service;

import org.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AppWriteService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String endpoint = "https://cloud.appwrite.io/v1";
    private final String projectId = "67a2acc10011128dd9da";
    private final String apiKey = "standard_4f64a7721c48e7bc3a05b0f7b6a0b5b5436892dfbe9a1a3d87a86cc9a43160b76176f04e331f54635c5944019f46bbdd1af71614a1ba6c467c6dc562060e61609f62a520b577af75a4915924eb4f37364edb6618de829b0dae8252be6f44a540a70cd4c38d5e2c6d719df30047ff2afa7447496ac0d81ddff99e47c29f457720";
    private final String bucketIdFiles = "67a2acf4003a5a1393c5";
    private final String bucketIdDiagrams = "67a3f784000b35db4a25";

    public String uploadFile(byte[] fileData, String fileName) {
        String url = endpoint + "/storage/buckets/" + bucketIdFiles + "/files";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Appwrite-Project", projectId);
        headers.set("X-Appwrite-Key", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("fileId", "unique()");
        body.add("file", new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return fileName; // Nombre del archivo
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                String fileId = jsonResponse.getString("$id");
                return endpoint + "/storage/buckets/" + bucketIdFiles + "/files/" + fileId + "/view?project=" + projectId;
            } else {
                throw new RuntimeException("Error al subir archivo. Código HTTP: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error HTTP al subir archivo: " + e.getMessage());
            throw new RuntimeException("Error HTTP al subir archivo: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            System.err.println("Error de conexión al subir archivo: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor al subir archivo.", e);
        }
    }

    public void deleteFile(String fileUrl) {
        int index = fileUrl.lastIndexOf("/view");
        String url = (index != -1) ? fileUrl.substring(0, index) : fileUrl;
        System.out.println(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Appwrite-Project", projectId);
        headers.set("X-Appwrite-Key", apiKey);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Archivo eliminado exitosamente.");
            } else {
                throw new RuntimeException("Error al eliminar archivo. Código HTTP: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error HTTP al eliminar archivo: " + e.getMessage());
            throw new RuntimeException("Error HTTP al eliminar archivo: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            System.err.println("Error de conexión al eliminar archivo: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor al eliminar archivo.", e);
        }
    }

    public String uploadDiagram(byte[] fileData, String fileName) {
        String url = endpoint + "/storage/buckets/" + bucketIdDiagrams + "/files";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Appwrite-Project", projectId);
        headers.set("X-Appwrite-Key", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("fileId", "unique()");
        body.add("file", new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                String fileId = jsonResponse.getString("$id");
                return endpoint + "/storage/buckets/" + bucketIdDiagrams + "/files/" + fileId + "/view?project=" + projectId;
            } else {
                throw new RuntimeException("Error al subir diagrama. Código HTTP: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error HTTP al subir diagrama: " + e.getMessage());
            throw new RuntimeException("Error HTTP al subir diagrama: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            System.err.println("Error de conexión al subir diagrama: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor al subir el diagrama.", e);
        }
    }

}
