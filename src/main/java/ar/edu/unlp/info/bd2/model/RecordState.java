package ar.edu.unlp.info.bd2.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RECORD_STATE")
public class RecordState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date; /*fecha en que se actualizo el estado*/
    private String state; /*Nombre del estado*/
    @ManyToOne
    private Order order;

    public RecordState() {
    }

    public RecordState(String state, Date date) {
        this.date = date;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getState() { return state; }

    public void setState(String state) {
        this.state = state;
    }

    public Order getOrder() {
        return order;
    }
}
