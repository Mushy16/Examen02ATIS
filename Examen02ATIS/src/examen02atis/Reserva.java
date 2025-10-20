/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Reserva implements Validable {

    private static int nextId = 1; // Generador simple de IDs
    protected int id;
    protected Aula aula;
    protected String responsable;
    protected LocalDate fecha;
    protected LocalTime horaInicio;
    protected LocalTime horaFin;
    protected EstadoReserva estado;

    public Reserva(Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.id = nextId++;
        this.aula = aula;
        this.responsable = responsable;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = EstadoReserva.ACTIVA; // Estado por defecto [cite: 22]
    }
    
    // Constructor para carga desde CSV
    public Reserva(int id, Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado) {
        this(aula, responsable, fecha, horaInicio, horaFin);
        this.id = id;
        this.estado = estado;
        if (id >= nextId) {
            nextId = id + 1; // Asegura que el proximo ID sea único
        }
    }

    // Getters y Setters
    public int getId() { return id; }
    public Aula getAula() { return aula; }
    public String getResponsable() { return responsable; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public EstadoReserva getEstado() { return estado; }

    public void setAula(Aula aula) { this.aula = aula; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }



    public double getDuracionHoras() {
        return Duration.between(horaInicio, horaFin).toMinutes() / 60.0;
    }


    public void validarReglasComunes() throws ValidacionException {
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new ValidacionException("Error: La hora de fin debe ser posterior a la hora de inicio.");
        }
        // Validacion de fechas [cite: 18]
        if (fecha.isBefore(LocalDate.now())) {
            throw new ValidacionException("Error: No se pueden registrar reservas para fechas pasadas.");
        }
    }
    
    public boolean seSolapaCon(Reserva otra) {
        // No hay conflicto si son aulas diferentes o días diferentes
        if (!this.aula.equals(otra.getAula())) return false;
        if (!this.fecha.equals(otra.getFecha())) return false;
        
        // Ignora si la otra reserva está cancelada
        if (otra.getEstado() == EstadoReserva.CANCELADA) return false;

        // Lógica de solapamiento de tiempo: (InicioA < FinB) y (FinA > InicioB)
        return this.horaInicio.isBefore(otra.getHoraFin()) &&
               this.horaFin.isAfter(otra.getHoraInicio());
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Aula: %s (%s) | Fecha: %s | Horario: %s-%s | Resp: %s | Estado: %s",
                id, this.getClass().getSimpleName(), aula.getCodigo(), aula.getNombre(),
                fecha, horaInicio, horaFin, responsable, estado);
    }
    
    @Override
    public abstract void validarReglasEspecificas() throws ValidacionException;
    
    public abstract String toCsvString();
}
