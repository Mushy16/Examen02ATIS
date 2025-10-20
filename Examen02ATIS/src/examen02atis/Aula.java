/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

import java.util.Objects;

public class Aula {
    // Encapsulamiento 
    private String codigo;
    private String nombre;
    private int capacidad;
    private TipoAula tipo;

    public Aula(String codigo, String nombre, int capacidad, TipoAula tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    // Getters y Setters para encapsulamiento
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public TipoAula getTipo() { return tipo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public void setTipo(TipoAula tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return String.format("Aula [Código: %s, Nombre: %s, Capacidad: %d, Tipo: %s]",
                codigo, nombre, capacidad, tipo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aula aula = (Aula) o;
        return Objects.equals(codigo, aula.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
