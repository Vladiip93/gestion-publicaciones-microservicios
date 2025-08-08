package com.espe.mspr.publicacionesservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name= "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    private String afiliacion;

    private String orcid;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    private String fotoUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "author_roles",
            joinColumns = @JoinColumn(name = "author_id")
    )
    @Column(name = "role")
    private Set<String> roles;
}
