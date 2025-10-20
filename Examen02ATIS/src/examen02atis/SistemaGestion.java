/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class SistemaGestion {

    private List<Aula> aulas;
    private List<Reserva> reservas;

    // Constantes para persistencia 
    private static final String ARCHIVO_AULAS = "aulas.csv";
    private static final String ARCHIVO_RESERVAS = "reservas.csv";

    public SistemaGestion() {
        // Uso de colecciones ArrayList 
        this.aulas = new ArrayList<>();
        this.reservas = new ArrayList<>();
        cargarAulas();
        cargarReservas();
    }

    // --- Gestión de Aulas ---

    public void registrarAula(Aula aula) throws ValidacionException {
        // Validar que el código no exista
        if (getAulaPorCodigo(aula.getCodigo()).isPresent()) {
            throw new ValidacionException("Error: Ya existe un aula con el código " + aula.getCodigo());
        }
        this.aulas.add(aula);
        System.out.println("Aula registrada exitosamente.");
    }

    public List<Aula> getAulas() {
        return new ArrayList<>(aulas); // Devuelve una copia para proteger la lista original
    }

    public Optional<Aula> getAulaPorCodigo(String codigo) {
        // Uso de Streams para búsqueda 
        return this.aulas.stream()
                .filter(a -> a.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    public void modificarAula(String codigo, String nuevoNombre, int nuevaCapacidad, TipoAula nuevoTipo) throws ValidacionException {
        Aula aula = getAulaPorCodigo(codigo)
                .orElseThrow(() -> new ValidacionException("No se encontró el aula con código " + codigo));

        aula.setNombre(nuevoNombre);
        aula.setCapacidad(nuevaCapacidad);
        aula.setTipo(nuevoTipo);
        System.out.println("Aula modificada exitosamente.");
    }

    // --- Gestión de Reservas ---

    public void registrarReserva(Reserva nuevaReserva) throws ValidacionException {
        // 1. Validar reglas comunes (ej. hora fin > hora inicio)
        nuevaReserva.validarReglasComunes();
        
        // 2. Validar reglas específicas del tipo (Polimorfismo) 
        nuevaReserva.validarReglasEspecificas();

        // 3. Validar solapamiento (regla de negocio principal) [cite: 7]
        for (Reserva existente : this.reservas) {
            if (nuevaReserva.seSolapaCon(existente)) {
                throw new ValidacionException(
                        String.format("Conflicto de horario: El aula ya está reservada en esa fecha/hora (ID Reserva: %d)", existente.getId())
                );
            }
        }

        // Si pasa todas las validaciones, se añade
        this.reservas.add(nuevaReserva);
        System.out.println("Reserva registrada exitosamente (ID: " + nuevaReserva.getId() + ")");
    }
    
    public Optional<Reserva> buscarReservaPorId(int id) {
        return this.reservas.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }

    public List<Reserva> buscarReservasPorResponsable(String texto) {
        return this.reservas.stream()
                .filter(r -> r.getResponsable().toLowerCase().contains(texto.toLowerCase()))
                .collect(Collectors.toList());
    }


    public List<Reserva> listarReservas(Comparator<Reserva> comparador) {
        return this.reservas.stream()
                .sorted(comparador)
                .collect(Collectors.toList());
    }

    public void cancelarReserva(int id) throws ValidacionException {
        Reserva reserva = buscarReservaPorId(id)
                .orElseThrow(() -> new ValidacionException("No se encontró la reserva con ID " + id));
        
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
             throw new ValidacionException("La reserva ya se encuentra cancelada.");
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA); // Requisito [cite: 22]
        System.out.println("Reserva " + id + " cancelada exitosamente.");
    }
    

    public void modificarReserva(int id, Aula nuevaAula, LocalDate nuevaFecha, LocalTime nuevoInicio, LocalTime nuevoFin) throws ValidacionException {
        Reserva reservaAModificar = buscarReservaPorId(id)
                .orElseThrow(() -> new ValidacionException("No se encontró la reserva con ID " + id));
        
        // Guarda los datos originales por si falla la validación
        Aula aulaOriginal = reservaAModificar.getAula();
        LocalDate fechaOriginal = reservaAModificar.getFecha();
        LocalTime inicioOriginal = reservaAModificar.getHoraInicio();
        LocalTime finOriginal = reservaAModificar.getHoraFin();

        // Aplica los nuevos datos temporalmente
        reservaAModificar.setAula(nuevaAula);
        reservaAModificar.setFecha(nuevaFecha);
        reservaAModificar.setHoraInicio(nuevoInicio);
        reservaAModificar.setHoraFin(nuevoFin);

        try {
            // Validar reglas (internas y de tipo)
            reservaAModificar.validarReglasComunes();
            reservaAModificar.validarReglasEspecificas();

            // Validar solapamiento contra OTRAS reservas
            for (Reserva existente : this.reservas) {
                if (existente.getId() != reservaAModificar.getId() && reservaAModificar.seSolapaCon(existente)) {
                    throw new ValidacionException(
                            String.format("Conflicto de horario al modificar: Se solapa con la reserva ID %d", existente.getId())
                    );
                }
            }
            
            System.out.println("Reserva " + id + " modificada exitosamente.");

        } catch (ValidacionException e) {
            // Si falla, revierte los cambios
            reservaAModificar.setAula(aulaOriginal);
            reservaAModificar.setFecha(fechaOriginal);
            reservaAModificar.setHoraInicio(inicioOriginal);
            reservaAModificar.setHoraFin(finOriginal);
            // Lanza la excepción hacia la UI
            throw e;
        }
    }


    // --- Reportes  ---

    public String generarReporteTopAulas() {
        StringBuilder reporte = new StringBuilder("--- Top 3 Aulas con más horas reservadas (Activas) ---\n");

        // Uso de Streams para agrupar y sumar 
        Map<Aula, Double> horasPorAula = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                        Reserva::getAula,
                        Collectors.summingDouble(Reserva::getDuracionHoras)
                ));

        // Ordenar el map por valor (horas) descendente
        horasPorAula.entrySet().stream()
                .sorted(Map.Entry.<Aula, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> {
                    reporte.append(String.format("1. Aula: %s (%s) - Total Horas: %.2f\n",
                            entry.getKey().getCodigo(),
                            entry.getKey().getNombre(),
                            entry.getValue()));
                });
        
        return reporte.toString();
    }

    public String generarReporteOcupacionPorTipoAula() {
        StringBuilder reporte = new StringBuilder("--- Ocupación por Tipo de Aula (Horas Activas) ---\n");

        Map<TipoAula, Double> horasPorTipo = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                        r -> r.getAula().getTipo(), // Agrupa por el tipo de aula de la reserva
                        Collectors.summingDouble(Reserva::getDuracionHoras)
                ));

        for (TipoAula tipo : TipoAula.values()) {
            reporte.append(String.format("- %s: %.2f horas\n",
                    tipo.toString(),
                    horasPorTipo.getOrDefault(tipo, 0.0)));
        }
        return reporte.toString();
    }
    
    public String generarReporteDistribucionReservas() {
        StringBuilder reporte = new StringBuilder("--- Distribución por Tipo de Reserva (Activas) ---\n");

        // Agrupa por el nombre simple de la clase (ReservaClase, ReservaPractica, etc.)
        Map<String, Long> conteoPorTipo = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                        r -> r.getClass().getSimpleName(), // Polimorfismo en acción
                        Collectors.counting()
                ));

        reporte.append(String.format("- Reservas de Clase: %d\n", conteoPorTipo.getOrDefault("ReservaClase", 0L)));
        reporte.append(String.format("- Reservas de Práctica: %d\n", conteoPorTipo.getOrDefault("ReservaPractica", 0L)));
        reporte.append(String.format("- Reservas de Evento: %d\n", conteoPorTipo.getOrDefault("ReservaEvento", 0L)));

        return reporte.toString();
    }

    // --- Persistencia  ---

    public void guardarDatos() {
        guardarAulas();
        guardarReservas();
        System.out.println("Datos guardados exitosamente en " + ARCHIVO_AULAS + " y " + ARCHIVO_RESERVAS);
    }

    private void guardarAulas() {
        // Uso de try-with-resources
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_AULAS))) {
            for (Aula aula : aulas) {
                // Formato: codigo,nombre,capacidad,tipo
                String linea = String.join(",",
                        aula.getCodigo(),
                        aula.getNombre(),
                        String.valueOf(aula.getCapacidad()),
                        aula.getTipo().toString()
                );
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar aulas: " + e.getMessage());
        }
    }

    private void guardarReservas() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_RESERVAS))) {
            for (Reserva reserva : reservas) {
                // Se usa el método polimórfico toCsvString()
                bw.write(reserva.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar reservas: " + e.getMessage());
        }
    }

    private void cargarAulas() {
        File file = new File(ARCHIVO_AULAS);
        if (!file.exists()) return; // No hay archivo para cargar

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    String[] datos = linea.split(",");
                    if (datos.length == 4) {
                        String codigo = datos[0];
                        String nombre = datos[1];
                        int capacidad = Integer.parseInt(datos[2]);
                        TipoAula tipo = TipoAula.valueOf(datos[3]);
                        aulas.add(new Aula(codigo, nombre, capacidad, tipo));
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar línea de aula (ignorada): " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar aulas: " + e.getMessage());
        }
    }

    private void cargarReservas() {
        File file = new File(ARCHIVO_RESERVAS);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    String[] datos = linea.split(",");
                    if (datos.length < 9) continue; // Línea inválida

                    int id = Integer.parseInt(datos[0]);
                    String tipoReserva = datos[1];
                    String aulaCodigo = datos[2];
                    String responsable = datos[3];
                    LocalDate fecha = LocalDate.parse(datos[4]);
                    LocalTime inicio = LocalTime.parse(datos[5]);
                    LocalTime fin = LocalTime.parse(datos[6]);
                    EstadoReserva estado = EstadoReserva.valueOf(datos[7]);

                    // Buscar el aula por código. Si no existe, no se puede cargar la reserva.
                    Aula aula = getAulaPorCodigo(aulaCodigo)
                            .orElseThrow(() -> new ValidacionException("No se encontró el aula " + aulaCodigo + " para la reserva " + id));

                    Reserva reserva = null;
                    // Polimorfismo en la carga de datos
                    switch (tipoReserva) {
                        case "CLASE":
                            if (datos.length == 10) {
                                reserva = new ReservaClase(id, aula, responsable, fecha, inicio, fin, estado, datos[8], datos[9]);
                            }
                            break;
                        case "PRACTICA":
                             if (datos.length == 10) {
                                reserva = new ReservaPractica(id, aula, responsable, fecha, inicio, fin, estado, datos[8], datos[9]);
                            }
                            break;
                        case "EVENTO":
                             if (datos.length == 10) {
                                TipoEvento tipoEv = TipoEvento.valueOf(datos[8]);
                                int publico = Integer.parseInt(datos[9]);
                                reserva = new ReservaEvento(id, aula, responsable, fecha, inicio, fin, estado, tipoEv, publico);
                            }
                            break;
                    }
                    
                    if (reserva != null) {
                        reservas.add(reserva);
                    } else {
                         System.err.println("Datos de reserva corruptos o incompletos: " + linea);
                    }

                } catch (Exception e) {
                    System.err.println("Error al procesar línea de reserva (ignorada): " + linea + " | Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar reservas: " + e.getMessage());
        }
    }
    
    public void exportarReporte(String nombreArchivo, String contenido) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            bw.write("--- Reporte Generado el " + LocalDate.now() + " a las " + LocalTime.now() + " ---\n");
            bw.write(contenido);
            System.out.println("Reporte exportado exitosamente a " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al exportar el reporte: " + e.getMessage());
        }
    }
}
