package pl.luxan.luxofttaskv4.model;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@Entity
@Table(name = "client_data")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientData {

    @Id
    @NotNull
    @Column(name = "PRIMARY_KEY", unique = true)
    private String primaryKey;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "UPDATED_TIMESTAMP")
    private Timestamp updatedTimestamp;

}
