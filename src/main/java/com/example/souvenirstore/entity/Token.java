package com.example.souvenirstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "tokens")
public class Token {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private UUID uuid;

    @JsonIgnore
    @OneToOne
    private User user;

    @JsonIgnore
    @Column(name = "creation_date_time")
    private LocalDateTime createdDateTime;

    @JsonIgnore
    @Column(name = "expiration_date_time")
    private LocalDateTime expireDateTime;
}
