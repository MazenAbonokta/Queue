package com.dlj4.tech.queue.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigScreen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mainScreenName;
    private String mainScreenFileExtension;
    private String mainScreenOriginalName;
    private String logoName;
    private String logoFileExtension;
    private String logoOriginalName;
    private  String mainScreenMessage;
    private  String ticketScreenMessage;



}
