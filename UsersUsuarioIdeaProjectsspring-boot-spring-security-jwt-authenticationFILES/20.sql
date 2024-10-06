INSERT INTO aspirante (
    cohorte_id, user_id, nombre, apellido, genero, lugar_nac, fecha_exp_di, fecha_nac, no_documento, 
    correo_personal, departamento_residencia, municipio_residencia, direccion_residencia, telefono, 
    empresa_trabajo, departamento_trabajo, municipio_trabajo, direccion_trabajo, estudios_pregrado, 
    estudios_posgrados, exp_laboral, es_egresado_ufps, estado_id, puntaje_notas, puntaje_distinciones_academicas, 
    puntaje_experiencia_laboral, puntaje_publicaciones, puntaje_cartas_referencia, puntaje_entrevista, puntaje_prueba, fecha_entrevista
) 
VALUES 
(
    1, 2, 'Carlos', 'González', 'Masculino', 'Cali', '2019-12-01', '1985-05-20', '987654321', 
    'carlosg@example.com', 'Valle del Cauca', 'Cali', 'Carrera 50 #25-30', '3019876543', 
    'Empresa ABC', 'Valle del Cauca', 'Cali', 'Avenida 60 #20-40', 'Ingeniería Industrial', 
    'Maestría en Logística', '8 años de experiencia', true, 1, 85, 3.8, 4.0, 1.5, 4, 12, 18, '2024-08-10 11:00:00'
);
