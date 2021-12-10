package com.sprect.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pages")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idPage;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idUser;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<GameObject> gameObjects;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pages_games",
            joinColumns = @JoinColumn(name = "idPage"),
            inverseJoinColumns = @JoinColumn(name = "idGame"))
    private List<Game> games;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Comment> comments;

    @CreationTimestamp
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createdAt;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "boolean default false")
    @JsonIgnore
    private boolean approved;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double rate;

    @JsonIgnore
    private int countComment;
}
