package com.whataboutmv.modules.event;

import com.whataboutmv.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "Enrollment.withEventAndMovie",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "movie")
        },
        subgraphs = @NamedSubgraph(name = "movie", attributeNodes = @NamedAttributeNode("movie"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;


}
