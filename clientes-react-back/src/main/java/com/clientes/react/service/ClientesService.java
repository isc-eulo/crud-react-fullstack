package com.clientes.react.service;

import com.clientes.react.dominio.entity.Cliente;
import com.clientes.react.dominio.facade.ClientesFacade;
import com.clientes.react.dominio.repository.ClientesRepository;


import com.clientes.react.dto.ClienteRequestDTO;
import com.clientes.react.dto.ClienteResponseDTO;
import com.clientes.react.dto.RegistroProcesoDTO;
import com.clientes.react.exception.BdaException;
import com.clientes.react.exception.InternalException;
import com.clientes.react.exception.NegocioException;
import com.clientes.react.service.validacion.ClienteValidacion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional; // <--- USA ESTOimport lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientesService {

    private static final Logger log = LoggerFactory.getLogger(ClientesService.class);
    private final ClientesFacade clientesFacade;

    private final ClienteValidacion  clienteValidator;
    /**
     * Método para guardar
     * Crea un flujo reactivo que envuelve una tarea síncrona.
     * * @param callable Bloque de código o método que devuelve un valor (ej: un mapper).
     * @return Un Mono que emitirá el resultado del callable cuando se inicie la suscripción.
     * @throws Exception Si el código dentro del callable falla, el Mono emite una señal de error.
     */
    public Mono<ClienteResponseDTO> guardarNuevo(ClienteRequestDTO dto) {
       log.info("cliente creado");
        return Mono.fromCallable(() -> mapToEntity(dto))
                // BLOQUE 1: Error en Mapper -> El DTO viene mal o falla el builder
                .onErrorMap(e -> new InternalException("Fallo en conversión de datos de entrada", HttpStatus.INTERNAL_SERVER_ERROR))

                // VALIDACIÓN: Separada en clase externa para limpieza
                .flatMap(clienteValidator::validarIdentificacionUnica)

                // PERSISTENCIA: Solo llegamos aquí si la validación pasó (estaba empty)
                .flatMap(entidad -> clientesFacade.crear(entidad)
                        // BLOQUE 4: Error en transacción -> Fallo al insertar
                        .onErrorMap(e -> new BdaException("Error al persistir el nuevo cliente", HttpStatus.INTERNAL_SERVER_ERROR)))

                .map(this::mapToResponseDTO)

                // CATCH GLOBAL: Captura errores no previstos (NullPointer, etc)
                .onErrorMap(this::handleGlobalError);
    }


    /**
     * Procesa la creación de un cliente y la carga de documentos pesados con limpieza total en caso de fallo.
     * * Estrategia:
     * 1. Persiste el cliente (liberando la transacción de DB inmediatamente).
     * 2. Inicia streaming de archivos a disco con concurrencia 1.
     * 3. Si ocurre un error en archivos, elimina la carpeta física y hace "rollback manual" en la DB.

    public Mono<ClienteResponseDTO> guardarConDocumentos(Mono<ClienteRequestDTO> dtoMono, Flux<FilePart> archivos) {
        return dtoMono
                .flatMap(dto -> Mono.fromCallable(() -> mapToEntity(dto))
                        .flatMap(clienteValidator::validarIdentificacionUnica)
                        .flatMap(entidad -> clientesFacade.crear(entidad))
                        .onErrorMap(e -> new BdaException("Error al crear el registro del cliente: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR))
                )
                .flatMap(clienteGuardado -> {
                    // 1. Definir la ruta base (estaba comentada, es necesaria para el flujo)
                    Path rutaBase = Paths.get("almacen", clienteGuardado.getId().toString());

                    // 2. Usar defer para asegurar que la creación del directorio ocurra en la suscripción
                    return Mono.fromRunnable(() ->
                                    crearDirectorio(rutaBase))
                            .subscribeOn(Schedulers.boundedElastic()) // La creación de carpetas es bloqueante
                            .thenMany(archivos)
                            .flatMap(filePart -> {
                                Path rutaArchivo = rutaBase.resolve(filePart.filename());

                                // 3. Retornar el flujo de transferencia de archivos
                                return filePart.transferTo(rutaArchivo)
                                        .onErrorMap(e -> new InternalException("Error de escritura en disco: " + filePart.filename(), HttpStatus.INSUFFICIENT_STORAGE))
                                        .then(Mono.just(rutaArchivo));
                            }, 1)
                            .collectList()
                            .map(archivosGuardados -> mapToResponseDTO(clienteGuardado))
                          //BLOQUE DE COMPENSACIÓN TOTAL
                            .onErrorResume(e -> {
                                log.error("Fallo crítico en carga. Limpiando rastro del cliente ID: {}", clienteGuardado.getId());

                                // 4. Limpiar Disco (Descomentado)
                             //   eliminarCarpetaRecursivo(rutaBase);

                                // 5. Limpiar DB y propagar error
                              //  return clientesFacade.eliminarFisicamente(clienteGuardado.getId())
                                  //      .then(Mono.error(e));
                            });
                })
                .onErrorMap(this::handleGlobalError);
    }**/

    /**
     * Procesa un máximo de 100 registros.
     * Los agrupa en lotes de 10. Cada lote es una transacción atómica.
     * Si el registro 11 falla, se hace rollback del segundo lote (11-20),
     * pero el primer lote (1-10) ya quedó guardado. El flujo se detiene.
     */
    public Flux<ClienteResponseDTO> guardarConLimiteYBloques(Flux<ClienteRequestDTO> flujoClientes) {
        return flujoClientes
                /* 1. LÍMITE TOTAL: Solo aceptamos los primeros 100 registros */
                .take(100)

                /* 2. VENTANAS: Agrupamos en bloques de 10 */
                .window(10)

                /* 3. CONCATENACIÓN: Usamos concatMap para asegurar que un lote
                   se termine (y haga commit) antes de empezar el siguiente.
                   Si un lote lanza error, concatMap detiene el flujo global. */
                .concatMap(loteFlux ->
                        loteFlux.collectList() // Convertimos a lista para el @Transactional
                                .flatMapMany(this::procesarLoteDiezEnDiez)
                )
                .onErrorMap(e -> {
                    log.error("Proceso abortado críticamente: {}", e.getMessage());
                    return e; // Propaga el error para detener todo
                });
    }

    /**
     * Transacción Atómica de 10 registros.
     */
    @Transactional
    public Flux<ClienteResponseDTO> procesarLoteDiezEnDiez(List<ClienteRequestDTO> lote) {
        return Flux.fromIterable(lote)
                .flatMap(dto -> Mono.fromCallable(() -> mapToEntity(dto))
                        .flatMap(clienteValidator::validarIdentificacionUnica)
                        .flatMap(entidad -> clientesFacade.crear(entidad))
                        .map(this::mapToResponseDTO)
                );
    }
