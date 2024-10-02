package com.bezkoder.springjwt.services.implementations;

import com.bezkoder.springjwt.exceptions.EmailExistsException;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.response.AspiranteCohorteResponse;
import com.bezkoder.springjwt.dto.AspiranteDTO;
import com.bezkoder.springjwt.dto.UserDTO;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.interfaces.AspiranteServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar las operaciones relacionadas con los aspirantes.
 * @author Julian Camilo Riveros Fonseca, Juan Pablo Correa Tarazona, Angel Yesid Duque Cruz, Ingrid Florez, Javier Lopez
 */
@Service
public class AspiranteService implements AspiranteServiceInterface {

    @Autowired
    private AspiranteRepository aspiranteRepository;
    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private EstadoRepository estadoRepository;
    @Autowired
    private CohorteRepository cohorteRepository;
    @Autowired
    private FileStorageRepository fileStorageRepository;
    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private  EstadoHistorialRepository estadoHistorialRepository;

    private static final Logger logger = LoggerFactory.getLogger(AspiranteService.class);

    /**
     * Registra un nuevo aspirante con los datos proporcionados.
     * 
     * @param aspirante El objeto ApplicantDTO que contiene los datos del
     *                  solicitante.
     * @return Un objeto ApplicantDTO que representa al nuevo solicitante
     *         registrado.
     * @throws EmailExistsException si el usuario correspondiente al ID
     *                              proporcionado
     *                              no se encuentra en la base de datos.
     */
    @Override
    public AspiranteDTO crearAspirante(AspiranteDTO aspirante, String email) throws EmailExistsException {
        // Log inicio del proceso
        logger.info("Iniciando la creación del aspirante para el email: " + email);

        // Buscar el usuario por correo
        User user = usuarioRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con el correo: " + email);
        }

        if (user.getAspirante() != null) {
            throw new EmailExistsException("El aspirante ya existe para este usuario.");
        }

