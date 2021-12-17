package com.teenteen.topping.challenge.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "key_word")
public class KeyWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordId;
    private String word;
    private boolean deleted;

    @ManyToMany
    @JoinTable(name = "word_challenge",
    joinColumns = @JoinColumn(name = "word_id"),
    inverseJoinColumns = @JoinColumn(name = "challenge_id"))
    private List<Challenge> challenges;
}
