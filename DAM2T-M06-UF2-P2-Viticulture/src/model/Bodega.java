package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name= "bodega")
public class Bodega {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = true)
    private int id_bodega;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "provincia") 
    private String provincia;    
    
    @OneToMany
    @JoinColumn(name = "bodega_id")
    private List<Vid> vids;
    
    public Bodega() {}
    
    public Bodega(String nombre, String provincia) { 
        this.nombre = nombre;
        this.provincia = provincia; 
        this.vids = new ArrayList<>();
    }

    // Getters and setters
    public int getId_bodega() {
        return id_bodega;
    }

    public void setId_bodega(int id_bodega) {
        this.id_bodega = id_bodega;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProvincia() { 
        return provincia;
    }

    public void setProvincia(String provincia) { 
        this.provincia = provincia;
    }

    public List<Vid> getVids() {
        return vids;
    }

    public void setVids(List<Vid> vids) {
        this.vids = vids;
    }

    @Override
    public String toString() {
        return "Bodega [id_bodega=" + id_bodega + ", nombre=" + nombre + ", provincia=" + provincia + ", vids=" + Arrays.toString(vids.toArray()) + "]";
    }
}

