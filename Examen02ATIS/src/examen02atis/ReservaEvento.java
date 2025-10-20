/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaEvento extends Reserva {

    private TipoEvento tipoEvento;
    private int publicoEstimado;

    public ReservaEvento(Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, TipoEvento tipoEvento, int publicoEstimado) {
        super(aula, responsable, fecha, horaInicio, horaFin);
        this.tipoEvento = tipoEvento;
        this.publicoEstimado = publicoEstimado;
    }
    
    // Constructor para carga CSV
    public ReservaEvento(int id, Aula aula, String responsable, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado, TipoEvento tipoEvento, int publicoEstimado) {
        super(id, aula, responsable, fecha, horaInicio, horaFin, estado);
        this.tipoEvento = tipoEvento;
        this.publicoEstimado = publicoEstimado;
    }

    // Getters y Setters
    public TipoEvento getTipoEvento() { return tipoEvento; }
    public int getPublicoEstimado() { return publicoEstimado; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }
    public void setPublicoEstimado(int publicoEstimado) { this.publicoEstimado = publicoEstimado; }

    @Override
    public void validarReglasEspecificas() throws ValidacionException {
        if (this.publicoEstimado > this.aula.getCapacidad()) {
            throw new ValidacionException(
                    String.format("Regla de negocio: El público estimado (%d) excede la capacidad del aula (%d).",
                            publicoEstimado, aula.getCapacidad())
            );
        }

        if (this.tipoEvento == TipoEvento.CONFERENCIA && this.aula.getTipo() != TipoAula.AUDITORIO) {
            throw new ValidacionException("Regla de negocio: Las CONFERENCIAS solo pueden realizarse en un AUDITORIO.");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" | Evento: %s | Público: %d", tipoEvento, publicoEstimado);
    }
    
    @Override
    public String toCsvString() {
        return String.join(",",
                String.valueOf(id), "EVENTO", aula.getCodigo(), responsable,
                fecha.toString(), horaInicio.toString(), horaFin.toString(),
                estado.toString(), tipoEvento.toString(), String.valueOf(publicoEstimado)
        );
    }
}