        // Verificar si hay cohorte abierta
        CohorteEntity cohorteAbierto = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteAbierto == null) {
            throw new EntityNotFoundException("No hay cohorte abierta actualmente.");
        }

        AspiranteEntity newApplicant = null;

        try {
            // Crear la entidad Aspirante y copiar los datos del DTO
            AspiranteEntity aspiranteEntity = new AspiranteEntity();
            BeanUtils.copyProperties(aspirante, aspiranteEntity);

            // Asignar el correo del usuario
            aspiranteEntity.setCorreoPersonal(user.getEmail());

            // Relacionar al usuario y el cohorte con el aspirante
            aspiranteEntity.setUser(user);
            aspiranteEntity.setEstado(estadoRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("Estado con ID 1 no encontrado")));
            aspiranteEntity.setCohorte(cohorteAbierto);

            // Guardar el aspirante en la base de datos
            newApplicant = aspiranteRepository.save(aspiranteEntity);

            logger.info("Aspirante creado correctamente con ID: " + newApplicant.getId());

        } catch (Exception e) {
            logger.error("Error al guardar el aspirante: " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar aspirante", e);
        }

        // Crear directorio para el aspirante
        try {
            fileStorageRepository.createDirectory(cohorteAbierto.getId() + "/" + newApplicant.getId().toString());
            logger.info("Directorio creado para el aspirante con ID: " + newApplicant.getId());
        } catch (Exception e) {
            logger.error("Error al crear el directorio: " + e.getMessage(), e);
            // Eliminar el aspirante en caso de que la creación del directorio falle
            aspiranteRepository.delete(newApplicant);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear carpeta", e);
        }

        // Mapear el aspirante creado a un DTO para retornar
        AspiranteDTO newAspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(newApplicant, newAspiranteDTO);

        logger.info("Proceso de creación del aspirante completado con éxito.");

        return newAspiranteDTO;
    }


    /**
     * Obtiene el usuario del aspirante
     * 
     * @param id ID del aspirante
     * @return el usuario que le pertenece al aspirante.
     */
    @Override
    public UserDTO getUserByAspirante(Integer id) {
        AspiranteEntity aspirante = aspiranteRepository.findById(id).get();
        User user = aspirante.getUser();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    /**
     * 
     * Este método se encarga de desactivar un aspirante en la base de datos
     * mediante su ID de usuario.
     * 
     * @param email el email del usuario.
     * @return Un objeto AspiranteDTO que representa al aspirante desactivado en la
     *         base de datos.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   de usuario proporcionado.
     */
    @Override
    public void disableAspirante(String email) {
        AspiranteEntity aspiranteEntity = aspiranteRepository.findByCorreoPersonal(email);
        if (aspiranteEntity == null) {
            throw new UsernameNotFoundException("Aspirante con el email " + email + " no fue encontrado");
        }
        aspiranteEntity.setEstado(estadoRepository.findByDescripcion("DESACTIVADO"));
        try {
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al desactivar aspirante");
        }
    }

    /**
     * Este método se encarga de activar un aspirante en la base de datos
     * mediante su ID de usuario.
     * 
     * @param email el email del usuario.
     * @return Un objeto AspiranteDTO que representa al aspirante desactivado en la
     *         base de datos.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   de usuario proporcionado.
     */
    @Override
    public void enableAspirante(String email) {
        AspiranteEntity applicantEntity = aspiranteRepository.findByCorreoPersonal(email);
        if (applicantEntity == null)
            throw new UsernameNotFoundException("Aspirante con el email " + email + " no fue encontrado");
        applicantEntity.setEstado(estadoRepository.findById(1).get());
        try {
            aspiranteRepository.save(applicantEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al activar aspirante");
        }
    }

    /**
     * Obtiene el aspirante a través del id de la cuenta.
     *
     * @param email email del usuario
     * @return @return un aspiranteDTO correspondiente al aspirante.
     */
    @Override
    public AspiranteCohorteResponse getAspiranteByUserEmail(String email) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findByUser_Email(email);
        if (!aspirante.isPresent()) {
            throw new UsernameNotFoundException("No existe ningún aspirante asociado al email.");
        }
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteCohorteResponse aspiranteRetornado = new AspiranteCohorteResponse();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteRetornado);
        return aspiranteRetornado;
    }



    /**
     * busca un aspirante por id del aspirante
     * 
     * @param aspiranteId ID del aspirante
     * @return AspiranteDTO
     */
    @Override
    public AspiranteDTO getAspiranteByAspiranteId(Integer aspiranteId) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        aspiranteDTO.setTotal(aspiranteEntity.getTotal());
        BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
        return aspiranteDTO;
    }

    /**
     * Obtiene la lista de aspirantes para la cohorte actual.
     * Ordena a los aspirantes por nombre
     * 
     * @return Una lista de objetos AspiranteDTO que representan a los aspirantes de
     *         la cohorte actual.
     */
    @Override
    public List<AspiranteDTO> listarAspirantesCohorteActual() {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        List<AspiranteEntity> aspiranteEntities = new ArrayList<>(cohorteEntity.getAspirantes());
        aspiranteEntities.sort(Comparator.comparing(AspiranteEntity::getNombre));
        List<AspiranteDTO> aspirantes = aspiranteEntities.stream()
                .sorted(Comparator.comparing(AspiranteEntity::getNombre))
                .map(this::convertirAspiranteEntityADTO)
                .collect(Collectors.toList());
        return aspirantes;
    }

    /**
     * Obtiene el aspirante a través del id del aspirante.
     * 
     * @param id id del aspirante
     * @return un aspiranteDTO correspondiente al aspirante.
     */
    @Override
    public AspiranteDTO getAspiranteById(int id) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        AspiranteDTO aspiranteRetornado = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteRetornado);
        return aspiranteRetornado;
    }

    /**
     * Busca un aspirante por id y le asigna la fecha de la entrevista
     *
     * @param id               id del aspirante
     * @param fecha_entrevista fecha de la entrevista
     */
    @Override
    public void habilitarFechaEntrevista(Integer id, LocalDateTime fecha_entrevista) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        if (cohorteEntity == null) {
            throw new EntityNotFoundException("No hay cohorte habilitado.");
        }

        if (cohorteEntity.getEnlace_entrevista() == null) {
            throw new EntityNotFoundException("No se ha asignado un enlace de entrevista.");
        }

        // Usando orElseThrow para simplificar el código
        AspiranteEntity aspiranteEntity = aspiranteRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No existe ningún aspirante asociado con el ID: " + id));

        try {
            // Asignar la fecha de entrevista al aspirante y guardarlo en la base de datos
            aspiranteEntity.setFecha_entrevista(fecha_entrevista);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al asignar fecha de entrevista", e);
        }

        // Formatear la fecha para la notificación
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm");
        String fechaFormateada = fecha_entrevista.format(formatter);

        // Crear la notificación
        try {
            notificacionService.crearNotificacion(
                    "Tiene una entrevista programada para el día " + fechaFormateada + ". Este es el enlace para acceder: "
                            + cohorteEntity.getEnlace_entrevista(), aspiranteEntity.getId());
        } catch (Exception e) {
            // Manejar la excepción si el servicio de notificación falla
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar la notificación", e);
        }
    }


    /**
     * Califica la prueba de un aspirante.
     *
     * @param id                 el ID del aspirante.
     * @param calificacionPrueba la calificación de la prueba.
     * @return un objeto AspiranteDTO con los datos actualizados del aspirante.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   proporcionado.
     * @throws IllegalArgumentException  si la calificación de la prueba no es
     *                                   válida.
     * @throws EntityNotFoundException   si no se encuentra un enlace y una fecha de
     *                                   prueba.
     */
    @Override
    public void calificarPruebaAspirante(int id, int calificacionPrueba) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        CohorteEntity cohorteEntity = aspiranteEntity.getCohorte();
        if(!cohorteEntity.getHabilitado()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cohorte no está habilitada");
        }

        if (calificacionPrueba < 0 || calificacionPrueba > 40)
            throw new IllegalArgumentException("La calificacion de la prueba no es valida");

        if (cohorteEntity.getEnlace_prueba() == null || cohorteEntity.getFechaMaxPrueba() == null)
            throw new EntityNotFoundException("No existe un enlace y fecha de prueba");

        try {
            aspiranteEntity.setPuntaje_prueba(calificacionPrueba);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al calificar la prueba");
        }
    }

    /**
     * Califica las notas de pregrado de un aspirante.
     *
     * @param aspiranteId          El ID del aspirante.
     * @param puntajeCartas        Puntaje de las cartas de referencia.
     * @param puntajeNotasPregrado Puntaje de las notas de pregrado.
     * @param puntajePublicaciones Puntaje de las publicaciones y participaciones en congresos.
     * @param puntajeDistinciones  Puntaje de las distinciones académicas.
     * @param puntajeExperiencia   Puntaje de la experiencia laboral o investigativa.
     * @throws UsernameNotFoundException Si no se encuentra ningún aspirante
     *                                   asociado al ID proporcionado.
     * @throws IllegalArgumentException  Si alguna de las calificaciones no es
     *                                   válida.
     * @throws IllegalArgumentException  Si el aspirante no se encuentra en el
     *                                   estado adecuado para calificar los
     *                                   documentos.
     */
    @Override
    public void calificarDocsIndivi(Integer aspiranteId, Integer puntajeCartas, Integer puntajeNotasPregrado,
            Double puntajePublicaciones, Double puntajeDistinciones, Double puntajeExperiencia) {
        Optional<AspiranteEntity> aspiranteEntity = aspiranteRepository.findById(aspiranteId);
        if (!aspiranteEntity.isPresent())
            throw new UsernameNotFoundException("No existe ningun aspirante asociado.");
        AspiranteEntity aspirante = aspiranteEntity.get();

        if (puntajeNotasPregrado < 0 || puntajeNotasPregrado > 20)
            throw new IllegalArgumentException("La calificacion de las notas de pregrado no es valida");
        aspirante.setPuntajeNotas(puntajeNotasPregrado);

        if (puntajeCartas < 0 || puntajeCartas > 15)
            throw new IllegalArgumentException("La calificacion de las cartas de referencia no es valida");
        aspirante.setPuntajeCartasReferencia(puntajeCartas);

        if (puntajeDistinciones < 0 || puntajeDistinciones > 2.5)
            throw new IllegalArgumentException("La calificacion de las distinciones academicas no es valida");
        aspirante.setPuntajeDistincionesAcademicas(puntajeDistinciones);

        if (puntajePublicaciones < 0 || puntajePublicaciones > 5)
            throw new IllegalArgumentException(
                    "La calificacion de las publicaciones y participaciones en congresos no es valida");
        aspirante.setPuntajePublicaciones(puntajePublicaciones);

        if (puntajeExperiencia < 0 || puntajeExperiencia > 2.5)
            throw new IllegalArgumentException("La calificacion de la experiencia laboral  no es valida");
        aspirante.setPuntajeExperienciaLaboral(puntajeExperiencia);

        if (aspirante.getEstado().getId() != 4)
            throw new IllegalArgumentException(
                    "No se puede calificar los documentos porque el aspirante se encuentre en el estado "
                            + aspirante.getEstado().getDescripcion());

        aspiranteRepository.save(aspirante);
        actualizarEstadoAspirante(aspirante);

    }

    /**
     * Califica la entrevista de un aspirante.
     *
     * @param id                     el ID del aspirante.
     * @param calificacionEntrevista la calificación de la entrevista.
     * @throws UsernameNotFoundException si no se encuentra un aspirante con el ID
     *                                   proporcionado.
     * @throws IllegalArgumentException  si la calificación de la entrevista no es
     *                                   válida.
     * @throws EntityNotFoundException   si no se encuentra un enlace y una fecha de
     *                                   entrevista.
     */
    @Override
    public void calificarEntrevistaAspirante(int id, int calificacionEntrevista) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(id);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();
        CohorteEntity cohorteEntity = aspiranteEntity.getCohorte();
        if(!cohorteEntity.getHabilitado()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cohorte no está habilitada");
        }

        if (calificacionEntrevista < 0 || calificacionEntrevista > 15)
            throw new IllegalArgumentException("La calificacion de la entrevista no es valida");

        if (cohorteEntity.getEnlace_entrevista() == null || aspiranteEntity.getFecha_entrevista() == null)
            throw new EntityNotFoundException("No existe un enlace y fecha de entrevista");
        try {
            aspiranteEntity.setPuntaje_entrevista(calificacionEntrevista);
            aspiranteRepository.save(aspiranteEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al calificar la entrevista");
        }
    }

    /**
     * Admite a un aspirante actualizando su estado a "ADMITIDO".
     *
     * @param aspiranteId el ID del aspirante a admitir
     * @throws UsernameNotFoundException si no se encuentra el aspirante
     */
    @Override
    public void admitirAspirante(Integer aspiranteId) {

        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();

        if (aspiranteEntity.getEstado().getId() == 5) {
            aspiranteEntity.setEstado(estadoRepository.findByDescripcion("ADMITIDO"));
            aspiranteRepository.save(aspiranteEntity);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El aspirante no se encuentra en el estado correcto para la admisión.");
        notificacionService.crearNotificacion("Haz sido admitido a la maestria", aspiranteEntity.getId());
    }

    /**
     * Rechaza la admisión de un aspirante dado su ID.
     *
     * @param aspiranteId El ID del aspirante a rechazar la admisión.
     * @throws UsernameNotFoundException si no se encuentra ningún aspirante
     *                                   asociado al ID proporcionado.
     * @throws ResponseStatusException   si el estado del aspirante no es válido
     *                                   para el rechazo de la admisión.
     */
    @Override
    public void rechazarAdmisionAspirante(Integer aspiranteId) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (!aspirante.isPresent())
            throw new UsernameNotFoundException("No existe ningún aspirante asociado.");
        AspiranteEntity aspiranteEntity = aspirante.get();

        if (aspiranteEntity.getEstado().getId() == 6) {
            aspiranteEntity.setEstado(estadoRepository.findById(5).get());
            aspiranteRepository.save(aspiranteEntity);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El aspirante no se encuentra admitido.");
    }

    /**
     * Obtiene una lista de los aspirantes admitidos.
     *
     * @return una lista de objetos AspiranteDTO de los aspirantes admitidos
     */
    @Override
    public List<AspiranteDTO> listarAdmitidos(Integer estadoId) {
        CohorteEntity cohorteEntity = cohorteRepository.findCohorteByHabilitado(true);
        List<AspiranteEntity> aspiranteEntities = new ArrayList<>(cohorteEntity.getAspirantes());
        List<AspiranteDTO> aspiranteDTOs = new ArrayList<>();

        for (AspiranteEntity aspiranteEntity : aspiranteEntities) {
            if (aspiranteEntity.getEstado().getId() == estadoId) {
                AspiranteDTO aspiranteDTO = new AspiranteDTO();
                BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
                aspiranteDTOs.add(aspiranteDTO);
            }
        }

        return aspiranteDTOs;
    }

    /**
     * Mapea una aspirante Entity
     * 
     * @param aspiranteEntity Aspirante entity
     * @return AspiranteDTO
     */
    private AspiranteDTO convertirAspiranteEntityADTO(AspiranteEntity aspiranteEntity) {
        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(aspiranteEntity, aspiranteDTO);
        return aspiranteDTO;
    }

    /**
     * Obtiene una lista de aspirantes históricos para un cohorte específico.
     *
     * @param cohorteId El ID del cohorte para el cual se obtendrán los aspirantes
     *                  históricos.
     * @return Una lista de objetos AspiranteDTO que representan los aspirantes
     *         históricos del cohorte.
     * @throws IllegalArgumentException Si no se encuentra el cohorte con el ID
     *                                  especificado.
     */
    @Override
    public List<AspiranteDTO> obtenerAspirantesHistoricosCohorte(Integer cohorteId) {
        CohorteEntity cohorte = cohorteRepository.findById(cohorteId)
                .orElseThrow(() -> new IllegalArgumentException("Cohorte no encontrado"));

        List<AspiranteEntity> aspirantes = aspiranteRepository.findByCohorte(cohorte);

        List<AspiranteDTO> aspiranteDTOs = new ArrayList<>();
        for (AspiranteEntity aspirante : aspirantes) {
            AspiranteDTO aspiranteDTO = new AspiranteDTO();
            BeanUtils.copyProperties(aspirante, aspiranteDTO);
            aspiranteDTOs.add(aspiranteDTO);
        }
        return aspiranteDTOs;
    }

    /**
     * Asigna el estado de un aspirante a "ENTREVISTA Y PRUEBA" si al aspirante se le han calificado sus documentos
     *
     * @param aspirante
     */
    private void actualizarEstadoAspirante(AspiranteEntity aspirante) {
        if (aspirante.getPuntajeCartasReferencia() > 0 && aspirante.getPuntajeDistincionesAcademicas() > 0
                && aspirante.getPuntajeNotas() > 0 && aspirante.getPuntajeExperienciaLaboral() > 0
                && aspirante.getPuntajePublicaciones() > 0) {
            EstadoEntity estadoEntity = estadoRepository.findById(5).get();
            aspirante.setEstado(estadoEntity);
            aspiranteRepository.save(aspirante);
        }
    }


    public boolean existsByEmail(String email) {
        // Check if the email exists in the database
        return usuarioRepository.existsByEmail(email);
    }
    public AspiranteDTO getAspiranteByEmail(String email) {
        Optional<AspiranteEntity> aspiranteOpt = aspiranteRepository.findByUser_Email(email);
        if (!aspiranteOpt.isPresent()) {
            throw new UsernameNotFoundException("No existe un aspirante asociado con el email: " + email);
        }
        AspiranteEntity aspirante = aspiranteOpt.get();
        AspiranteDTO aspiranteDTO = new AspiranteDTO();
        BeanUtils.copyProperties(aspirante, aspiranteDTO);
        return aspiranteDTO;
    }

    public List<AspiranteDTO> getAllAspirantes() {
        List<AspiranteEntity> aspirantes = aspiranteRepository.findAll();
        return aspirantes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AspiranteDTO editAspirante(Integer id, AspiranteDTO aspiranteDTO) {
        // Fetch the existing aspirant record by ID
        AspiranteEntity existingAspirante = aspiranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aspirante not found"));

        // Update fields based on the provided DTO
        existingAspirante.setNombre(aspiranteDTO.getNombre());
        existingAspirante.setApellido(aspiranteDTO.getApellido());
        existingAspirante.setGenero(aspiranteDTO.getGenero());
        existingAspirante.setLugar_nac(aspiranteDTO.getLugar_nac());
        existingAspirante.setFecha_exp_di(aspiranteDTO.getFecha_exp_di());
        existingAspirante.setFecha_nac(aspiranteDTO.getFecha_nac());
        existingAspirante.setNo_documento(aspiranteDTO.getNo_documento());
        existingAspirante.setCorreoPersonal(aspiranteDTO.getCorreoPersonal());
        existingAspirante.setDepartamento_residencia(aspiranteDTO.getDepartamento_residencia());
        existingAspirante.setMunicipio_residencia(aspiranteDTO.getMunicipio_residencia());
        existingAspirante.setDireccion_residencia(aspiranteDTO.getDireccion_residencia());
        existingAspirante.setTelefono(aspiranteDTO.getTelefono());
        existingAspirante.setEmpresa_trabajo(aspiranteDTO.getEmpresa_trabajo());
        existingAspirante.setDepartamento_trabajo(aspiranteDTO.getDepartamento_trabajo());
        existingAspirante.setMunicipio_trabajo(aspiranteDTO.getMunicipio_trabajo());
        existingAspirante.setDireccion_trabajo(aspiranteDTO.getDireccion_trabajo());
        existingAspirante.setEstudios_pregrado(aspiranteDTO.getEstudios_pregrado());
        existingAspirante.setEstudios_posgrados(aspiranteDTO.getEstudios_posgrados());
        existingAspirante.setExp_laboral(aspiranteDTO.getExp_laboral());
        existingAspirante.setEs_egresado_ufps(aspiranteDTO.getEs_egresado_ufps());
        existingAspirante.setPuntajeNotas(aspiranteDTO.getPuntajeNotas());
        existingAspirante.setPuntajeDistincionesAcademicas(aspiranteDTO.getPuntajeDistincionesAcademicas());
        existingAspirante.setPuntajeExperienciaLaboral(aspiranteDTO.getPuntajeExperienciaLaboral());
        existingAspirante.setPuntajePublicaciones(aspiranteDTO.getPuntajePublicaciones());
        existingAspirante.setPuntajeCartasReferencia(aspiranteDTO.getPuntajeCartasReferencia());
        existingAspirante.setPuntaje_entrevista(aspiranteDTO.getPuntaje_entrevista());
        existingAspirante.setPuntaje_prueba(aspiranteDTO.getPuntaje_prueba());
        existingAspirante.setFecha_entrevista(aspiranteDTO.getFecha_entrevista());

        // Save the updated aspirant record
        AspiranteEntity updatedAspirante = aspiranteRepository.save(existingAspirante);

        // Convert the updated entity back to DTO and return it
        return convertToDTO(updatedAspirante);
    }

    @Override
    public void deleteAspirante(Integer id) {
        // Fetch the existing aspirant record by ID
        AspiranteEntity aspirante = aspiranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aspirante not found"));

        // Delete the aspirant record
        aspiranteRepository.delete(aspirante);
    }

    // In AspiranteService.java (or AspiranteServiceInterface)

    private AspiranteDTO convertToDTO(AspiranteEntity aspirante) {
        return new AspiranteDTO(
                aspirante.getId(), aspirante.getCohorte().getId(), aspirante.getNombre(), aspirante.getApellido(), aspirante.getGenero(), aspirante.getLugar_nac(), aspirante.getFecha_exp_di(), aspirante.getFecha_nac(), aspirante.getNo_documento(), aspirante.getCorreoPersonal(), aspirante.getDepartamento_residencia(), aspirante.getMunicipio_residencia(), aspirante.getDireccion_residencia(), aspirante.getTelefono(), aspirante.getEmpresa_trabajo(), aspirante.getDepartamento_trabajo(), aspirante.getMunicipio_trabajo(), aspirante.getDireccion_trabajo(), aspirante.getEstudios_pregrado(), aspirante.getEstudios_posgrados(), aspirante.getExp_laboral(), aspirante.getEs_egresado_ufps(), aspirante.getTotal(), // Assuming getTotal() calculates the total score
                aspirante.getPuntajeNotas(),
                aspirante.getPuntajeDistincionesAcademicas(),
                aspirante.getPuntajeExperienciaLaboral(),
                aspirante.getPuntajePublicaciones(),
                aspirante.getPuntajeCartasReferencia(),
                aspirante.getPuntaje_entrevista(),
                aspirante.getPuntaje_prueba(),
                aspirante.getFecha_entrevista(), // Converting LocalDateTime to LocalDate
                aspirante.getEstado().getId() // Assuming EstadoEntity has an getId method
        );
    }


    @Transactional
    public void cambiarEstado(AspiranteEntity aspirante, EstadoEntity nuevoEstado, String responsable, String comentario) {
        // Cambiamos el estado actual del aspirante
        aspirante.setEstado(nuevoEstado);
        aspiranteRepository.save(aspirante);

        // Guardamos el cambio en el historial
        EstadoHistorialEntity historial = new EstadoHistorialEntity();
        historial.setAspirante(aspirante);
        historial.setEstado(nuevoEstado);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setResponsable(responsable);
        historial.setComentario(comentario);

        estadoHistorialRepository.save(historial);
    }
    @Override
    public List<AspiranteDTO> getAspirantesByCohorte(Integer cohorteId) {
        return aspiranteRepository.findByCohorteId(cohorteId).stream()
                .map(this::convertToDTOs) // Assuming you have a method to convert entity to DTO
                .collect(Collectors.toList());
    }

    private AspiranteDTO convertToDTOs(AspiranteEntity aspirante) {
        // Conversion logic here
        return new AspiranteDTO(
                aspirante.getId(),
                aspirante.getCohorte().getId(),
                aspirante.getNombre(),
                aspirante.getApellido(),
                aspirante.getGenero(),
                aspirante.getLugar_nac(),
                aspirante.getFecha_exp_di(),
                aspirante.getFecha_nac(),
                aspirante.getNo_documento(),
                aspirante.getCorreoPersonal(),
                aspirante.getDepartamento_residencia(),
                aspirante.getMunicipio_residencia(),
                aspirante.getDireccion_residencia(),
                aspirante.getTelefono(),
                aspirante.getEmpresa_trabajo(),
                aspirante.getDepartamento_trabajo(),
                aspirante.getMunicipio_trabajo(),
                aspirante.getDireccion_trabajo(),
                aspirante.getEstudios_pregrado(),
                aspirante.getEstudios_posgrados(),
                aspirante.getExp_laboral(),
                aspirante.getEs_egresado_ufps(),
                aspirante.getTotal(),
                aspirante.getPuntajeNotas(),
                aspirante.getPuntajeDistincionesAcademicas(),
                aspirante.getPuntajeExperienciaLaboral(),
                aspirante.getPuntajePublicaciones(),
                aspirante.getPuntajeCartasReferencia(),
                aspirante.getPuntaje_entrevista(),
                aspirante.getPuntaje_prueba(),
                aspirante.getFecha_entrevista(),
                aspirante.getEstado().getId() // Assuming EstadoEntity has a getId method
        );
    }
    public boolean cambiarEsEgresado(Integer aspiranteId) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (aspirante.isPresent()) {
            AspiranteEntity aspiranteEntity = aspirante.get();
            aspiranteEntity.setEs_egresado_ufps(false); // Set to false
            aspiranteRepository.save(aspiranteEntity);
            return true;
        }
        return false;
    }

    public boolean rechazarAdmision(Integer aspiranteId) {
        Optional<AspiranteEntity> aspirante = aspiranteRepository.findById(aspiranteId);
        if (aspirante.isPresent()) {
            AspiranteEntity aspiranteEntity = aspirante.get();

            // Retrieve the EstadoEntity corresponding to "RECHAZADO"
            EstadoEntity rechazadoEstado = estadoRepository.findByDescripcion("RECHAZADO");

            aspiranteEntity.setEstado(rechazadoEstado);  // Set the retrieved EstadoEntity
            aspiranteRepository.save(aspiranteEntity);
            return true;
        }
        return false;
    }



}
