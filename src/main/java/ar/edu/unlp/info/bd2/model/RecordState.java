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
    private String status; /*Nombre del estado*/
    @ManyToOne
    private Order order;

    public RecordState() {
    }

    public RecordState(String state, Date date) {
        this.date = date;
        this.status = status;
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

    public String getStatus() { return status; }

    public void setStatus(String state) {
        this.status = state;
    }

    public Order getOrder() {
        return order;
    }
}
