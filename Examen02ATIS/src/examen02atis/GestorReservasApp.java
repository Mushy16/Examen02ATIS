/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package examen02atis;

/**
 *
 * @author Fernando Enrique Bermudez Torres
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class GestorReservasApp {

    private final SistemaGestion sistema;
    private final Scanner scanner;

    public GestorReservasApp() {
        this.sistema = new SistemaGestion();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        GestorReservasApp app = new GestorReservasApp();
        app.run();
    }

    public void run() {
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");

            // Estructura de control switch [cite: 15]
            switch (opcion) {
                case 1:
                    manejarMenuAulas();
                    break;
                case 2:
                    manejarMenuReservas();
                    break;
                case 3:
                    manejarMenuReportes();
                    break;
                case 0:
                    salir = true;
                    sistema.guardarDatos(); // Persistencia al salir 
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        System.out.println("Gracias por usar el Gestor de Reservas ITCA. ¡Adiós!");
        scanner.close();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n--- Gestor de Reservas de Aulas ITCA ---");
        System.out.println("1. Gestión de Aulas");
        System.out.println("2. Gestión de Reservas");
        System.out.println("3. Reportes");
        System.out.println("0. Salir y Guardar");
    }

    // --- Sub-Menús ---

    private void manejarMenuAulas() {
        System.out.println("\n--- Gestión de Aulas --- ");
        System.out.println("1. Registrar nueva aula");
        System.out.println("2. Listar todas las aulas");
        System.out.println("3. Modificar aula existente");
        System.out.println("0. Volver al menú principal");
        int opcion = leerEntero("Seleccione: ");

        // Uso de try/catch para capturar excepciones [cite: 15]
        try {
            switch (opcion) {
                case 1: registrarAula(); break;
                case 2: listarAulas(); break;
                case 3: modificarAula(); break;
                case 0: break;
                default: System.out.println("Opción no válida.");
            }
        } catch (ValidacionException e) {
            // Muestra el mensaje de la excepción personalizada [cite: 14, 25]
            System.err.println("Error de Validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }

    private void manejarMenuReservas() {
        System.out.println("\n--- Gestión de Reservas ---");
        System.out.println("1. Registrar nueva reserva");
        System.out.println("2. Listar reservas (con ordenamiento)");
        System.out.println("3. Modificar reserva (fecha/hora/aula)");
        System.out.println("4. Cancelar reserva");
        System.out.println("5. Buscar reservas por responsable");
        System.out.println("0. Volver al menú principal");
        int opcion = leerEntero("Seleccione: ");
        
         try {
            switch (opcion) {
                case 1: registrarReserva(); break;
                case 2: listarReservas(); break;
                case 3: modificarReserva(); break;
                case 4: cancelarReserva(); break;
                case 5: buscarPorResponsable(); break;
                case 0: break;
                default: System.out.println("Opción no válida.");
            }
        } catch (ValidacionException e) {
            System.err.println("Error de Validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
    }
    
    private void manejarMenuReportes() {
        System.out.println("\n--- Módulo de Reportes --- ");
        System.out.println("1. Top 3 aulas con más horas reservadas");
        System.out.println("2. Ocupación por tipo de aula");
        System.out.println("3. Distribución por tipo de reserva");
        System.out.println("4. Exportar todos los reportes a archivo");
        System.out.println("0. Volver al menú principal");
        int opcion = leerEntero("Seleccione: ");

        String rptTopAulas = sistema.generarReporteTopAulas();
        String rptOcupacion = sistema.generarReporteOcupacionPorTipoAula();
        String rptDistribucion = sistema.generarReporteDistribucionReservas();

        switch (opcion) {
            case 1:
                System.out.println(rptTopAulas);
                break;
            case 2:
                System.out.println(rptOcupacion);
                break;
            case 3:
                System.out.println(rptDistribucion);
                break;
            case 4:
                String contenidoCompleto = rptTopAulas + "\n" + rptOcupacion + "\n" + rptDistribucion;
                String nombreArchivo = "Reporte_General_" + LocalDate.now() + ".txt";
                sistema.exportarReporte(nombreArchivo, contenidoCompleto);
                break;
            case 0:
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }


    // --- Lógica de Aulas ---

    private void registrarAula() throws ValidacionException {
        System.out.println("--- Registrar Nueva Aula ---");
        String codigo = leerTexto("Ingrese código único: ");
        String nombre = leerTexto("Ingrese nombre del aula): ");
        int capacidad = leerEntero("Ingrese capacidad: ");
        TipoAula tipo = seleccionarEnum(TipoAula.class, "Seleccione el tipo de aula:");

        Aula nuevaAula = new Aula(codigo, nombre, capacidad, tipo);
        sistema.registrarAula(nuevaAula);
    }

    private void listarAulas() {
        System.out.println("--- Listado de Aulas ---");
        List<Aula> aulas = sistema.getAulas();
        if (aulas.isEmpty()) {
            System.out.println("No hay aulas registradas.");
            return;
        }
        aulas.forEach(System.out::println);
    }

    private void modificarAula() throws ValidacionException {
        System.out.println("--- Modificar Aula ---");
        String codigo = leerTexto("Ingrese el código del aula a modificar: ");
        
        // Asegurarse que el aula existe
        Aula aula = sistema.getAulaPorCodigo(codigo)
                .orElseThrow(() -> new ValidacionException("No se encontró el aula con código " + codigo));

        System.out.println("Datos actuales: " + aula);
        String nuevoNombre = leerTexto("Ingrese nuevo nombre (" + aula.getNombre() + "): ");
        int nuevaCapacidad = leerEntero("Ingrese nueva capacidad (" + aula.getCapacidad() + "): ");
        TipoAula nuevoTipo = seleccionarEnum(TipoAula.class, "Seleccione nuevo tipo (" + aula.getTipo() + "):");

        sistema.modificarAula(codigo, nuevoNombre, nuevaCapacidad, nuevoTipo);
    }

    // --- Lógica de Reservas ---
    
    private void registrarReserva() throws ValidacionException {
        System.out.println("--- Registrar Nueva Reserva ---");
        
        // 1. Seleccionar Aula
        Aula aula = seleccionarAula();
        if (aula == null) return; // Si no hay aulas, no se puede continuar

        // 2. Datos comunes
        String responsable = leerTexto("Nombre del responsable: ");
        LocalDate fecha = leerFecha("Fecha de la reserva (YYYY-MM-DD): ");
        LocalTime horaInicio = leerHora("Hora de inicio (HH:MM): ");
        LocalTime horaFin = leerHora("Hora de fin (HH:MM): ");

        // 3. Datos específicos (Polimorfismo)
        System.out.println("Seleccione el tipo de reserva:");
        System.out.println("1. Clase");
        System.out.println("2. Práctica");
        System.out.println("3. Evento");
        int tipo = leerEntero("Opción: ");

        Reserva nuevaReserva;

        switch (tipo) {
            case 1:
                String carrera = leerTexto("Carrera: ");
                String asignatura = leerTexto("Código de Asignatura: ");
                nuevaReserva = new ReservaClase(aula, responsable, fecha, horaInicio, horaFin, carrera, asignatura);
                break;
            case 2:
                String nomPractica = leerTexto("Nombre de la práctica: ");
                String equipo = leerTexto("Equipo requerido: ");
                nuevaReserva = new ReservaPractica(aula, responsable, fecha, horaInicio, horaFin, nomPractica, equipo);
                break;
            case 3:
                TipoEvento tipoEvento = seleccionarEnum(TipoEvento.class, "Seleccione el tipo de evento:");
                int publico = leerEntero("Público estimado: ");
                nuevaReserva = new ReservaEvento(aula, responsable, fecha, horaInicio, horaFin, tipoEvento, publico);
                break;
            default:
                throw new ValidacionException("Tipo de reserva no válido.");
        }
        
        // El sistema se encarga de validar todo [cite: 7]
        sistema.registrarReserva(nuevaReserva);
    }
    
    private void listarReservas() {
        System.out.println("--- Listar Reservas ---");
        System.out.println("Seleccione criterio de ordenamiento:");
        System.out.println("1. Por ID (Más nuevas primero)");
        System.out.println("2. Por Fecha (Más próximas primero)");
        System.out.println("3. Por Responsable (A-Z)");
        int opcion = leerEntero("Opción: ");

        Comparator<Reserva> comparador;
        switch (opcion) {
            case 1:
                comparador = Comparator.comparingInt(Reserva::getId).reversed();
                break;
            case 2:
                comparador = Comparator.comparing(Reserva::getFecha).thenComparing(Reserva::getHoraInicio);
                break;
            case 3:
                comparador = Comparator.comparing(Reserva::getResponsable);
                break;
            default:
                System.out.println("Opción inválida, ordenando por ID.");
                comparador = Comparator.comparingInt(Reserva::getId).reversed();
        }

        List<Reserva> reservas = sistema.listarReservas(comparador);
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        reservas.forEach(System.out::println);
    }

    private void modificarReserva() throws ValidacionException {
        System.out.println("--- Modificar Reserva ---");
        int id = leerEntero("Ingrese el ID de la reserva a modificar: ");
        Reserva reserva = sistema.buscarReservaPorId(id)
                .orElseThrow(() -> new ValidacionException("No se encontró la reserva con ID " + id));

        System.out.println("Datos actuales: " + reserva);
        System.out.println("--- Ingrese nuevos datos (deje en blanco para no cambiar) ---");
        
        // Modificación de Aula
        System.out.println("Aula actual: " + reserva.getAula().getCodigo());
        Aula nuevaAula = seleccionarAula("Nuevo código de aula (o enter para mantener): ", true);
        if (nuevaAula == null) nuevaAula = reserva.getAula(); // Mantener la original

        // Modificación de Fecha
        LocalDate nuevaFecha = leerFecha("Nueva fecha (" + reserva.getFecha() + ") (YYYY-MM-DD o enter): ", true);
        if (nuevaFecha == null) nuevaFecha = reserva.getFecha();

        // Modificación de Horas
        LocalTime nuevoInicio = leerHora("Nueva hora inicio (" + reserva.getHoraInicio() + ") (HH:MM o enter): ", true);
        if (nuevoInicio == null) nuevoInicio = reserva.getHoraInicio();
        
        LocalTime nuevoFin = leerHora("Nueva hora fin (" + reserva.getHoraFin() + ") (HH:MM o enter): ", true);
        if (nuevoFin == null) nuevoFin = reserva.getHoraFin();

        // Nota: La modificación de datos específicos (ej. carrera, tipo de evento)
        // requeriría una lógica más compleja (re-crear el objeto).
        // El requisito [cite: 22] es genérico, se implementa para los datos comunes.

        sistema.modificarReserva(id, nuevaAula, nuevaFecha, nuevoInicio, nuevoFin);
    }
    
    private void cancelarReserva() throws ValidacionException {
        System.out.println("--- Cancelar Reserva ---");
        int id = leerEntero("Ingrese el ID de la reserva a cancelar: ");
        sistema.cancelarReserva(id);
    }

    private void buscarPorResponsable() {
        System.out.println("--- Buscar por Responsable --- [cite: 28]");
        String texto = leerTexto("Ingrese el nombre (o parte del nombre) del responsable: ");
        List<Reserva> resultados = sistema.buscarReservasPorResponsable(texto);
        
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron reservas para '" + texto + "'.");
            return;
        }
        
        System.out.println("Resultados de la búsqueda:");
        resultados.forEach(System.out::println);
    }


    // --- Métodos de Ayuda (Helpers) para entrada robusta [cite: 25] ---

    private String leerTexto(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("La entrada no puede estar vacía.");
            }
        }
    }

    private int leerEntero(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero válido.");
            }
        }
    }

    private LocalDate leerFecha(String prompt) {
        return leerFecha(prompt, false); // No opcional por defecto
    }

    private LocalDate leerFecha(String prompt, boolean opcional) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
        while (true) {
            try {
                String input = leerTexto(prompt);
                if (opcional && input.isEmpty()) return null; // Permite entrada vacía
                return LocalDate.parse(input, formatter); // Validacion [cite: 18]
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de fecha incorrecto. Use YYYY-MM-DD.");
            }
        }
    }

    private LocalTime leerHora(String prompt) {
       return leerHora(prompt, false); // No opcional por defecto
    }

    private LocalTime leerHora(String prompt, boolean opcional) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME; // HH:MM
        while (true) {
            try {
                String input = leerTexto(prompt);
                 if (opcional && input.isEmpty()) return null; // Permite entrada vacía
                return LocalTime.parse(input, formatter); // Validacion [cite: 18]
            } catch (DateTimeParseException e) {
                System.out.println("Error: Formato de hora incorrecto. Use HH:MM (ej. 09:00 o 14:30).");
            }
        }
    }


    private <T extends Enum<T>> T seleccionarEnum(Class<T> enumClass, String prompt) {
        System.out.println(prompt);
        T[] constants = enumClass.getEnumConstants();
        for (int i = 0; i < constants.length; i++) {
            System.out.printf("%d. %s\n", i + 1, constants[i].toString());
        }

        while (true) {
            int opcion = leerEntero("Seleccione (1-" + constants.length + "): ");
            if (opcion >= 1 && opcion <= constants.length) {
                return constants[opcion - 1];
            } else {
                System.out.println("Opción fuera de rango.");
            }
        }
    }
    
    private Aula seleccionarAula() throws ValidacionException {
        return seleccionarAula("Ingrese el código del aula deseada: ", false);
    }

    private Aula seleccionarAula(String prompt, boolean opcional) throws ValidacionException {
        listarAulas();
        List<Aula> aulas = sistema.getAulas();
        if (aulas.isEmpty()) {
            System.err.println("Error: No hay aulas registradas. Registre un aula primero.");
            return null;
        }
        
        while (true) {
            String codigo = leerTexto(prompt);
            if(opcional && codigo.isEmpty()) return null; // Permite saltar

            Optional<Aula> aula = sistema.getAulaPorCodigo(codigo);
            if (aula.isPresent()) {
                return aula.get();
            } else {
                System.out.println("Código de aula no válido. Intente de nuevo.");
            }
        }
    }
}