/// ---------------------fin lote 1 1000


    /**
     * Procesa una carga masiva de clientes de forma reactiva y asíncrona.
     * A diferencia del procesamiento por lotes, aquí cada registro es independiente,
     * permitiendo que los registros válidos se guarden aunque otros fallen.
     *
     * @param flujoEntrada Flux de DTOs que llegan desde el cliente (ej. NDJSON).
     * @return Flux de resultados individuales para cada intento de registro.
     */
    public Flux<RegistroProcesoDTO> guardarMasivoContinuo(Flux<ClienteRequestDTO> flujoEntrada) {
        return flujoEntrada
                /* * flatMap procesa cada elemento del flujo. El segundo parámetro (concurrency)
                 * permite definir cuántas inserciones simultáneas tolera la base de datos.
                 */
                .flatMap(dto ->
                        procesarYCapturar(dto)
                                /* * onErrorResume es el "try-catch" reactivo. Si procesarYCapturar lanza una
                                 * excepción, se captura aquí para evitar que el Flux principal muera.
                                 */
                                .onErrorResume(e -> Mono.just(construirDtoError(e, dto.getIdentificacion())))
                );
    }

    /**
     * Realiza la lógica de negocio y persistencia para un solo cliente.
     * * @param dto Datos de entrada del cliente.
     * @return Mono con el resultado exitoso del proceso.
     */
    private Mono<RegistroProcesoDTO> procesarYCapturar(ClienteRequestDTO dto) {
        /* Convierte el DTO de entrada en una entidad de dominio Cliente */
        return Mono.fromCallable(() -> mapToEntity(dto))
                /* Verifica en la base de datos que la identificación no esté duplicada */
                .flatMap(clienteValidator::validarIdentificacionUnica)
                /* * Llama al Facade para guardar. Aquí se activa el @Transactional individual
                 * definido en ClientesFacade.crear(cliente).
                 */
                .flatMap(entidad -> clientesFacade.crear(entidad))
                /* Si la persistencia fue exitosa, construye el DTO de respuesta positiva */
                .map(this::construirDtoExito);
    }

    /// fin cliente uno en uno


    /**
     * Método de apoyo para limpiar el catch global del flujo principal
     */
    private Throwable handleGlobalError(Throwable e) {
        if (e instanceof NegocioException || e instanceof BdaException || e instanceof InternalException) {
            return e;
        }
        return new InternalException("Error no controlado en guardar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Actualiza un cliente existente validando que la nueva identificación no la tenga otro.
     */
    public Mono<ClienteResponseDTO> actualizarCliente(Long id, ClienteRequestDTO dto) {
        log.info("cliente actualizar");
        return Mono.fromCallable(() -> mapToEntity(dto))
                // BLOQUE 1: Error en Mapper
                .onErrorMap(e -> new InternalException("Error de mapeo para actualización", HttpStatus.INTERNAL_SERVER_ERROR))

                // VALIDACIÓN: Usamos el método de actualización del validador
                .flatMap(entidad -> clienteValidator.validarIdentificacionParaActualizar(entidad, id))

                // PERSISTENCIA: Ejecutamos el cambio
                .flatMap(entidad -> clientesFacade.actualizar(id, entidad)
                        .onErrorMap(e -> new BdaException("Error en base de datos al actualizar", HttpStatus.INTERNAL_SERVER_ERROR)))

                .map(this::mapToResponseDTO)
                // CATCH GLOBAL
                .onErrorMap(this::handleGlobalError);
    }

    /**
     * Lista todos los clientes transformándolos a DTO.
     */
    public Flux<ClienteResponseDTO> listarTodos() {
        log.info("cliente consulta todos");
        return clientesFacade.obtenerTodos()
                // BLOQUE 2: Error de Base de Datos
                .onErrorMap(e -> new BdaException("Error al recuperar lista de clientes", HttpStatus.INTERNAL_SERVER_ERROR))
                .map(this::mapToResponseDTO)
                // BLOQUE 1: Si el mapeo de salida falla
                .onErrorMap(e -> !(e instanceof BdaException),
                        e -> new InternalException("Error al procesar datos de salida", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Busca un cliente por su ID único.
     */
    public Mono<ClienteResponseDTO> buscarPorId(Long id) {
        log.info("cliente consulta ");
        return clientesFacade.obtenerPorId(id)
                .onErrorMap(e -> new BdaException("Error de conexión al buscar por ID", HttpStatus.INTERNAL_SERVER_ERROR))
                .map(this::mapToResponseDTO)
                // Si el ID no existe, lanzamos excepción de Negocio
                .switchIfEmpty(Mono.error(new NegocioException("Cliente no encontrado con ID: " + id, HttpStatus.NOT_FOUND)));
    }

    /**
     * Elimina un cliente previa validación de existencia.
     */
    public Mono<Void> eliminarCliente(Long id) {
        log.info("cliente elimina ");
        return clientesFacade.obtenerPorId(id)
                .switchIfEmpty(Mono.error(new NegocioException("No existe el cliente a eliminar", HttpStatus.NOT_FOUND)))
                .flatMap(c -> clientesFacade.eliminar(id))
                .onErrorMap(e -> !(e instanceof NegocioException),
                        e -> new BdaException("Fallo de base de datos al intentar eliminar", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Consulta clientes por correo (Retorna Lista/Flux).
     */
    public Flux<ClienteResponseDTO> listarPorEmail(String email) {
        return clientesFacade.buscarPorEmail(email)
                .onErrorMap(e -> new BdaException("Error al consultar clientes por email", HttpStatus.INTERNAL_SERVER_ERROR))
                .map(this::mapToResponseDTO);
    }

    // --- MAPPERS INTERNOS ---

    private Cliente mapToEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombre(dto.getNombre())
                .identificacion(dto.getIdentificacion())
                .email(dto.getEmail())
                .build();
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente entity) {
        return ClienteResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .identificacion(entity.getIdentificacion())
                .email(entity.getEmail())
                .build();
    }


    /**
     * Transforma un error ocurrido en el flujo en un objeto de respuesta legible.
     * * @param t La excepción capturada (Negocio, BDA o Sistema).
     * @param identificacion El ID del cliente que causó el fallo.
     * @return DTO con el detalle del error y su clasificación.
     */
    private RegistroProcesoDTO construirDtoError(Throwable t, String identificacion) {
        /* Determinamos el tipo de error basado en la clase de la excepción */
        String estado = (t instanceof NegocioException) ? "ERROR_NEGOCIO" : "ERROR_SISTEMA";

        /* Si la base de datos falla, se clasifica específicamente */
        if (t instanceof BdaException) estado = "ERROR_BASE_DATOS";

        return RegistroProcesoDTO.builder()
                .identificacion(identificacion)
                .estado(estado)
                .mensaje(t.getMessage()) // Descripción técnica o de negocio del error
                .fecha(LocalDateTime.now()) // Marca de tiempo del fallo
                .build();
    }

    /**
     * Construye el objeto de éxito tras una persistencia correcta.
     * * @param entidad Cliente guardado en la base de datos.
     * @return DTO con estado EXITOSO.
     */
    private RegistroProcesoDTO construirDtoExito(Cliente entidad) {
        return RegistroProcesoDTO.builder()
                .identificacion(entidad.getIdentificacion())
                .estado("EXITOSO")
                .mensaje("Registro procesado correctamente")
                .fecha(LocalDateTime.now())
                .build();
    }


    /// /solo queda de e ejemplo

    /**
     * Procesa un millón de datos en bloques de 1,000.
     * Si un lote falla, se hace rollback de esos 1,000, pero el proceso
     * salta al siguiente bloque de 1,000 sin detenerse.
     */
    public Flux<RegistroProcesoDTO> guardarMasivoPorLotes(Flux<ClienteRequestDTO> flujoEntrada) {
        return flujoEntrada
                /* 1. Agrupamos los datos en listas de 1,000 elementos */
                .buffer(1000)
                /* 2. flatMap procesa cada lista (lote) */
                .flatMap(lote ->
                        procesarLoteTransaccional(lote)
                                /* * 3. CRUCIAL: Si el lote entero falla (por 1 solo error),
                                 * capturamos la excepción aquí para que el flujo de 1M siga vivo.
                                 */
                                .onErrorResume(e -> {
                                    log.error("Lote fallido completamente. Causa: {}", e.getMessage());
                                    // Retornamos un DTO indicando que el lote falló
                                    return Flux.just(RegistroProcesoDTO.builder()
                                            .estado("ERROR_LOTE")
                                            .mensaje("Error en bloque: " + e.getMessage())
                                            .fecha(LocalDateTime.now())
                                            .build());
                                })
                );
    }

    /**
     * Método atómico para un grupo de 1,000.
     * El @Transactional asegura que si uno falla, los 1,000 se deshacen.
     */
    @Transactional
    private Flux<RegistroProcesoDTO> procesarLoteTransaccional(List<ClienteRequestDTO> lista) {
        return Flux.fromIterable(lista)
                .flatMap(dto ->
                        Mono.fromCallable(() -> mapToEntity(dto))
                                .flatMap(clienteValidator::validarIdentificacionUnica)
                                /* * Aquí usamos el Facade. Si el crear() lanza BdaException,
                                 * disparará el rollback de TODO este método procesarLoteTransaccional.
                                 */
                                .flatMap(clientesFacade::crear)
                                .map(this::construirDtoExito)
                );
    }
}