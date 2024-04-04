package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name= "campo")
public class Campo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = true)
    private int id_campo;

    @OneToMany
    @JoinColumn(name = "campo_id")
    private List<Vid> vids;
    
    @OneToOne
    @JoinColumn(name = "id_bodega")
    private Bodega bodega;
    
    public Campo() {}

    public Campo(Bodega b) {
        this.bodega = b;
        this.vids = new ArrayList<>();
    }

    // Getters and setters
    public int getId_campo() {
        return id_campo;
    }

    public void setId_campo(int id_campo) {
        this.id_campo = id_campo;
    }

    public List<Vid> getVidss() {
        return vids;
    }

    public void setVids(List<Vid> vids) {
        this.vids = vids;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public void addVid(Vid v) {
        this.vids.add(v);        
    }

    public ArrayList<Vid> getVids() {
        ArrayList<Vid> vids = new ArrayList<>();
        vids.addAll(this.vids);
        return vids;
    }

    @Override
    public String toString() {
        return "Campo [id_campo=" + id_campo + ", vids=" + Arrays.toString(vids.toArray()) + ", bodega="
                + bodega.toString() + "]";
    }

}

