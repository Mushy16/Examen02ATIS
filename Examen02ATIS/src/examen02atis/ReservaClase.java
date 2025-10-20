/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;


import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaClase extends Reserva {

    private String carrera;
    private String codigoAsignatura;

    public ReservaClase(Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String carrera, String codigoAsignatura) {
        super(aula, responsable, fecha, horaInicio, horaFin);
        this.carrera = carrera;
        this.codigoAsignatura = codigoAsignatura;
    }
    
    // Constructor para carga CSV
    public ReservaClase(int id, Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado, String carrera, String codigoAsignatura) {
        super(id, aula, responsable, fecha, horaInicio, horaFin, estado);
        this.carrera = carrera;
        this.codigoAsignatura = codigoAsignatura;
    }

    // Getters y Setters
    public String getCarrera() { return carrera; }
    public String getCodigoAsignatura() { return codigoAsignatura; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setCodigoAsignatura(String codigoAsignatura) { this.codigoAsignatura = codigoAsignatura; }

    @Override
    public void validarReglasEspecificas() throws ValidacionException {
        if (aula.getTipo() != TipoAula.TEORICA && aula.getTipo() != TipoAula.LABORATORIO) {
            throw new ValidacionException("Regla de negocio: Las clases solo pueden reservarse en aulas de tipo TEORICA o LABORATORIO.");
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Carrera: %s | Asignatura: %s", carrera, codigoAsignatura);
    }

    @Override
    public String toCsvString() {
        // Formato: id,TIPO,aula_codigo,responsable,fecha,inicio,fin,estado,extra1,extra2
        return String.join(",",
                String.valueOf(id), "CLASE", aula.getCodigo(), responsable,
                fecha.toString(), horaInicio.toString(), horaFin.toString(),
                estado.toString(), carrera, codigoAsignatura
        );
    }
}
