package study.querdsl.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id @GeneratedValue
    private Long id;
    private String name;

    // 거울?
    @OneToMany(mappedBy = "team") // mappedBy 로서 연관관계 주인을 명시
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

}
