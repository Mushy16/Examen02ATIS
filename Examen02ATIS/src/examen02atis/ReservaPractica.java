/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

import java.time.LocalDate;
import java.time.LocalTime;


public class ReservaPractica extends Reserva {

    private String nombrePractica;
    private String equipoRequerido;

    public ReservaPractica(Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String nombrePractica, String equipoRequerido) {
        super(aula, responsable, fecha, horaInicio, horaFin);
        this.nombrePractica = nombrePractica;
        this.equipoRequerido = equipoRequerido;
    }

    // Constructor para carga CSV
     public ReservaPractica(int id, Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado, String nombrePractica, String equipoRequerido) {
        super(id, aula, responsable, fecha, horaInicio, horaFin, estado);
        this.nombrePractica = nombrePractica;
        this.equipoRequerido = equipoRequerido;
    }

    // Getters y Setters
    public String getNombrePractica() { return nombrePractica; }
    public String getEquipoRequerido() { return equipoRequerido; }
    public void setNombrePractica(String nombrePractica) { this.nombrePractica = nombrePractica; }
    public void setEquipoRequerido(String equipoRequerido) { this.equipoRequerido = equipoRequerido; }

 
    @Override
    public void validarReglasEspecificas() throws ValidacionException {
        if (aula.getTipo() != TipoAula.LABORATORIO) {
            throw new ValidacionException("Regla de negocio: Las prácticas solo pueden reservarse en aulas de tipo LABORATORIO.");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" | Práctica: %s | Equipo: %s", nombrePractica, equipoRequerido);
    }
    
    @Override
    public String toCsvString() {
        return String.join(",",
                String.valueOf(id), "PRACTICA", aula.getCodigo(), responsable,
                fecha.toString(), horaInicio.toString(), horaFin.toString(),
                estado.toString(), nombrePractica, equipoRequerido
        );
    }
}
